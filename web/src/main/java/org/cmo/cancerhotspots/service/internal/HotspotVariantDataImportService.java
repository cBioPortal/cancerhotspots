package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.conversions.Conversion;
import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.domain.MapConversion;
import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.domain.VariantComposition;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.service.VariantDataImportService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotVariantDataImportService implements VariantDataImportService
{
    private String variantFileUri;
    @Value("${hotspot.variant.uri}")
    public void setVariantFileUri(String variantFileUri)
    {
        this.variantFileUri = variantFileUri;
    }

    // cache of <amino acid change, variant> pairs
    private Map<String, VariantComposition> variantCacheByAAChange;
    // cache of <hugo symbol + amino acid change, variant> pairs
    private Map<String, VariantComposition> variantCacheByGeneAndAAChange;

    private MutationAnnotationService mafService;

    @Autowired
    public HotspotVariantDataImportService(MutationAnnotationService mafService)
    {
        this.mafService = mafService;
        // TODO technically we should use database for such large data, not in-memory cache
        this.variantCacheByGeneAndAAChange = null;
        this.variantCacheByAAChange = null;
    }

    public VariantComposition getVariantComposition(String aminoAcidChange)
    {
        if (this.variantCacheByAAChange == null)
        {
            this.variantCacheByAAChange = constructVariantCacheByAAChange();
        }

        return variantCacheByAAChange.get(aminoAcidChange.toUpperCase());
    }

    public VariantComposition getVariantComposition(String hugoSymbol, String aminoAcidChange)
    {
        if (this.variantCacheByGeneAndAAChange == null)
        {
            this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange();
        }

        return variantCacheByGeneAndAAChange.get(
            (hugoSymbol + "_" + aminoAcidChange).toUpperCase());
    }

    @Override
    public void createVariantFile(List<HotspotMutation> hotspotMutations)
    {
        if (this.variantCacheByGeneAndAAChange == null)
        {
            this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange();
        }

        Writer writer = FileIO.getWriter(variantFileUri);

        // TODO use CSV parser to write the data!
        Conversion<String, Map<String, Integer>> conversion = new MapConversion();

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
                                 conversion.revert(composition.getTumorTypeComposition()) + "\n");
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
