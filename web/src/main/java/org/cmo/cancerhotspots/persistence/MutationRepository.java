package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.Mutation;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationRepository
{
    Iterable<Mutation> findAll();
    void saveAll(Iterable<Mutation> mutations);
}
