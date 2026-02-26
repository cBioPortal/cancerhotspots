package org.cmo.cancerhotspots.persistence.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.tsv.TsvWriter;
import org.cmo.cancerhotspots.model.Mutation;
import org.cmo.cancerhotspots.persistence.MutationRepository;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    private String hotspotMutationV3Uri;
    @Value("${hotspot.mutation.v3.uri:}")
    public void setHotspotMutationV3Uri(String hotspotMutationV3Uri) { this.hotspotMutationV3Uri = hotspotMutationV3Uri; }

    private List<Mutation> cache;
    private List<Mutation> v3Cache;

    private List<Mutation> loadMutations(String uri)
    {
        BeanListProcessor<Mutation> rowProcessor =
            new BeanListProcessor<>(Mutation.class);

        CsvParser hotspotParser = FileIO.initCsvParser(rowProcessor);
        hotspotParser.parse(FileIO.getReader(uri));

        return rowProcessor.getBeans();
    }

    public Iterable<Mutation> findAll()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            this.cache = loadMutations(hotspotMutationUri);
        }

        return this.cache;
    }

    @Override
    public Iterable<Mutation> findAllV3()
    {
        if (hotspotMutationV3Uri == null || hotspotMutationV3Uri.isEmpty())
        {
            return findAll();
        }

        if (this.v3Cache == null ||
            this.v3Cache.size() == 0)
        {
            this.v3Cache = loadMutations(hotspotMutationV3Uri);
        }

        return this.v3Cache;
    }

    private Iterable<Mutation> findAllForVersion(String version)
    {
        if ("v3".equals(version))
        {
            return findAllV3();
        }
        return findAll();
    }

    @Override
    public Iterable<Mutation> findByGene(String hugoSymbol)
    {
        return findByGene(hugoSymbol, null);
    }

    @Override
    public Iterable<Mutation> findByGene(String hugoSymbol, String version)
    {
        Iterable<Mutation> mutations = findAllForVersion(version);
        List<Mutation> result = new ArrayList<>();

        for (Mutation mutation: mutations)
        {
            if (mutation.getHugoSymbol() != null &&
                mutation.getHugoSymbol().trim().equalsIgnoreCase(hugoSymbol))
            {
                result.add(mutation);
            }
        }

        return result;
    }

    @Override
    public Iterable<Mutation> findByTranscript(String transcriptId)
    {
        return findByTranscript(transcriptId, null);
    }

    @Override
    public Iterable<Mutation> findByTranscript(String transcriptId, String version)
    {
        Iterable<Mutation> mutations = findAllForVersion(version);
        List<Mutation> result = new ArrayList<>();

        for (Mutation mutation: mutations)
        {
            if (mutation.getTranscriptId() != null &&
                mutation.getTranscriptId().trim().equalsIgnoreCase(transcriptId))
            {
                result.add(mutation);
            }
        }

        return result;
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
