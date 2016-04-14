package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.domain.VariantComposition;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.service.VariantService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotVariantService implements VariantService
{
    private String variantFileUri;
    @Value("${hotspot.variant.uri}")
    public void setVariantFileUri(String variantFileUri)
    {
        this.variantFileUri = variantFileUri;
    }

    // cache of <amino acid change, variant> pairs
    private Map<String, VariantComposition> variantCacheByAAChange;
    private Map<String, VariantComposition> variantCacheByGeneAndAAChange;

    private MutationAnnotationService mafService;

    @Autowired
    public HotspotVariantService(MutationAnnotationService mafService)
    {
        this.mafService = mafService;
        this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange();

        // TODO disable for now to reduce memory usage
        // technically we should use database for such large data
        //this.variantCacheByAAChange = constructVariantCacheByAAChange();
        this.variantCacheByAAChange = Collections.emptyMap();
    }

    @Override
    public VariantComposition getVariantComposition(String aminoAcidChange)
    {
        return variantCacheByAAChange.get(aminoAcidChange.toUpperCase());
    }

    @Override
    public VariantComposition getVariantComposition(String hugoSymbol, String aminoAcidChange)
    {
        return variantCacheByGeneAndAAChange.get(
            (hugoSymbol + "_" + aminoAcidChange).toUpperCase());
    }

    @Override
    public void createVariantFile(List<HotspotMutation> hotspotMutations)
    {
        Writer writer = FileIO.getWriter(variantFileUri);

        // TODO use CSV parser to write the data!
        try {
            writer.write("Hugo_Symbol\t" +
                         "Reference_Amino_Acid\t" +
                         "Amino_Acid_Position\t" +
                         "Variant_Amino_Acid\t" +
                         "Tumor_Type_Composition\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (HotspotMutation mutation : hotspotMutations)
        {
            for (String variant: mutation.getVariantAminoAcid().keySet())
            {
                String aminoAcidChange = mutation.getCodon() + variant;
                String key = mutation.getHugoSymbol() + "_" + aminoAcidChange;
                key = key.toUpperCase();
                VariantComposition composition = variantCacheByGeneAndAAChange.get(key);

                // skip unknown/invalid variants
                if (composition == null)
                {
                    continue;
                }

                try {
                    writer.write(mutation.getHugoSymbol() + "\t" +
                                 composition.getReferenceAminoAcid() + "\t" +
                                 composition.getAminoAcidPosition() + "\t" +
                                 composition.getVariantAminoAcid() + "\t" +
                                 composition.getTumorTypeComposition() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, VariantComposition> constructVariantCacheByGeneAndAAChange()
    {
        Map<String, VariantComposition> variantCache = new HashMap<>();
        // TODO do not use maf service if the variant file already exists!
        List<MutationAnnotation> annotations = mafService.getAllMutationAnnotations();

        for (MutationAnnotation annotation : annotations)
        {
            String aaChange = annotation.getReferenceAminoAcid() +
                              annotation.getAminoAcidPosition() +
                              annotation.getVariantAminoAcid();

            String key = (annotation.getHugoSymbol() + "_" + aaChange).toUpperCase();
            this.updateVariant(variantCache, key, annotation);
        }

        return variantCache;
    }

    private Map<String, VariantComposition> constructVariantCacheByAAChange()
    {
        Map<String, VariantComposition> variantCache = new HashMap<>();
        // TODO do not use maf service if the variant file already exists!
        List<MutationAnnotation> annotations = mafService.getAllMutationAnnotations();

        for (MutationAnnotation annotation : annotations)
        {
            String aaChange = annotation.getReferenceAminoAcid() +
                              annotation.getAminoAcidPosition() +
                              annotation.getVariantAminoAcid();

            String key = aaChange.toUpperCase();
            this.updateVariant(variantCache, key, annotation);
        }

        return variantCache;
    }

    private void updateVariant(Map<String, VariantComposition> variantCache,
        String key,
        MutationAnnotation annotation )
    {
        VariantComposition variantComposition = variantCache.get(key);

        if (variantComposition == null)
        {
            variantComposition = new VariantComposition();

            // init variant
            variantComposition.setAminoAcidPosition(annotation.getAminoAcidPosition());
            variantComposition.setReferenceAminoAcid(annotation.getReferenceAminoAcid());
            variantComposition.setVariantAminoAcid(annotation.getVariantAminoAcid());

            variantCache.put(key, variantComposition);
        }

        variantComposition.updateTumorTypeComposition(annotation.getTumorType());
    }
}
