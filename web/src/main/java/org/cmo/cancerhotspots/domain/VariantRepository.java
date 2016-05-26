package org.cmo.cancerhotspots.domain;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantRepository
{
    List<TumorTypeComposition> findAll();
    void saveAll(List<TumorTypeComposition> compositions);
}
