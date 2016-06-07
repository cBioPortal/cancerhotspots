package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.TumorTypeComposition;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantRepository
{
    Iterable<TumorTypeComposition> findAll();
    void saveAll(Iterable<TumorTypeComposition> compositions);
}
