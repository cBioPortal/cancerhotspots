package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.domain.*;
import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Map<String, Cluster> clusterMap = new LinkedHashMap<>();
        Map<String, ClusteredHotspotMutation> mutationMap = new LinkedHashMap<>();

        // index Cluster instances
        for (Mutation mutation : mutations)
        {
            String key = mutation.getCluster();

            Cluster cluster = clusterMap.get(key);

            if (cluster == null)
            {
                cluster = new Cluster();

                cluster.setClusterId(key);
                cluster.setPdbChains(mutation.getPdbChains());
                cluster.setpValue(mutation.getpValue());

                clusterMap.put(key, cluster);
            }

            cluster.addResidue(mutation.getResidue());
        }

        // create ClusteredHotspotMutation instances
        for (Mutation mutation : mutations)
        {
            String key = mutation.getHugoSymbol().toLowerCase() + "_" + mutation.getResidue();
            ClusteredHotspotMutation clusteredMutation = mutationMap.get(key);

            if (clusteredMutation == null)
            {
                clusteredMutation = new ClusteredHotspotMutation();

                // copy required fields from the mutation instance
                clusteredMutation.init(mutation);
                clusteredMutation.setClassification(mutation.getClassification());

                mutationMap.put(key, clusteredMutation);
            }

            // update the mutation with the current cluster information
            Cluster cluster = clusterMap.get(mutation.getCluster());
            clusteredMutation.addCluster(cluster);
        }

        List<HotspotMutation> list = new ArrayList<>();
        list.addAll(mutationMap.values());
        return list;
    }
}
