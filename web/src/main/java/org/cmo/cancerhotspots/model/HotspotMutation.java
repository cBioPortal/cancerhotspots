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

package org.cmo.cancerhotspots.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotMutation
{
    private String hugoSymbol;
    private String residue;
    private Map<String, Integer> variantAminoAcid;
    private Integer tumorTypeCount;
    private Integer tumorCount;
    private Map<String, Integer> tumorTypeComposition;

    @ApiModelProperty(value = "Hugo symbol", required = true)
    public String getHugoSymbol()
    {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol)
    {
        this.hugoSymbol = hugoSymbol;
    }

    @ApiModelProperty(value = "Residue", required = true)
    public String getResidue()
    {
        return residue;
    }

    public void setResidue(String residue)
    {
        this.residue = residue;
    }

    @ApiModelProperty(value = "Variant Amino Acid", required = true)
    public Map<String, Integer> getVariantAminoAcid()
    {
        return variantAminoAcid;
    }

    public void setVariantAminoAcid(Map<String, Integer> variantAminoAcid)
    {
        this.variantAminoAcid = variantAminoAcid;
    }

    @ApiModelProperty(value = "Number of Tumors", required = true)
    public Integer getTumorCount()
    {
        return tumorCount;
    }

    public void setTumorCount(Integer tumorCount)
    {
        this.tumorCount = tumorCount;
    }

    @ApiModelProperty(value = "Tumor Type Composition", required = true)
    public Map<String, Integer> getTumorTypeComposition()
    {
        return tumorTypeComposition;
    }

    public void setTumorTypeComposition(Map<String, Integer> tumorTypeComposition)
    {
        this.tumorTypeComposition = tumorTypeComposition;
    }

    @ApiModelProperty(value = "Number of Distinct Tumor Types", required = false)
    public Integer getTumorTypeCount()
    {
        return tumorTypeCount;
    }

    public void setTumorTypeCount(Integer tumorTypeCount)
    {
        this.tumorTypeCount = tumorTypeCount;
    }

    // TODO this is a manual mapping / field copy from Mutation -> HotspotMutation
    public void init(Mutation mutation)
    {
        setHugoSymbol(mutation.getHugoSymbol());
        setTumorCount(mutation.getTumorCount());
        setTumorTypeComposition(mutation.getTumorTypeComposition());
        setTumorTypeCount(mutation.getTumorTypeCount());
        setResidue(mutation.getResidue());
        setVariantAminoAcid(mutation.getVariantAminoAcid());
    }
}
