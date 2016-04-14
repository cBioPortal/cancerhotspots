package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.cmo.cancerhotspots.domain.HotspotVariantComposition;
import org.cmo.cancerhotspots.domain.VariantComposition;
import org.cmo.cancerhotspots.service.VariantService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public VariantComposition getVariantComposition(String aminoAcidChange)
    {
        return null;
    }

    @Override
    public VariantComposition getVariantComposition(String hugoSymbol, String aminoAcidChange)
    {
        return null;
    }

    @Override
    public List<VariantComposition> getAllVariantCompositions()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.variantCache == null ||
            this.variantCache.size() == 0)
        {
            BeanListProcessor<HotspotVariantComposition> rowProcessor =
                new BeanListProcessor<>(HotspotVariantComposition.class);

            CsvParser variantParser = FileIO.initCsvParser(rowProcessor);
            variantParser.parse(FileIO.getReader(variantFileUri));

            List<HotspotVariantComposition> beans = rowProcessor.getBeans();

            // cache retrieved beans
            this.variantCache = new ArrayList<>(beans.size());
            this.variantCache.addAll(beans);
        }

        return this.variantCache;
    }
}
