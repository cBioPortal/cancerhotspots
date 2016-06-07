package org.cmo.cancerhotspots.domain.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.tsv.TsvWriter;
import org.cmo.cancerhotspots.domain.Mutation;
import org.cmo.cancerhotspots.domain.MutationRepository;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class MutationRepositoryImpl implements MutationRepository
{
    private String hotspotMutationUri;
    @Value("${hotspot.mutation.uri}")
    public void setHotspotMutationUri(String hotspotMutationUri) { this.hotspotMutationUri = hotspotMutationUri; }

    private List<Mutation> cache;

    public Iterable<Mutation> findAll()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            BeanListProcessor<Mutation> rowProcessor =
                new BeanListProcessor<>(Mutation.class);

            CsvParser hotspotParser = FileIO.initCsvParser(rowProcessor);
            hotspotParser.parse(FileIO.getReader(hotspotMutationUri));

            // cache retrieved beans
            this.cache = rowProcessor.getBeans();
        }

        return this.cache;
    }

    @Override
    public void saveAll(Iterable<Mutation> mutations)
    {
        TsvWriter writer = FileIO.initTsvWriter(
            new BeanWriterProcessor<>(Mutation.class),
            FileIO.getWriter(hotspotMutationUri));

        writer.writeHeaders();

        for (Mutation mutation : mutations)
        {
            writer.processRecord(mutation);
        }

        writer.close();
    }
}
