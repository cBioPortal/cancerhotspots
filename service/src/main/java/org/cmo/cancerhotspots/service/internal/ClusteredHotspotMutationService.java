package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.persistence.*;
import org.cmo.cancerhotspots.model.Cluster;
import org.cmo.cancerhotspots.model.ClusteredHotspotMutation;
import org.cmo.cancerhotspots.model.HotspotMutation;
import org.cmo.cancerhotspots.model.Mutation;
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
    private final ClusterRepository clusterRepository;
    private List<HotspotMutation> hotspotCache;

    @Autowired
    public ClusteredHotspotMutationService(MutationRepository mutationRepository,
        ClusterRepository clusterRepository)
    {
        this.mutationRepository = mutationRepository;
        this.clusterRepository = clusterRepository;
    }

    public List<HotspotMutation> getAllHotspotMutations()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.hotspotCache == null ||
            this.hotspotCache.size() == 0)
        {
            Iterable<Mutation> mutations = mutationRepository.findAll();
            this.hotspotCache = convertToMultiResidue(mutations);
        }

        return this.hotspotCache;
    }

    @Override
    public List<HotspotMutation> getAllHotspotMutations(String version)
    {
        return getAllHotspotMutations();
    }

    @Override
    public List<HotspotMutation> getHotspotMutationsByGene(List<String> hugoSymbols)
    {
        List<HotspotMutation> mutations = new ArrayList<>();

        for (String hugoSymbol: hugoSymbols)
        {
            mutations.addAll(convertToMultiResidue(
                mutationRepository.findByGene(hugoSymbol.toUpperCase())));
        }

        return mutations;
    }

    @Override
    public List<HotspotMutation> getHotspotMutationsByGene(List<String> hugoSymbols, String version)
    {
        return getHotspotMutationsByGene(hugoSymbols);
    }

    @Override
    public List<HotspotMutation> getHotspotMutationsByTranscript(List<String> transcriptIds)
    {
        List<HotspotMutation> mutations = new ArrayList<>();

        for (String transcriptId: transcriptIds)
        {
            mutations.addAll(convertToMultiResidue(
                mutationRepository.findByTranscript(transcriptId.toUpperCase())));
        }

        return mutations;
    }

    @Override
    public List<HotspotMutation> getHotspotMutationsByTranscript(List<String> transcriptIds, String version)
    {
        return getHotspotMutationsByTranscript(transcriptIds);
    }

    public List<HotspotMutation> convertToMultiResidue(Iterable<Mutation> mutations)
    {
        Map<String, ClusteredHotspotMutation> mutationMap = new LinkedHashMap<>();

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
                clusteredMutation.setpValue(mutation.getpValue());

                mutationMap.put(key, clusteredMutation);
            }

            // update the mutation with the current cluster information
            Cluster cluster = clusterRepository.findOne(mutation.getCluster());
            clusteredMutation.addCluster(cluster);
        }

        List<HotspotMutation> list = new ArrayList<>();
        list.addAll(mutationMap.values());
        return list;
    }
}
