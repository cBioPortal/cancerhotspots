package org.cmo.cancerhotspots.domain;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
public class SingleResidueHotspotMutation extends HotspotMutation
{
    private String qValue;
    private Integer tumorTypeCount;

    @ApiModelProperty(value = "Q-value", required = true)
    public String getqValue()
    {
        return qValue;
    }

    public void setqValue(String qValue)
    {
        this.qValue = qValue;
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


}
