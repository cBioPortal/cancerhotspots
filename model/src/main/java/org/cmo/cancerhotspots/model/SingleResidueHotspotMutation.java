package org.cmo.cancerhotspots.model;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
public class SingleResidueHotspotMutation extends HotspotMutation
{
    private String qValue;
    private String qValuePancan;
    private String qValueCancerType;
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

    @ApiModelProperty(value = "Q-value (Cancer Type)", required = true)
    public String getqValueCancerType()
    {
        return qValueCancerType;
    }

    public void setqValueCancerType(String qValueCancerType)
    {
        this.qValueCancerType = qValueCancerType;
    }

    @ApiModelProperty(value = "Q-value (Pancan)", required = true)
    public String getqValuePancan()
    {
        return qValuePancan;
    }

    public void setqValuePancan(String qValuePancan)
    {
        this.qValuePancan = qValuePancan;
    }
}
