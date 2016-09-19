package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.model.Mutation;

/**
 * @author Selcuk Onur Sumer
 */
public interface DataImportService
{
    void createVariantFile(Iterable<Mutation> mutations);
    void createHotspotFile(Iterable<Mutation> mutations);
    void createClusterFile(Iterable<Mutation> mutations);
    void generateVariantComposition(Iterable<Mutation> mutations);
    void generateTumorTypeComposition(Iterable<Mutation> mutations);
}
