package org.cmo.cancerhotspots.persistence.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.tsv.TsvWriter;
import org.cmo.cancerhotspots.model.Cluster;
import org.cmo.cancerhotspots.persistence.ClusterRepository;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class ClusterRepositoryImpl implements ClusterRepository
{
    private String clusterFileUri;
    @Value("${hotspot.cluster.uri}")
    public void setClusterFileUri(String clusterFileUri)
    {
        this.clusterFileUri = clusterFileUri;
    }

    private Map<String, Cluster> cache;
    private Map<String, List<Cluster>> cacheByGeneAndResidue;
    private Map<String, List<Cluster>> cacheByGene;

    @Override
    public Iterable<Cluster> findAll()
    {
        // parse the input file only once, and save the result in the cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            BeanListProcessor<Cluster> rowProcessor =
                new BeanListProcessor<>(Cluster.class);

            CsvParser variantParser = FileIO.initCsvParser(rowProcessor);
            variantParser.parse(FileIO.getReader(clusterFileUri));

            // cache retrieved clusters
            this.cache = cacheById(rowProcessor.getBeans());
        }

        return this.cache.values();
    }

    @Override
    public Cluster findOne(String clusterId)
    {
        if (this.cache == null)
        {
            // this will generate the cache by id
            findAll();
        }

        return this.cache.get(clusterId);
    }

    @Override
    public Iterable<Cluster> findByGeneAndResidue(String hugoSymbol, String residue)
    {
        if (this.cacheByGeneAndResidue == null)
        {
            this.cacheByGeneAndResidue = cacheByGeneAndResidue(findAll());
        }

        return this.cacheByGeneAndResidue.get(
            hugoSymbol.toLowerCase() + "_" + residue.toLowerCase());
    }

    @Override
    public Iterable<Cluster> findByGene(String hugoSymbol)
    {
        if (this.cacheByGene == null)
        {
            this.cacheByGene = cacheByGene(findAll());
        }

        return this.cacheByGene.get(hugoSymbol.toLowerCase());
    }

    @Override
    public void saveAll(Iterable<Cluster> clusters)
    {
        TsvWriter writer = FileIO.initTsvWriter(
            new BeanWriterProcessor<>(Cluster.class),
            FileIO.getWriter(clusterFileUri));

        writer.writeHeaders();

        for (Cluster cluster : clusters)
        {
            writer.processRecord(cluster);
        }

        writer.close();
    }

    private Map<String, Cluster> cacheById(Iterable<Cluster> clusters)
    {
        Map<String, Cluster> cache = new LinkedHashMap<>();

        for (Cluster cluster : clusters)
        {
            // populate cache by id
            cache.put(cluster.getClusterId(), cluster);
        }

        return cache;
    }

    private Map<String, List<Cluster>> cacheByGeneAndResidue(Iterable<Cluster> clusters)
    {
        Map<String, List<Cluster>> cache = new LinkedHashMap<>();

        for (Cluster cluster : clusters)
        {
            // populate cache by gene and residue
            for (String residue : cluster.getResidueMap().keySet())
            {
                String key = (cluster.getHugoSymbol() + "_" + residue).toLowerCase();

                List<Cluster> list = cache.get(key);

                if (list == null)
                {
                    list = new ArrayList<>();
                    cache.put(key, list);
                }

                list.add(cluster);
            }
        }

        return cache;
    }

    private Map<String, List<Cluster>> cacheByGene(Iterable<Cluster> clusters)
    {
        Map<String, List<Cluster>> cache = new LinkedHashMap<>();

        for (Cluster cluster : clusters)
        {
            String key = cluster.getHugoSymbol().toLowerCase();

            List<Cluster> list = cache.get(key);

            if (list == null)
            {
                list = new ArrayList<>();
                cache.put(key, list);
            }

            list.add(cluster);
        }

        return cache;
    }
}
