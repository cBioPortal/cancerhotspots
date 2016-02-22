package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.HotspotMutation;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface HotspotMutationService
{
    List<HotspotMutation> getAllHotspotMutations();
}
