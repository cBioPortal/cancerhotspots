package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.tsv.TsvWriter;
import org.cmo.cancerhotspots.domain.*;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.service.DataImportService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotDataImportService implements DataImportService
{
    private String variantFileUri;
    @Value("${hotspot.variant.uri}")
    public void setVariantFileUri(String variantFileUri)
    {
        this.variantFileUri = variantFileUri;
    }

    private String hotspotMutationUri;
    @Value("${hotspot.mutation.uri}")
    public void setHotspotMutationUri(String hotspotMutationUri) { this.hotspotMutationUri = hotspotMutationUri; }

    // cache of <amino acid change, tumor type composition> pairs
    private Map<String, TumorTypeComposition> variantCacheByAAChange;
    // cache of <hugo symbol + amino acid change, tumor type composition> pairs
    private Map<String, TumorTypeComposition> variantCacheByGeneAndAAChange;
    // cache of <hugo symbol + codon, variant composition> pairs
    private Map<String, VariantComposition> variantCacheByGeneAndCodon;

    private MutationAnnotationService mafService;

    @Autowired
    public HotspotDataImportService(MutationAnnotationService mafService)
    {
        this.mafService = mafService;
        // TODO technically we should use database for such large data, not in-memory cache
        this.variantCacheByGeneAndAAChange = null;
        this.variantCacheByAAChange = null;
        this.variantCacheByGeneAndCodon = null;
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

    public VariantComposition getVariantComposition(String hugoSymbol, String codon)
    {
        if (this.variantCacheByGeneAndCodon == null)
        {
            this.variantCacheByGeneAndCodon = constructVariantCacheByGeneAndCodon();
        }

        return variantCacheByGeneAndCodon.get(
            (hugoSymbol + "_" + codon).toUpperCase());
    }

    @Override
    public void createVariantFile(List<HotspotMutation> hotspotMutations)
    {
        if (this.variantCacheByGeneAndAAChange == null)
        {
            this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange();
        }

        TsvWriter writer = FileIO.initTsvWriter(
            new BeanWriterProcessor<>(TumorTypeComposition.class),
            FileIO.getWriter(variantFileUri));

        writer.writeHeaders();

        for (HotspotMutation mutation : hotspotMutations)
        {
            if (mutation.getVariantAminoAcid() == null)
            {
                // TODO log a warning message
                continue;
            }

            for (String variant: mutation.getVariantAminoAcid().keySet())
            {
                String aminoAcidChange = mutation.getCodon() + variant;
                TumorTypeComposition composition = this.getTumorTypeComposition(
                    mutation.getHugoSymbol(), aminoAcidChange);

                // skip unknown/invalid variants
                if (composition == null)
                {
                    continue;
                }

                writer.processRecord(composition);
            }
        }

        writer.close();
    }

    @Override
    public void createHotspotFile(List<HotspotMutation> hotspotMutations)
    {
        TsvWriter writer = FileIO.initTsvWriter(
            new BeanWriterProcessor<>(HotspotMutation.class),
            FileIO.getWriter(hotspotMutationUri));

        writer.writeHeaders();

        for (HotspotMutation mutation : hotspotMutations)
        {
            writer.processRecord(mutation);
        }

        writer.close();
    }

    @Override
    public void generateVariantComposition(List<HotspotMutation> hotspotMutations)
    {
        if (this.variantCacheByGeneAndCodon == null)
        {
            this.variantCacheByGeneAndCodon = constructVariantCacheByGeneAndCodon();
        }

        for (HotspotMutation mutation : hotspotMutations)
        {
            VariantComposition composition = this.getVariantComposition(
                mutation.getHugoSymbol(), mutation.getCodon());

            // skip unknown/invalid variants
            if (composition == null)
            {
                continue;
            }

            mutation.setVariantAminoAcid(composition.getVariantComposition());
        }
    }

    @Override
    public void generateTumorTypeComposition(List<HotspotMutation> hotspotMutations)
    {
        if (this.variantCacheByGeneAndCodon == null)
        {
            this.variantCacheByGeneAndCodon = constructVariantCacheByGeneAndCodon();
        }

        for (HotspotMutation mutation : hotspotMutations)
        {
            // get variant composition for each hotspot mutations
            VariantComposition variantComposition = this.getVariantComposition(
                mutation.getHugoSymbol(), mutation.getCodon());

            // skip unknown/invalid variants
            if (variantComposition == null)
            {
                continue;
            }

            // TODO a method to get tumor type composition by gene and codon would be useful here
            TumorTypeComposition composition = new TumorTypeComposition();

            // for each variant get tumor type composition and combine them into a single
            // tumor type composition for this codon
            for (String variant: variantComposition.getVariantComposition().keySet())
            {
                String aminoAcidChange = mutation.getCodon() + variant;
                TumorTypeComposition tumorTypeComposition = this.getTumorTypeComposition(
                    mutation.getHugoSymbol(), aminoAcidChange);

                // skip unknown/invalid variants
                if (tumorTypeComposition != null)
                {
                    // merge all tumor type compositions into one!
                    composition.merge(tumorTypeComposition);
                }
            }

            mutation.setTumorTypeComposition(composition.getTumorTypeComposition());
        }
    }

    private Map<String, VariantComposition> constructVariantCacheByGeneAndCodon()
    {
        Map<String, VariantComposition> variantCache = new HashMap<>();
        List<MutationAnnotation> annotations = mafService.getAllMutationAnnotations();

        for (MutationAnnotation annotation : annotations)
        {
            String codon = this.extractCodon(annotation);

            if (codon != null)
            {
                String key = (annotation.getHugoSymbol() + "_" + codon).toUpperCase();
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
            variantComposition.setCodon(this.extractCodon(annotation));
            variantComposition.setHugoSymbol(annotation.getHugoSymbol());

            variantCache.put(key, variantComposition);
        }

        variantComposition.updateVariantComposition(annotation.getVariantAminoAcid());

        return variantComposition;
    }

	/**
     * Creates a codon string by using the reference amino acid
     * and amino acid position values of the given annotation.
     *
     * @param annotation mutation annotation instance
     * @return codon string
     */
    private String extractCodon(MutationAnnotation annotation)
    {
        String codon = null;

        if (annotation.getReferenceAminoAcid() != null &&
            annotation.getAminoAcidPosition() != null)
        {
            codon = annotation.getReferenceAminoAcid() +
                    annotation.getAminoAcidPosition();
        }

        return codon;
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
