package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.domain.Mutation;
import org.cmo.cancerhotspots.domain.MutationRepository;
import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class ClusteredHotspotMutationService implements HotspotMutationService
{
    private final MutationRepository mutationRepository;
    private List<HotspotMutation> hotspotCache;

    @Autowired
    public ClusteredHotspotMutationService(MutationRepository mutationRepository)
    {
        this.mutationRepository = mutationRepository;
    }

    public List<HotspotMutation> getAllHotspotMutations()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.hotspotCache == null ||
            this.hotspotCache.size() == 0)
        {
            List<Mutation> mutations = mutationRepository.findAll();
            this.hotspotCache = convertToMultiResidue(mutations);
        }

        return this.hotspotCache;
    }

    public List<HotspotMutation> convertToMultiResidue(List<Mutation> mutations)
    {
        // TODO process mutations and merge into corresponding ClusteredHotspotMutation instances
        return null;
    }
}
