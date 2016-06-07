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

    private List<TumorTypeComposition> cache;

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
        // parse the input file only once, and save the result in the hotspot cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            BeanListProcessor<TumorTypeComposition> rowProcessor =
                new BeanListProcessor<>(TumorTypeComposition.class);

            CsvParser variantParser = FileIO.initCsvParser(rowProcessor);
            variantParser.parse(FileIO.getReader(variantFileUri));

            // cache retrieved beans
            this.cache = rowProcessor.getBeans();
        }

        return this.cache;
    }
}
