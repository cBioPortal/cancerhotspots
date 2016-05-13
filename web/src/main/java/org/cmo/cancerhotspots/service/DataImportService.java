package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.HotspotMutation;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface DataImportService
{
    void createVariantFile(List<HotspotMutation> hotspotMutations);
    void createHotspotFile(List<HotspotMutation> hotspotMutations);
    void generateVariantComposition(List<HotspotMutation> hotspotMutations);
}
