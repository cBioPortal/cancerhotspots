package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.data.IntegerRange;
import org.cmo.cancerhotspots.model.*;
import org.cmo.cancerhotspots.persistence.*;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.service.DataImportService;
import org.cmo.cancerhotspots.service.MutationFilterService;
import org.cmo.cancerhotspots.util.DataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotDataImportService implements DataImportService
{
    // define the logger object for this class
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // cache of <amino acid change, tumor type composition> pairs
    private Map<String, TumorTypeComposition> variantCacheByAAChange;
    // cache of <hugo symbol + amino acid change, tumor type composition> pairs
    private Map<String, TumorTypeComposition> variantCacheByGeneAndAAChange;
    // cache of <hugo symbol + residue, variant composition> pairs
    private Map<String, VariantComposition> variantCacheByGeneAndResidue;
    // cache of <cluster id, Cluster> pairs
    private Map<String, Cluster> clusterCacheById;

    private MutationAnnotationService mafService;
    private MutationFilterService filterService;
    private MutationRepository mutationRepository;
    private VariantRepository variantRepository;
    private ClusterRepository clusterRepository;
    private TranscriptRepository transcriptRepository;

    @Autowired
    public HotspotDataImportService(MutationAnnotationService mafService,
        MutationFilterService filterService,
        MutationRepository mutationRepository,
        VariantRepository variantRepository,
        ClusterRepository clusterRepository,
        TranscriptRepository transcriptRepository)
    {
        this.mafService = mafService;
        this.filterService = filterService;
        this.mutationRepository = mutationRepository;
        this.variantRepository = variantRepository;
        this.clusterRepository = clusterRepository;
        this.transcriptRepository = transcriptRepository;
        // TODO technically we should use database for such large data, not in-memory cache
        this.variantCacheByGeneAndAAChange = null;
        this.variantCacheByAAChange = null;
        this.variantCacheByGeneAndResidue = null;
        this.clusterCacheById = null;
    }

    public TumorTypeComposition getTumorTypeComposition(Mutation mutation)
    {
        // assuming that there is only one variant amino acid
        String variant = mutation.getVariantAminoAcid().keySet().iterator().next();

        // construct the tumor type instance
        TumorTypeComposition composition = new TumorTypeComposition();
        String residue = DataUtils.mutationResidue(mutation.getAminoAcidPosition(),
                                                   mutation.mostFrequentReference(),
                                                   mutation.getIndelSize());

        composition.setHugoSymbol(mutation.getHugoSymbol());
        composition.setAminoAcidPosition(mutation.getAminoAcidPosition());
        composition.setReferenceAminoAcid(mutation.mostFrequentReference());
        composition.setVariantAminoAcid(variant);
        composition.setTumorTypeCompositionMap(mutation.getTumorTypeComposition());
        composition.setResidue(residue);

        return composition;
    }

    public TumorTypeComposition getTumorTypeComposition(String aminoAcidChange)
    {
        if (this.variantCacheByAAChange == null)
        {
            this.variantCacheByAAChange = constructVariantCacheByAAChange();
        }

        return variantCacheByAAChange.get(aminoAcidChange.toUpperCase());
    }

    public TumorTypeComposition getTumorTypeComposition(String hugoSymbol, String aminoAcidChange)
    {
        if (this.variantCacheByGeneAndAAChange == null)
        {
            this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange();
        }

        return variantCacheByGeneAndAAChange.get(
            (hugoSymbol + "_" + aminoAcidChange).toUpperCase());
    }

    public VariantComposition getVariantComposition(String hugoSymbol, String residue)
    {
        if (this.variantCacheByGeneAndResidue == null)
        {
            this.variantCacheByGeneAndResidue = constructVariantCacheByGeneAndResidue();
        }

        return variantCacheByGeneAndResidue.get(
            (hugoSymbol + "_" + residue).toUpperCase());
    }

    @Override
    public void createVariantFile(Iterable<Mutation> mutations)
    {
        List<TumorTypeComposition> compositions = new ArrayList<>();

        for (Mutation mutation : mutations)
        {
            if (mutation.getVariantAminoAcid() == null)
            {
                // TODO log a warning message
                continue;
            }

            int variantCount = mutation.getVariantAminoAcid().keySet().size();

            for (String variant: mutation.getVariantAminoAcid().keySet())
            {
                String aminoAcidChange = mutation.getResidue() + variant;
                TumorTypeComposition composition;

                // do not use the MAF file if there is only one variant and
                // the tumor type composition field already exist
                if (variantCount == 1 &&
                    mutation.getTumorTypeComposition() != null &&
                    mutation.getTumorTypeComposition().size() > 0)
                {
                    composition = this.getTumorTypeComposition(mutation);
                }
                else
                {
                    composition = this.getTumorTypeComposition(
                        mutation.getHugoSymbol(), aminoAcidChange);
                }

                // skip unknown/invalid variants
                if (composition == null)
                {
                    continue;
                }

                compositions.add(composition);
            }
        }

        variantRepository.saveAll(compositions);
    }

    @Override
    public void createClusterFile(Iterable<Mutation> mutations)
    {
        if (clusterCacheById == null)
        {
            clusterCacheById = constructClusterCache(mutations);
        }

        clusterRepository.saveAll(clusterCacheById.values());
    }

    @Override
    public void createHotspotFile(Iterable<Mutation> mutations)
    {
        // merge mutations by residue and save to the repository
        Iterable<Mutation> merged = this.mergeByGeneAndResidue(mutations);
        mutationRepository.saveAll(merged);
    }

    @Override
    public void updateHotspotsFile(Iterable<Mutation> mutations)
    {
        this.updateResidueAndPosition(mutations);
        mutationRepository.saveAll(mutations);
    }

    @Override
    public void generateVariantComposition(Iterable<Mutation> mutations)
    {
        for (Mutation mutation : mutations)
        {
            VariantComposition composition = this.getVariantComposition(
                mutation.getHugoSymbol(), mutation.getResidue());

            // skip unknown/invalid variants
            if (composition == null)
            {
                continue;
            }

            mutation.setVariantAminoAcid(composition.getVariantComposition());
        }
    }

    @Override
    public void generateTumorTypeComposition(Iterable<Mutation> mutations)
    {
        for (Mutation mutation : mutations)
        {
            // get variant composition for each hotspot mutations
            VariantComposition variantComposition = this.getVariantComposition(
                mutation.getHugoSymbol(), mutation.getResidue());

            // skip unknown/invalid variants
            if (variantComposition == null)
            {
                continue;
            }

            // TODO a method to get tumor type composition by gene and residue would be useful here
            TumorTypeComposition composition = new TumorTypeComposition();

            // for each variant get tumor type composition and combine them into a single
            // tumor type composition for this residue
            for (String variant: variantComposition.getVariantComposition().keySet())
            {
                String aminoAcidChange = mutation.getResidue() + variant;
                TumorTypeComposition tumorTypeComposition = this.getTumorTypeComposition(
                    mutation.getHugoSymbol(), aminoAcidChange);

                // skip unknown/invalid variants
                if (tumorTypeComposition != null)
                {
                    // merge all tumor type compositions into one!
                    composition.merge(tumorTypeComposition);
                }
            }

            if (mutation.getTumorCount() != null &&
                !mutation.getTumorCount().equals(composition.tumorCount())) {
                log.debug("Tumor Count Mismatch: " +
                          mutation.getHugoSymbol() + "\t" +
                          mutation.getResidue() + "\t" +
                          mutation.getTumorCount() + "\t" +
                          composition.tumorCount());
            }

            mutation.setTumorTypeComposition(composition.getTumorTypeCompositionMap());
            mutation.setTumorTypeCount(composition.tumorTypeCount());
            mutation.setTumorCount(composition.tumorCount());
        }
    }

    @Override
    public void importTranscript(Iterable<Mutation> mutations)
    {
        for (Mutation mutation : mutations)
        {
            Iterable<Transcript> transcripts = transcriptRepository.findByGene(mutation.getHugoSymbol());

            if (transcripts.iterator().hasNext())
            {
                mutation.setTranscriptId(
                    transcripts.iterator().next().getTranscriptId());
            }
        }
    }

    private Iterable<Mutation> updateResidueAndPosition(Iterable<Mutation> mutations)
    {
        for (Mutation mutation : mutations)
        {
            // set residue if null
            if (mutation.getResidue() == null &&
                mutation.getAminoAcidPosition() != null)
            {
                mutation.setResidue(DataUtils.mutationResidue(
                    mutation.getAminoAcidPosition(),
                    mutation.mostFrequentReference(),
                    mutation.getIndelSize()));
            }

            // set position if null
            if (mutation.getAminoAcidPosition() == null &&
                mutation.getResidue() != null)
            {
                mutation.setAminoAcidPosition(
                    DataUtils.aminoAcidPosition(mutation.getResidue()));
            }
        }

        return mutations;
    }

    private Iterable<Mutation> mergeByGeneAndResidue(Iterable<Mutation> mutations)
    {
        Map<String, List<Mutation>> map = new LinkedHashMap<>();
        List<Mutation> mergedMutations = new ArrayList<>();

        // first, index mutations by residue
        for (Mutation mutation: mutations)
        {
            String gene = mutation.getHugoSymbol();
            String residue = mutation.getResidue();
            IntegerRange position = mutation.getAminoAcidPosition();

            // set residue if null
            if (residue == null &&
                position != null)
            {
                // update local reference
                residue = DataUtils.mutationResidue(position,
                    mutation.mostFrequentReference(),
                    mutation.getIndelSize());

                mutation.setResidue(residue);
            }

            // if residue is still null, then nothing to do...
            if (residue != null)
            {
                String key = (gene + "_" + residue).toUpperCase();
                List<Mutation> list = map.get(key);

                if (list == null)
                {
                    list = new LinkedList<>();
                    map.put(key, list);
                }

                list.add(mutation);
            }
        }


        // second, merge mutations by residue:
        // merge tumor type composition and variant amino acid values,
        // assuming all other values are identical
        for (String key: map.keySet())
        {
            Mutation merged = null;
            List<Mutation> list = map.get(key);

            for (Mutation mutation: list)
            {
                if (merged == null)
                {
                    merged = mutation;
                }
                else
                {
                    DataUtils.mergeCompositions(merged.getTumorTypeComposition(),
                                                mutation.getTumorTypeComposition());

                    DataUtils.mergeCompositions(merged.getVariantAminoAcid(),
                                                mutation.getVariantAminoAcid());
                }
            }

            if (merged != null)
            {
                // also calculate tumor count value if null
                if (merged.getTumorCount() == null)
                {
                    Integer tumorCount = 0;

                    for (Integer value: merged.getTumorTypeComposition().values())
                    {
                        tumorCount += value;
                    }

                    merged.setTumorCount(tumorCount);
                }

                mergedMutations.add(merged);
            }
        }

        return mergedMutations;
    }

    private Map<String, Cluster> constructClusterCache(Iterable<Mutation> mutations)
    {
        if (mutations == null)
        {
            mutations = mutationRepository.findAll();
        }

        Map<String, Cluster> clusterMap = new LinkedHashMap<>();

        // index Cluster instances
        for (Mutation mutation : mutations)
        {
            String key = mutation.getCluster();

            Cluster cluster = clusterMap.get(key);

            if (cluster == null)
            {
                cluster = new Cluster();

                cluster.setClusterId(key);
                cluster.setPdbChainMap(mutation.getPdbChains());
                cluster.setpValue(mutation.getpValue());
                cluster.setHugoSymbol(mutation.getHugoSymbol());

                clusterMap.put(key, cluster);
            }

            cluster.addResidue(mutation.getResidue(), mutation.getTumorCount());
        }

        return clusterMap;
    }

    private Map<String, VariantComposition> constructVariantCacheByGeneAndResidue()
    {
        Map<String, VariantComposition> variantCache = new HashMap<>();
        List<MutationAnnotation> annotations = mafService.getAllMutationAnnotations();

        for (MutationAnnotation annotation : annotations)
        {
            String residue = this.extractResidue(annotation);

            // TODO should we also apply this filter for all import methods for consistency?
            // skip the annotation if the mutation type is filtered out,
            // or if no residue information can be extracted
            if (this.filterService.filterByType(annotation) &&
                residue != null)
            {
                String key = (annotation.getHugoSymbol() + "_" + residue).toUpperCase();
                this.updateVariant(variantCache, key, annotation);
            }
        }

        return variantCache;
    }

    private Map<String, TumorTypeComposition> constructVariantCacheByGeneAndAAChange()
    {
        Map<String, TumorTypeComposition> variantCache = new HashMap<>();
        List<MutationAnnotation> annotations = mafService.getAllMutationAnnotations();

        for (MutationAnnotation annotation : annotations)
        {
            String aaChange = annotation.getReferenceAminoAcid() +
                              annotation.getAminoAcidPosition() +
                              annotation.getVariantAminoAcid();

            String key = (annotation.getHugoSymbol() + "_" + aaChange).toUpperCase();
            this.updateTumorType(variantCache, key, annotation);
        }

        return variantCache;
    }

    private Map<String, TumorTypeComposition> constructVariantCacheByAAChange()
    {
        Map<String, TumorTypeComposition> variantCache = new HashMap<>();
        List<MutationAnnotation> annotations = mafService.getAllMutationAnnotations();

        for (MutationAnnotation annotation : annotations)
        {
            String aaChange = annotation.getReferenceAminoAcid() +
                              annotation.getAminoAcidPosition() +
                              annotation.getVariantAminoAcid();

            String key = aaChange.toUpperCase();
            this.updateTumorType(variantCache, key, annotation);
        }

        return variantCache;
    }

    private TumorTypeComposition updateTumorType(Map<String, TumorTypeComposition> variantCache,
        String key,
        MutationAnnotation annotation)
    {
        TumorTypeComposition tumorTypeComposition = variantCache.get(key);

        if (tumorTypeComposition == null)
        {
            tumorTypeComposition = new TumorTypeComposition();

            // init variant
            tumorTypeComposition.setAminoAcidPosition(
                new IntegerRange(annotation.getAminoAcidPosition()));
            tumorTypeComposition.setReferenceAminoAcid(annotation.getReferenceAminoAcid());
            tumorTypeComposition.setVariantAminoAcid(annotation.getVariantAminoAcid());
            tumorTypeComposition.setHugoSymbol(annotation.getHugoSymbol());

            variantCache.put(key, tumorTypeComposition);
        }

        tumorTypeComposition.updateTumorTypeComposition(
            this.stripTumorType(annotation.getTumorType()));

        return tumorTypeComposition;
    }

    private VariantComposition updateVariant(Map<String, VariantComposition> variantCache,
        String key,
        MutationAnnotation annotation)
    {
        VariantComposition variantComposition = variantCache.get(key);

        if (variantComposition == null)
        {
            variantComposition = new VariantComposition();

            // init variant
            variantComposition.setAminoAcidPosition(new IntegerRange(
                annotation.getAminoAcidPosition()));
            variantComposition.setReferenceAminoAcid(annotation.getReferenceAminoAcid());
            variantComposition.setResidue(this.extractResidue(annotation));
            variantComposition.setHugoSymbol(annotation.getHugoSymbol());

            variantCache.put(key, variantComposition);
        }

        variantComposition.updateVariantComposition(annotation.getVariantAminoAcid());

        return variantComposition;
    }

	/**
     * Creates a residue string by using the reference amino acid
     * and amino acid position values of the given annotation.
     *
     * @param annotation mutation annotation instance
     * @return residue string
     */
    private String extractResidue(MutationAnnotation annotation)
    {
        String residue = null;

        if (annotation.getReferenceAminoAcid() != null &&
            annotation.getAminoAcidPosition() != null)
        {
            residue = annotation.getReferenceAminoAcid() +
                    annotation.getAminoAcidPosition();
        }

        return residue;
    }

	/**
     * Removes the part after the underscore character fot
     * the given tumor type value.
     *
     * @param tumorType tumor type value
     * @return basic tumor type value
     */
    private String stripTumorType(String tumorType)
    {
        if (tumorType == null || tumorType.length() == 0)
        {
            return tumorType;
        }

        return tumorType.split("_")[0];
    }
}
