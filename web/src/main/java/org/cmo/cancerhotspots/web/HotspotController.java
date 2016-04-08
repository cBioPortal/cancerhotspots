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

import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.domain.VariantComposition;
import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.cmo.cancerhotspots.service.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value = "/")
public class HotspotController
{
    private final HotspotMutationService hotspotMutationService;
    private final VariantService variantService;

    @Autowired
    public HotspotController(HotspotMutationService hotspotMutationService,
        VariantService variantService)
    {
        this.hotspotMutationService = hotspotMutationService;
        this.variantService = variantService;
    }

    @RequestMapping(value = "/hotspots",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<HotspotMutation> getAllHotspotMutations()
    {
        return hotspotMutationService.getAllHotspotMutations();
    }

    @RequestMapping(value = "/variants/{aminoAcidChanges}",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<VariantComposition> getVariants(@PathVariable List<String> aminoAcidChanges)
    {
        List<VariantComposition> variants = new LinkedList<>();

        for (String aminoAcidChange : aminoAcidChanges)
        {
            VariantComposition variantComposition = variantService.getVariantComposition(aminoAcidChange);

            if (variantComposition != null)
            {
                variants.add(variantComposition);
            }
        }

        return variants;
    }

    @RequestMapping(value = "/variants/{hugoSymbol}/{aminoAcidChanges}",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public List<VariantComposition> getVariants(@PathVariable String hugoSymbol,
        @PathVariable List<String> aminoAcidChanges)
    {
        List<VariantComposition> variants = new LinkedList<>();

        for (String aminoAcidChange : aminoAcidChanges)
        {
            VariantComposition variantComposition = variantService.getVariantComposition(hugoSymbol, aminoAcidChange);

            if (variantComposition != null)
            {
                variants.add(variantComposition);
            }
        }

        return variants;
    }
}
