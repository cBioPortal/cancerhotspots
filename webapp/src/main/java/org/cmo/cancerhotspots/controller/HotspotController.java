/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Cancer Hotspots.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cmo.cancerhotspots.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cmo.cancerhotspots.model.*;
import org.cmo.cancerhotspots.service.ClusterService;
import org.cmo.cancerhotspots.service.VariantService;
import org.cmo.cancerhotspots.service.internal.ConfigurationService;
import org.cmo.cancerhotspots.service.internal.ClusteredHotspotMutationService;
import org.cmo.cancerhotspots.service.internal.SingleResidueHotspotMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author Selcuk Onur Sumer
 */
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value = "/api")
public class HotspotController
{
    private final SingleResidueHotspotMutationService singleResidueHotspotMutationService;
    private final ClusteredHotspotMutationService multiResidueHotspotMutationService;
    private final VariantService variantService;
    private final ClusterService clusterService;
    private final ConfigurationService configService;

    @Autowired
    public HotspotController(SingleResidueHotspotMutationService singleResidueHotspotMutationService,
        ClusteredHotspotMutationService multiResidueHotspotMutationService,
        VariantService variantService,
        ClusterService clusterService,
        ConfigurationService configService)
    {
        this.singleResidueHotspotMutationService = singleResidueHotspotMutationService;
        this.multiResidueHotspotMutationService = multiResidueHotspotMutationService;
        this.variantService = variantService;
        this.clusterService = clusterService;
        this.configService = configService;
    }

