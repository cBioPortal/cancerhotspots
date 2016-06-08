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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

    @ApiOperation(value = "get all hotspot mutations",
        nickname = "getAllHotspotMutations")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = SingleResidueHotspotMutation.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/hotspots/single",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> getSingleResidueHotspotMutations()
    {
        return singleResidueHotspotMutationService.getAllHotspotMutations();
    }

    @ApiOperation(value = "get all 3D hotspot mutations",
        nickname = "getAll3dHotspotMutations")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = ClusteredHotspotMutation.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/hotspots/3d",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> get3dHotspotMutations()
    {
        return multiResidueHotspotMutationService.getAllHotspotMutations();
    }

    // TODO API disabled for now, enable if needed
    // -- after implementing corresponding service method properly!

//    @ApiOperation(value = "get variant tumor type compositions",
//        nickname = "getVariantsByAminoAcidChange")
//    @ApiResponses(value = {
//        @ApiResponse(code = 200, message = "Success",
//            response = VariantComposition.class,
//            responseContainer = "List"),
//        @ApiResponse(code = 400, message = "Bad Request")
//    })
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

    @ApiOperation(value = "get variant tumor type compositions",
        nickname = "getVariantsByHugoSymbolAndAminoAcidChange")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = TumorTypeComposition.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/variants/{hugoSymbol}/{aminoAcidChanges}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<TumorTypeComposition> getVariants(
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
        nickname = "postVariants")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = TumorTypeComposition.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/variants",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<TumorTypeComposition> postVariants(
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = false)
        @RequestParam(required = false)
        String hugoSymbol,
        @ApiParam(value = "Comma separated list of amino acid change values. For example V600E,V600K",
            required = false,
            allowMultiple = true)
        @RequestParam(required = false)
        List<String> aminoAcidChanges)
    {
        if (aminoAcidChanges == null)
        {
            return getAllVariants();
        }
        else if (hugoSymbol == null)
        {
            return getVariants(aminoAcidChanges);
        }
        else
        {
            return getVariants(hugoSymbol, aminoAcidChanges);
        }
    }

    @ApiOperation(value = "get all variant tumor type composition",
        nickname = "getAllVariants")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = TumorTypeComposition.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/variants",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<TumorTypeComposition> getAllVariants()
    {
        return variantService.getAllVariantCompositions();
    }

    @ApiOperation(value = "get clusters by hugo symbol and residue",
        nickname = "getClustersByHugoSymbolAndResidue")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = Cluster.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/clusters/{hugoSymbol}/{residue}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<Cluster> getClusters(
        @ApiParam(value = "Hugo gene symbol, for example BRAF",
            required = true)
        @PathVariable String hugoSymbol,
        @ApiParam(value = "Residue, for example F595",
            required = true,
            allowMultiple = true)
        @PathVariable String residue)
    {
        return clusterService.getCluster(hugoSymbol, residue);
    }

    @ApiOperation(value = "get clusters by cluster id",
        nickname = "getClustersByClusterId")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = Cluster.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/clusters/{clusterIds}",
        method = {RequestMethod.GET},
        produces = "application/json")
    public List<Cluster> getClusters(
        @ApiParam(value = "Comma separated list of cluster ids, for example 1,2,3",
            required = false)
        @PathVariable List<String> clusterIds)
    {
        return clusterService.getCluster(clusterIds);
    }

    @ApiOperation(value = "get clusters",
        nickname = "postClusters")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = Cluster.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/clusters",
        method = {RequestMethod.POST},
        produces = "application/json")
    public List<Cluster> postClusters(
        @ApiParam(value = "Comma separated list of cluster ids, for example 1,2,3",
            required = false)
        @RequestParam(required = false)
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
        if (clusterIds != null)
        {
            // if cluster ids are provided get clusters by id by default
            return getClusters(clusterIds);
        }
        else if (hugoSymbol != null &&
                 residue != null)
        {
            // this will only be invoked if both hugo symbol and residue provided,
            // and no cluster id provided
            return getClusters(hugoSymbol, residue);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @ApiOperation(value = "get metadata",
        nickname = "getMetadata")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/metadata",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public Map<String, String> getMetadata()
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
