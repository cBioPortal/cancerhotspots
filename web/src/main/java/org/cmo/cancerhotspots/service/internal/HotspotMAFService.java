package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotMAFService implements MutationAnnotationService
{
    private String mafUri;
    @Value("${hotspot.maf.uri}")
    public void setMafUri(String mafUri)
    {
        this.mafUri = mafUri;
    }

    private List<MutationAnnotation> mafCache;

    @Override
    public List<MutationAnnotation> getAllMutationAnnotations()
    {
        // parse the input file only once,
        // and save the result in the maf cache
        if (this.mafCache == null ||
            this.mafCache.size() == 0)
        {
            BeanListProcessor<MutationAnnotation> rowProcessor =
                new BeanListProcessor<>(MutationAnnotation.class);

            CsvParser mafParser = FileIO.initCsvParser(rowProcessor);
            mafParser.parse(FileIO.getReader(mafUri));

            // cache retrieved beans
            this.mafCache = rowProcessor.getBeans();

            // post process to construct missing fields (if any)
            this.mafCache = this.postProcess(this.mafCache);
        }

        return this.mafCache;
    }

    private List<MutationAnnotation> postProcess(List<MutationAnnotation> annotations)
    {
        for (MutationAnnotation annotation : annotations)
        {
            boolean missing = annotation.getReferenceAminoAcid() == null ||
                              annotation.getAminoAcidPosition() == null ||
                              annotation.getVariantAminoAcid() == null;

            String aaChange = annotation.getAminoAcidChange();

            if (missing && aaChange != null)
            {
                // try to extract information from amino acid change
                Pattern mainPattern = Pattern.compile("[A-Za-z][0-9]+.");
                Pattern numericalPattern = Pattern.compile("[0-9]+");
                Matcher m1 = mainPattern.matcher(aaChange);

                if (m1.find())
                {
                    String match = m1.group();
                    Matcher m2 = numericalPattern.matcher(match);

                    if (m2.find())
                    {
                        String reference = match.substring(0, 1);
                        String position = m2.group();
                        String variant = match.substring(match.length() - 1);

                        annotation.setReferenceAminoAcid(reference);
                        annotation.setAminoAcidPosition(Integer.parseInt(position));
                        annotation.setVariantAminoAcid(variant);
                    }
                }
            }
        }

        return annotations;
    }
}
