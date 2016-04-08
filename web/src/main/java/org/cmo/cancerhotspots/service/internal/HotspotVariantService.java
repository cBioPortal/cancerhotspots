package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.domain.VariantComposition;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotVariantService implements VariantService
{
    // cache of <amino acid change, variant> pairs
    private Map<String, VariantComposition> variantCacheByAAChange;
    private Map<String, VariantComposition> variantCacheByGeneAndAAChange;

    private MutationAnnotationService mafService;

    @Autowired
    public HotspotVariantService(MutationAnnotationService mafService)
    {
        this.mafService = mafService;
        this.variantCacheByAAChange = constructVariantCacheByAAChange();
        this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange();
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

    private Map<String, VariantComposition> constructVariantCacheByGeneAndAAChange()
    {
        Map<String, VariantComposition> variantCache = new HashMap<>();
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
