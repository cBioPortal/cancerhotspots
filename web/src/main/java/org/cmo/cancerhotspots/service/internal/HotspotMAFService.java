package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

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
        }

        return this.mafCache;
    }
}
