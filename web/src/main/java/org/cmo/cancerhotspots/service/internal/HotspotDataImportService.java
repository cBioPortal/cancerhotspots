package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.domain.*;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.service.DataImportService;
import org.cmo.cancerhotspots.service.MutationFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private MutationAnnotationService mafService;
    private MutationFilterService filterService;
    private MutationRepository mutationRepository;
    private VariantRepository variantRepository;

    @Autowired
    public HotspotDataImportService(MutationAnnotationService mafService,
        MutationFilterService filterService,
        MutationRepository mutationRepository,
        VariantRepository variantRepository)
    {
        this.mafService = mafService;
        this.filterService = filterService;
        this.mutationRepository = mutationRepository;
        this.variantRepository = variantRepository;
        // TODO technically we should use database for such large data, not in-memory cache
        this.variantCacheByGeneAndAAChange = null;
        this.variantCacheByAAChange = null;
        this.variantCacheByGeneAndResidue = null;
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
    public void createVariantFile(List<Mutation> mutations)
    {
        List<TumorTypeComposition> compositions = new ArrayList<>();

        for (Mutation mutation : mutations)
        {
            if (mutation.getVariantAminoAcid() == null)
            {
                // TODO log a warning message
                continue;
            }

            for (String variant: mutation.getVariantAminoAcid().keySet())
            {
                String aminoAcidChange = mutation.getResidue() + variant;
                TumorTypeComposition composition = this.getTumorTypeComposition(
                    mutation.getHugoSymbol(), aminoAcidChange);

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
    public void createHotspotFile(List<Mutation> mutations)
    {
        mutationRepository.saveAll(mutations);
    }

    @Override
    public void generateVariantComposition(List<Mutation> mutations)
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
    public void generateTumorTypeComposition(List<Mutation> mutations)
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

            mutation.setTumorTypeComposition(composition.getTumorTypeComposition());
            mutation.setTumorTypeCount(composition.tumorTypeCount());
            mutation.setTumorCount(composition.tumorCount());
        }
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
            tumorTypeComposition.setAminoAcidPosition(annotation.getAminoAcidPosition());
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
            variantComposition.setAminoAcidPosition(annotation.getAminoAcidPosition());
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
