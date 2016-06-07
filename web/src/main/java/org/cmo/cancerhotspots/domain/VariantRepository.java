package org.cmo.cancerhotspots.domain;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantRepository
{
    Iterable<TumorTypeComposition> findAll();
    void saveAll(Iterable<TumorTypeComposition> compositions);
}
