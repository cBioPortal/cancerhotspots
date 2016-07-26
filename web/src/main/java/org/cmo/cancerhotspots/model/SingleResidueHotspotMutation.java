package org.cmo.cancerhotspots.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
public class SingleResidueHotspotMutation extends HotspotMutation
{
    private String qValue;

    @ApiModelProperty(value = "Q-value", required = true)
    public String getqValue()
    {
        return qValue;
    }

    public void setqValue(String qValue)
    {
        this.qValue = qValue;
    }
}
