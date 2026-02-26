package org.cmo.cancerhotspots.persistence.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.tsv.TsvWriter;
import org.cmo.cancerhotspots.model.TumorTypeComposition;
import org.cmo.cancerhotspots.persistence.VariantRepository;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class VariantRepositoryImpl implements VariantRepository
{
    private String variantFileUri;
    @Value("${hotspot.variant.uri}")
    public void setVariantFileUri(String variantFileUri)
    {
        this.variantFileUri = variantFileUri;
    }

    private String variantFileV3Uri;
    @Value("${hotspot.variant.v3.uri:}")
    public void setVariantFileV3Uri(String variantFileV3Uri)
    {
        this.variantFileV3Uri = variantFileV3Uri;
    }

    private List<TumorTypeComposition> cache;
    private List<TumorTypeComposition> v3Cache;

    private List<TumorTypeComposition> loadVariants(String uri)
    {
        BeanListProcessor<TumorTypeComposition> rowProcessor =
            new BeanListProcessor<>(TumorTypeComposition.class);

        CsvParser variantParser = FileIO.initCsvParser(rowProcessor);
        variantParser.parse(FileIO.getReader(uri));

        return rowProcessor.getBeans();
    }

    public void saveAll(Iterable<TumorTypeComposition> compositions)
    {
        TsvWriter writer = FileIO.initTsvWriter(
            new BeanWriterProcessor<>(TumorTypeComposition.class),
            FileIO.getWriter(variantFileUri));

        writer.writeHeaders();

        for (TumorTypeComposition composition : compositions)
        {
            writer.processRecord(composition);
        }

        writer.close();
    }

    public Iterable<TumorTypeComposition> findAll()
    {
        // parse the input file only once, and save the result in the cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            this.cache = loadVariants(variantFileUri);
        }

        return this.cache;
    }

    @Override
    public Iterable<TumorTypeComposition> findAllV3()
    {
        if (variantFileV3Uri == null || variantFileV3Uri.isEmpty())
        {
            return findAll();
        }

        if (this.v3Cache == null ||
            this.v3Cache.size() == 0)
        {
            this.v3Cache = loadVariants(variantFileV3Uri);
        }

        return this.v3Cache;
    }
}
