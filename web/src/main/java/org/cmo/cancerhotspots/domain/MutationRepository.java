package org.cmo.cancerhotspots.domain;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationRepository
{
    List<Mutation> findAll();
    void saveAll(List<Mutation> mutations);
}
