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

package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.model.HotspotMutation;
import org.cmo.cancerhotspots.model.Mutation;
import org.cmo.cancerhotspots.persistence.MutationRepository;
import org.cmo.cancerhotspots.model.SingleResidueHotspotMutation;
import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class SingleResidueHotspotMutationService implements HotspotMutationService
{
    private final MutationRepository mutationRepository;
    private List<HotspotMutation> hotspotCache;

    @Autowired
    public SingleResidueHotspotMutationService(MutationRepository mutationRepository)
    {
        this.mutationRepository = mutationRepository;
    }

    public List<HotspotMutation> getAllHotspotMutations()
    {
        // parse the input file only once, and save the result in the hotspot cache
        if (this.hotspotCache == null ||
            this.hotspotCache.size() == 0)
        {
            Iterable<Mutation> mutations = mutationRepository.findAll();

            // cache converted data
            this.hotspotCache = convertToSingleResidue(mutations);
        }

        return this.hotspotCache;
    }

    @Override
    public List<HotspotMutation> getHotspotMutations(List<String> hugoSymbols)
    {
        List<HotspotMutation> mutations = new ArrayList<>();

        for (String hugoSymbol: hugoSymbols)
        {
            mutations.addAll(convertToSingleResidue(
                mutationRepository.findByGene(hugoSymbol)));
        }

        return mutations;
    }

    public List<HotspotMutation> convertToSingleResidue(Iterable<Mutation> mutations)
    {
        List<HotspotMutation> list = new ArrayList<>();

        for (Mutation mutation : mutations)
        {
            SingleResidueHotspotMutation hotspotMutation = new SingleResidueHotspotMutation();

            hotspotMutation.init(mutation);
            hotspotMutation.setqValue(mutation.getqValue());
            hotspotMutation.setqValuePancan(mutation.getqValuePancan());
            hotspotMutation.setqValueCancerType(mutation.getqValueCancerType());

            if (mutation.getIndelSize() != null) {
                hotspotMutation.setType("indel");
            }
            else {
                // TODO we may have different type of mutations, not only missense!
                hotspotMutation.setType("missense");
            }

            list.add(hotspotMutation);
        }

        return list;
    }
}
