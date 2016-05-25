package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.Mutation;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface DataImportService
{
    void createVariantFile(List<Mutation> mutations);
    void createHotspotFile(List<Mutation> mutations);
    void generateVariantComposition(List<Mutation> mutations);
    void generateTumorTypeComposition(List<Mutation> mutations);
}
