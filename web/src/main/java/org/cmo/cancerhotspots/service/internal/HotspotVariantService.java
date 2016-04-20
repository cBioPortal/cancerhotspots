package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.cmo.cancerhotspots.domain.VariantComposition;
import org.cmo.cancerhotspots.service.VariantService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private List<VariantComposition> variantCache;

    // cache of <amino acid change, variant> pairs
    private Map<String, VariantComposition> variantCacheByAAChange;
    // cache of <hugo symbol + amino acid change, variant> pairs
    private Map<String, VariantComposition> variantCacheByGeneAndAAChange;

    @Override
    public VariantComposition getVariantComposition(String aminoAcidChange)
    {
        if (this.variantCacheByAAChange == null)
        {
            this.variantCacheByAAChange = constructVariantCacheByAAChange();
        }

        return variantCacheByAAChange.get(aminoAcidChange.toUpperCase());
    }

    @Override
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
    public List<VariantComposition> getAllVariantCompositions()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.variantCache == null ||
            this.variantCache.size() == 0)
        {
            BeanListProcessor<VariantComposition> rowProcessor =
                new BeanListProcessor<>(VariantComposition.class);

            CsvParser variantParser = FileIO.initCsvParser(rowProcessor);
            variantParser.parse(FileIO.getReader(variantFileUri));

            // cache retrieved beans
            this.variantCache = rowProcessor.getBeans();
        }

        List<VariantComposition> variantCompositions = new ArrayList<>(variantCache.size());
        variantCompositions.addAll(variantCache);

        return variantCompositions;
    }

    private Map<String, VariantComposition> constructVariantCacheByGeneAndAAChange()
    {
        if (this.variantCache == null ||
            this.variantCache.size() == 0)
        {
            getAllVariantCompositions();
        }

        Map<String, VariantComposition> variantCache = new HashMap<>();

        for (VariantComposition variant : this.variantCache)
        {
            String aaChange = variant.getReferenceAminoAcid() +
                              variant.getAminoAcidPosition() +
                              variant.getVariantAminoAcid();

            String key = (variant.getHugoSymbol() + "_" + aaChange).toUpperCase();
            variantCache.put(key, variant);
        }

        return variantCache;
    }

    private Map<String, VariantComposition> constructVariantCacheByAAChange()
    {
        if (this.variantCache == null ||
            this.variantCache.size() == 0)
        {
            getAllVariantCompositions();
        }

        Map<String, VariantComposition> variantCache = new HashMap<>();

        // TODO this is not accurate! we need to combine all gene specific info
        // together into one VariantComposition instance...

        for (VariantComposition variant : this.variantCache)
        {
            String aaChange = variant.getReferenceAminoAcid() +
                              variant.getAminoAcidPosition() +
                              variant.getVariantAminoAcid();

            String key = aaChange.toUpperCase();
            variantCache.put(key, variant);
        }

        return variantCache;
    }
}
