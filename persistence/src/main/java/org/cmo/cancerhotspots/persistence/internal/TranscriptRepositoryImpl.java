package org.cmo.cancerhotspots.persistence.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.tsv.TsvWriter;
import org.cmo.cancerhotspots.model.Transcript;
import org.cmo.cancerhotspots.persistence.TranscriptRepository;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class TranscriptRepositoryImpl implements TranscriptRepository
{
    private String transcriptUri;
    @Value("${hotspot.transcript.uri}")
    public void setTranscriptUri(String transcriptUri) { this.transcriptUri = transcriptUri; }

    private List<Transcript> cache;

    public Iterable<Transcript> findAll()
    {
        // parse the input file only once, and save the result in the transcript cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            BeanListProcessor<Transcript> rowProcessor =
                new BeanListProcessor<>(Transcript.class);

            CsvParser transcriptParser = FileIO.initCsvParser(rowProcessor);
            transcriptParser.parse(FileIO.getReader(transcriptUri));

            // cache retrieved beans
            this.cache = rowProcessor.getBeans();
        }

        return this.cache;
    }

    @Override
    public Iterable<Transcript> findByGene(String hugoSymbol)
    {
        Iterable<Transcript> transcripts = findAll();
        List<Transcript> result = new ArrayList<>();

        for (Transcript transcript: transcripts)
        {
            if (transcript.getGeneSymbol() != null &&
                transcript.getGeneSymbol().trim().equalsIgnoreCase(hugoSymbol))
            {
                result.add(transcript);
            }
        }

        return result;
    }

    @Override
    public Iterable<Transcript> findByTranscript(String transcriptId)
    {
        Iterable<Transcript> transcripts = findAll();
        List<Transcript> result = new ArrayList<>();

        for (Transcript transcript: transcripts)
        {
            if (transcript.getTranscriptId() != null &&
                transcript.getTranscriptId().trim().equalsIgnoreCase(transcriptId))
            {
                result.add(transcript);
            }
        }

        return result;
    }

    @Override
    public void saveAll(Iterable<Transcript> transcripts)
    {
        TsvWriter writer = FileIO.initTsvWriter(
            new BeanWriterProcessor<>(Transcript.class),
            FileIO.getWriter(transcriptUri));

        writer.writeHeaders();

        for (Transcript transcript : transcripts)
        {
            writer.processRecord(transcript);
        }

        writer.close();
    }
}
