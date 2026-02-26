package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.Mutation;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationRepository
{
    Iterable<Mutation> findAll();
    Iterable<Mutation> findAllV3();
    Iterable<Mutation> findByGene(String hugoSymbol);
    Iterable<Mutation> findByGene(String hugoSymbol, String version);
    Iterable<Mutation> findByTranscript(String transcriptId);
    Iterable<Mutation> findByTranscript(String transcriptId, String version);
    void saveAll(Iterable<Mutation> mutations);
}
