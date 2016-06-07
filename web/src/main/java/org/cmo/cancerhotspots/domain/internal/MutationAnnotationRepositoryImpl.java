package org.cmo.cancerhotspots.domain.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.domain.MutationAnnotationRepository;
import org.cmo.cancerhotspots.util.FileIO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Repository
public class MutationAnnotationRepositoryImpl implements MutationAnnotationRepository
{
    private String mafUri;
    @Value("${hotspot.maf.uri}")
    public void setMafUri(String mafUri)
    {
        this.mafUri = mafUri;
    }

    private List<MutationAnnotation> cache;

    public Iterable<MutationAnnotation> findAll()
    {
        // parse the input file only once,
        // and save the result in the maf cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            BeanListProcessor<MutationAnnotation> rowProcessor =
                new BeanListProcessor<>(MutationAnnotation.class);

            CsvParser mafParser = FileIO.initCsvParser(rowProcessor);
            mafParser.parse(FileIO.getReader(mafUri));

            // cache retrieved beans
            this.cache = rowProcessor.getBeans();
        }

        return this.cache;
    }
}