    @ApiOperation(value = "get all single residue hotspot mutations",
        nickname = "fetchSingleResidueHotspotMutations")
    @RequestMapping(value = "/hotspots/single",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> fetchSingleResidueHotspotMutations()
    {
        return singleResidueHotspotMutationService.getAllHotspotMutations();
    }

    @ApiOperation(value = "get hotspot mutations by hugo gene symbol",
        nickname = "fetchSingleResidueHotspotMutationsByGeneGET")
    @RequestMapping(value = "/hotspots/single/byGene/{transcriptIds}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<HotspotMutation> fetchSingleResidueHotspotMutationsByGeneGET(
        @ApiParam(value = "Comma separated list of hugo gene symbols. For example PTEN,BRAF,TP53",
            required = true,
            allowMultiple = true)
        @PathVariable List<String> hugoSymbols)
    {
        return singleResidueHotspotMutationService.getHotspotMutationsByGene(hugoSymbols);
    }

    @ApiOperation(value = "get hotspot mutations by hugo gene symbol",
        nickname = "fetchSingleResidueHotspotMutationsByGenePOST")
    @RequestMapping(value = "/hotspots/single/byGene",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> fetchSingleResidueHotspotMutationsByGenePOST(
        @ApiParam(value = "List of hugo gene symbols. For example [\"PTEN\",\"BRAF\",\"TP53\"]",
            required = true,
            allowMultiple = true)
        @RequestBody
        List<String> hugoSymbols)
    {
        return this.fetchSingleResidueHotspotMutationsByGeneGET(hugoSymbols);
    }

    @ApiOperation(value = "get hotspot mutations by transcript id",
        nickname = "fetchSingleResidueHotspotMutationsByTranscriptGET")
    @RequestMapping(value = "/hotspots/single/byTranscript/{transcriptIds}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<HotspotMutation> fetchSingleResidueHotspotMutationsByTranscriptGET(
        @ApiParam(value = "Comma separated list of transcript IDs. For example ENST00000288602,ENST00000275493",
            required = true,
            allowMultiple = true)
        @PathVariable List<String> transcriptIds)
    {
        return singleResidueHotspotMutationService.getHotspotMutationsByTranscript(transcriptIds);
    }

    @ApiOperation(value = "get hotspot mutations by transcript id",
        nickname = "fetchSingleResidueHotspotMutationsByTranscriptPOST")
    @RequestMapping(value = "/hotspots/single/byTranscript",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> fetchSingleResidueHotspotMutationsByTranscriptPOST(
        @ApiParam(value = "List of transcript IDs. For example [\"ENST00000288602\",\"ENST00000275493\"]",
            required = true,
            allowMultiple = true)
        @RequestBody
        List<String> transcriptIds)
    {
        return fetchSingleResidueHotspotMutationsByTranscriptGET(transcriptIds);
    }

    // TODO add RequestMethod.GET after removing the backward compatible parameter hugoSymbols
    @ApiOperation(value = "get all 3D hotspot mutations",
        nickname = "fetch3dHotspotMutationsPOST")
    @RequestMapping(value = "/hotspots/3d",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> fetch3dHotspotMutationsPOST(
        @ApiParam(value = "Comma separated list of hugo symbols. For example PTEN,BRAF,TP53",
            required = false,
            allowMultiple = true)
        @RequestParam(required = false)
        List<String> hugoSymbols)
    {
        if (hugoSymbols == null ||
            hugoSymbols.size() == 0)
        {
            return multiResidueHotspotMutationService.getAllHotspotMutations();
        }
        else
        {
            return fetch3dHotspotMutationsByGeneGET(hugoSymbols);
        }
    }

    /**
     * @deprecated keeping this endpoint for backwards compatibility only
     */
    @ApiOperation(value = "get all hotspot mutations for the specified genes",
        nickname = "fetch3dHotspotMutationsByGene_Legacy")
    @RequestMapping(value = "/hotspots/3d/{hugoSymbols}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<HotspotMutation> fetch3dHotspotMutationsByGeneLegacy(
        @ApiParam(value = "Comma separated list of hugo symbols. For example PTEN,BRAF,TP53",
            required = true,
            allowMultiple = true)
        @PathVariable
            List<String> hugoSymbols)
    {
        return fetch3dHotspotMutationsByGeneGET(hugoSymbols);
    }

    @ApiOperation(value = "get all hotspot mutations for the specified genes",
        nickname = "fetch3dHotspotMutationsByGeneGET")
    @RequestMapping(value = "/hotspots/3d/byGene/{hugoSymbols}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<HotspotMutation> fetch3dHotspotMutationsByGeneGET(
        @ApiParam(value = "Comma separated list of hugo symbols. For example PTEN,BRAF,TP53",
            required = true,
            allowMultiple = true)
        @PathVariable
        List<String> hugoSymbols)
    {
        return multiResidueHotspotMutationService.getHotspotMutationsByGene(hugoSymbols);
    }

    @ApiOperation(value = "get all hotspot mutations for the specified genes",
        nickname = "fetch3dHotspotMutationsByGenePOST")
    @RequestMapping(value = "/hotspots/3d/byGene",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> fetch3dHotspotMutationsByGenePOST(
        @ApiParam(value = "List of hugo symbols. For example [\"PTEN\",\"BRAF\",\"TP53\"]",
            required = true,
            allowMultiple = true)
        @RequestBody
        List<String> hugoSymbols)
    {
        return fetch3dHotspotMutationsByGeneGET(hugoSymbols);
    }

    // TODO API disabled for now, enable if needed
    // -- after implementing corresponding service method properly!

//    @ApiOperation(value = "get variant tumor type compositions by amino acid change",
//        nickname = "getVariantsByAminoAcidChange")
//    @RequestMapping(value = "/variants/{aminoAcidChanges}",
//        method = {RequestMethod.GET, RequestMethod.POST},
//        produces = "application/json")
    public List<TumorTypeComposition> getVariants(
        @ApiParam(value = "Comma separated list of amino acid change values. For example V600E,V600K",
            required = true,
            allowMultiple = true)
        @RequestParam List<String> aminoAcidChanges)
    {
        List<TumorTypeComposition> variants = new LinkedList<>();

        for (String aminoAcidChange : aminoAcidChanges)
        {
            TumorTypeComposition tumorTypeComposition = variantService.getVariantComposition(aminoAcidChange);

            if (tumorTypeComposition != null)
            {
                variants.add(tumorTypeComposition);
            }
        }

        return variants;
    }

    @ApiOperation(value = "get variant tumor type compositions by gene and amino acid change",
        nickname = "fetchVariantsGET")
    @RequestMapping(value = "/variants/{hugoSymbol}/{aminoAcidChanges}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<TumorTypeComposition> fetchVariantsGET(
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = true)
        @PathVariable String hugoSymbol,
        @ApiParam(value = "Comma separated list of amino acid change values. For example V600E,V600K",
            required = true,
            allowMultiple = true)
        @PathVariable List<String> aminoAcidChanges)
    {
        List<TumorTypeComposition> variants = new LinkedList<>();

        for (String aminoAcidChange : aminoAcidChanges)
        {
            TumorTypeComposition
                tumorTypeComposition = variantService.getVariantComposition(hugoSymbol, aminoAcidChange);

            if (tumorTypeComposition != null)
            {
                variants.add(tumorTypeComposition);
            }
        }

        return variants;
    }

    @ApiOperation(value = "get variant tumor type compositions",
        nickname = "fetchVariantsPOST")
    @RequestMapping(value = "/variants",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<TumorTypeComposition> fetchVariantsPOST(
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = false)
        @RequestParam(required = false)
        String hugoSymbol,
        @ApiParam(value = "List of amino acid change values. For example [\"V600E\",\"V600K\"]",
            required = false,
            allowMultiple = true)
        @RequestBody(required = false)
        List<String> aminoAcidChanges)
    {
        if (aminoAcidChanges == null ||
            aminoAcidChanges.size() == 0)
        {
            return fetchAllVariantsGET();
        }
        else if (hugoSymbol == null)
        {
            return getVariants(aminoAcidChanges);
        }
        else
        {
            return fetchVariantsGET(hugoSymbol, aminoAcidChanges);
        }
    }

    @ApiOperation(value = "get all variant tumor type composition",
        nickname = "fetchAllVariantsGET")
    @RequestMapping(value = "/variants",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<TumorTypeComposition> fetchAllVariantsGET()
    {
        return variantService.getAllVariantCompositions();
    }

    @ApiOperation(value = "get clusters by hugo symbol and residue",
        nickname = "fetchClustersByHugoSymbolAndResidueGET")
    @RequestMapping(value = "/clusters/{hugoSymbol}/{residue}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<Cluster> fetchClustersGET(
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = true)
        @PathVariable String hugoSymbol,
        @ApiParam(value = "Residue, for example F595",
            required = true,
            allowMultiple = true)
        @PathVariable String residue)
    {
        return clusterService.getClusters(hugoSymbol, residue);
    }

    @ApiOperation(value = "get clusters by hugo symbol",
        nickname = "fetchClustersByHugoSymbolGET")
    @RequestMapping(value = "/clusters/{hugoSymbol}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<Cluster> fetchClustersGET(
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = true)
        @PathVariable String hugoSymbol)
    {
        return clusterService.getClusters(hugoSymbol);
    }

    @ApiOperation(value = "get clusters by cluster id",
        nickname = "fetchClustersByClusterIdGET")
    @RequestMapping(value = "/clusters/id/{clusterIds}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<Cluster> fetchClustersGET(
        @ApiParam(value = "Comma separated list of cluster ids, for example 1,2,3",
            required = true)
        @PathVariable List<String> clusterIds)
    {
        return clusterService.getClusters(clusterIds);
    }

    @ApiOperation(value = "get clusters",
        nickname = "fetchClustersPOST")
    @RequestMapping(value = "/clusters",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<Cluster> fetchClustersPOST(
        @ApiParam(value = "List of cluster ids, for example [1,2,3]",
            required = false)
        @RequestBody(required=false)
        List<String> clusterIds,
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = false)
        @RequestParam(required = false)
        String hugoSymbol,
        @ApiParam(value = "Residue, for example F595",
            required = false,
            allowMultiple = true)
        @RequestParam(required = false)
        String residue)
    {
        if (clusterIds != null &&
            clusterIds.size() > 0)
        {
            // if cluster ids are provided get clusters by id by default
            return fetchClustersGET(clusterIds);
        }
        else if (hugoSymbol != null &&
                 residue != null)
        {
            // this will only be invoked if both hugo symbol and residue provided,
            // and no cluster id provided
            return fetchClustersGET(hugoSymbol, residue);
        }
        else if (hugoSymbol != null)
        {
            // this will only be invoked when only hugo symbol provided
            return fetchClustersGET(hugoSymbol);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @ApiOperation(value = "get metadata",
        nickname = "fetchMetadata")
    @RequestMapping(value = "/metadata",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public Map<String, String> fetchMetadata()
    {
        Map <String, String> map = new LinkedHashMap<>();

        map.put("profile", this.configService.getProfile());

        return map;
    }

//    @RequestMapping(value = "/download/{filename}",
//        method = {RequestMethod.GET, RequestMethod.POST})
    public InputStreamResource downloadFile(@PathVariable String filename) throws IOException
    {
        Resource resource = new ClassPathResource("data/" + filename + ".txt");
        return new InputStreamResource(resource.getInputStream());
    }
}
