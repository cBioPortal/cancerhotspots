package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.model.TumorTypeComposition;
import org.cmo.cancerhotspots.persistence.VariantRepository;
import org.cmo.cancerhotspots.service.VariantService;
import org.cmo.cancerhotspots.util.RangeConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotVariantService implements VariantService
{
    private final VariantRepository variantRepository;

    // cache of <amino acid change, variant> pairs
    private Map<String, TumorTypeComposition> variantCacheByAAChange;
    // cache of <hugo symbol + amino acid change, variant> pairs
    private Map<String, TumorTypeComposition> variantCacheByGeneAndAAChange;

    // v3 caches
    private Map<String, TumorTypeComposition> v3VariantCacheByGeneAndAAChange;

    @Autowired
    public HotspotVariantService(VariantRepository variantRepository)
    {
        this.variantRepository = variantRepository;
    }

    @Override
    public TumorTypeComposition getVariantComposition(String aminoAcidChange)
    {
        if (this.variantCacheByAAChange == null)
        {
            this.variantCacheByAAChange = constructVariantCacheByAAChange();
        }

        return variantCacheByAAChange.get(aminoAcidChange.toUpperCase());
    }

    @Override
    public TumorTypeComposition getVariantComposition(String hugoSymbol, String aminoAcidChange)
    {
        return getVariantComposition(hugoSymbol, aminoAcidChange, null);
    }

    @Override
    public TumorTypeComposition getVariantComposition(String hugoSymbol, String aminoAcidChange, String version)
    {
        if ("v3".equals(version))
        {
            if (this.v3VariantCacheByGeneAndAAChange == null)
            {
                this.v3VariantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange(
                    getAllVariantCompositions("v3"));
            }
            return v3VariantCacheByGeneAndAAChange.get(
                (hugoSymbol + "_" + aminoAcidChange).toUpperCase());
        }

        if (this.variantCacheByGeneAndAAChange == null)
        {
            this.variantCacheByGeneAndAAChange = constructVariantCacheByGeneAndAAChange(
                getAllVariantCompositions());
        }

        return variantCacheByGeneAndAAChange.get(
            (hugoSymbol + "_" + aminoAcidChange).toUpperCase());
    }

    @Override
    public List<TumorTypeComposition> getAllVariantCompositions()
    {
        return getAllVariantCompositions(null);
    }

    @Override
    public List<TumorTypeComposition> getAllVariantCompositions(String version)
    {
        List<TumorTypeComposition> list = new ArrayList<>();

        Iterable<TumorTypeComposition> compositions;
        if ("v3".equals(version))
        {
            compositions = variantRepository.findAllV3();
        }
        else
        {
            compositions = variantRepository.findAll();
        }

        for (TumorTypeComposition composition : compositions)
        {
            list.add(composition);
        }

        return list;
    }

    private Map<String, TumorTypeComposition> constructVariantCacheByGeneAndAAChange(
        List<TumorTypeComposition> variants)
    {
        Map<String, TumorTypeComposition> variantCache = new HashMap<>();

        for (TumorTypeComposition variant : variants)
        {
            String key = (variant.getHugoSymbol() + "_" + aminoAcidChange(variant)).toUpperCase();
            variantCache.put(key, variant);
        }

        return variantCache;
    }

    private Map<String, TumorTypeComposition> constructVariantCacheByAAChange()
    {
        Map<String, TumorTypeComposition> variantCache = new HashMap<>();

        // TODO this is not accurate! we need to combine all gene specific info
        // together into one VariantComposition instance...

        for (TumorTypeComposition variant : getAllVariantCompositions())
        {
            String key = aminoAcidChange(variant).toUpperCase();
            variantCache.put(key, variant);
        }

        return variantCache;
    }

    private String aminoAcidChange(TumorTypeComposition variant)
    {
        String aaChange;

        // use residue if available
        if (variant.getResidue() != null)
        {
            aaChange = variant.getResidue() +
                       variant.getVariantAminoAcid();
        }
        else
        {
            RangeConversion converter = new RangeConversion();

            // this is for backward compatibility
            // (variant files with no residue column available)
            aaChange = variant.getReferenceAminoAcid() +
                       converter.revert(variant.getAminoAcidPosition()) +
                       variant.getVariantAminoAcid();
        }

        return aaChange;
    }
}
