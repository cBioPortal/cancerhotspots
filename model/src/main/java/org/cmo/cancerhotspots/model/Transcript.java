package org.cmo.cancerhotspots.model;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.annotations.Trim;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Selcuk Onur Sumer
 */
public class Transcript
{
    @Trim
    @Parsed(field = "enst_id")
    @ApiModelProperty(value = "Ensembl transcript id", required = true)
    private String transcriptId;

    @Trim
    @Parsed(field = "gene_name")
    @ApiModelProperty(value = "Hugo gene symbol")
    private String geneSymbol;

    public String getGeneSymbol()
    {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol)
    {
        this.geneSymbol = geneSymbol;
    }

    public String getTranscriptId()
    {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId)
    {
        this.transcriptId = transcriptId;
    }
}
