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

package org.cmo.cancerhotspots.web;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.domain.TumorTypeComposition;
import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.cmo.cancerhotspots.service.VariantService;
import org.cmo.cancerhotspots.service.internal.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value = "/api")
public class HotspotController
{
    private final HotspotMutationService hotspotMutationService;
    private final VariantService variantService;
    private final ConfigurationService configService;

    @Autowired
    public HotspotController(HotspotMutationService hotspotMutationService,
        VariantService variantService,
        ConfigurationService configService)
    {
        this.hotspotMutationService = hotspotMutationService;
        this.variantService = variantService;
        this.configService = configService;
    }

    @ApiOperation(value = "get all hotspot mutations",
        nickname = "getAllHotspotMutations")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success",
            response = HotspotMutation.class,
            responseContainer = "List"),
        @ApiResponse(code = 400, message = "Bad Request")
    })
    @RequestMapping(value = "/hotspots",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> getAllHotspotMutations()
    {
        return hotspotMutationService.getAllHotspotMutations();
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

//    @RequestMapping(value = "/download/{filename}",
//        method = {RequestMethod.GET, RequestMethod.POST})
    public InputStreamResource downloadFile(@PathVariable String filename) throws IOException
    {
        Resource resource = new ClassPathResource("data/" + filename + ".txt");
        return new InputStreamResource(resource.getInputStream());
    }
}
