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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.cmo.cancerhotspots.data.IntegerRange;

import java.util.Map;

/**
 * @author Selcuk Onur Sumer
 */
public class HotspotMutation
{
    private String hugoSymbol;
    private String residue;
    private Map<String, Integer> variantAminoAcidMap;
    private Integer tumorTypeCount;
    private Integer tumorCount;
    private Map<String, Integer> tumorTypeCompositionMap;
    private String transcriptId;
    private IntegerRange aminoAcidPosition;

    @ApiModelProperty(value = "Hugo gene symbol", required = true)
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
    public Object getVariantAminoAcid()
    {
        return getVariantAminoAcidMap();
    }

    @JsonIgnore
    public Map<String, Integer> getVariantAminoAcidMap()
    {
        return variantAminoAcidMap;
    }

    public void setVariantAminoAcidMap(Map<String, Integer> variantAminoAcidMap)
    {
        this.variantAminoAcidMap = variantAminoAcidMap;
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
    public Object getTumorTypeComposition()
    {
        return getTumorTypeCompositionMap();
    }

    @JsonIgnore
    public Map<String, Integer> getTumorTypeCompositionMap()
    {
        return tumorTypeCompositionMap;
    }

    public void setTumorTypeCompositionMap(Map<String, Integer> tumorTypeCompositionMap)
    {
        this.tumorTypeCompositionMap = tumorTypeCompositionMap;
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

    @ApiModelProperty(value = "Ensemble Transcript Id", required = false)
    public String getTranscriptId()
    {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId)
    {
        this.transcriptId = transcriptId;
    }

    @ApiModelProperty(value = "Amino Acid Position", required = false)
    public IntegerRange getAminoAcidPosition() {
        return aminoAcidPosition;
    }

    public void setAminoAcidPosition(IntegerRange aminoAcidPosition) {
        this.aminoAcidPosition = aminoAcidPosition;
    }

    // TODO this is a manual mapping / field copy from Mutation -> HotspotMutation
    public void init(Mutation mutation)
    {
        setHugoSymbol(mutation.getHugoSymbol());
        setTumorCount(mutation.getTumorCount());
        setTumorTypeCompositionMap(mutation.getTumorTypeComposition());
        setTumorTypeCount(mutation.getTumorTypeCount());
        setResidue(mutation.getResidue());
        setVariantAminoAcidMap(mutation.getVariantAminoAcid());
        setTranscriptId(mutation.getTranscriptId());
        setAminoAcidPosition(mutation.getAminoAcidPosition());
    }
}
