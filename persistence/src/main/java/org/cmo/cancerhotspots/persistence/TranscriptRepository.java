package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.Transcript;

/**
 * @author Selcuk Onur Sumer
 */
public interface TranscriptRepository
{
    Iterable<Transcript> findAll();
    Iterable<Transcript> findByGene(String hugoSymbol);
    Iterable<Transcript> findByTranscript(String transcriptId);
    void saveAll(Iterable<Transcript> mutations);
}
