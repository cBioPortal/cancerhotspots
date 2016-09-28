package org.cmo.cancerhotspots.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
public class SingleResidueHotspotMutation extends HotspotMutation
{
    private String qValue;
    private String type;

    @ApiModelProperty(value = "Q-value", required = true)
    public String getqValue()
    {
        return qValue;
    }

    public void setqValue(String qValue)
    {
        this.qValue = qValue;
    }

    @ApiModelProperty(value = "Type", required = true)
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
