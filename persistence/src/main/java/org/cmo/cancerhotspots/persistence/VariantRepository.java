package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.TumorTypeComposition;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantRepository
{
    Iterable<TumorTypeComposition> findAll();
    Iterable<TumorTypeComposition> findAllV3();
    void saveAll(Iterable<TumorTypeComposition> compositions);
}
