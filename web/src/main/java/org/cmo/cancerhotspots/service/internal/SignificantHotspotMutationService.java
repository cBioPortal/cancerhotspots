package org.cmo.cancerhotspots.service.internal;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class SignificantHotspotMutationService implements HotspotMutationService
{
    @Autowired
    private ResourceLoader resourceLoader;

    private String hotspotMutationUri;
    @Value("${significant.hotspot.uri}")
    public void setHotspotMutationUri(String hotspotMutationUri) { this.hotspotMutationUri = hotspotMutationUri; }

    private List<HotspotMutation> cache;

    public List<HotspotMutation> getAllHotspotMutations()
    {
        // parse the input file only once, and save the result in the cache
        if (this.cache == null ||
            this.cache.size() == 0)
        {
            BeanListProcessor<HotspotMutation> rowProcessor =
                new BeanListProcessor<HotspotMutation>(HotspotMutation.class);

            CsvParserSettings parserSettings = new CsvParserSettings();
            parserSettings.setHeaderExtractionEnabled(true);
            parserSettings.getFormat().setDelimiter('\t');
            parserSettings.setRowProcessor(rowProcessor);

            CsvParser parser = new CsvParser(parserSettings);
            parser.parse(getReader(hotspotMutationUri));

            // cache retrieved beans
            this.cache = rowProcessor.getBeans();
        }

        return this.cache;
    }

    /**
     * Creates a reader for a resource in the relative path
     *
     * @param relativePath path of the resource to be read
     * @return a reader of the resource
     */
    public Reader getReader(String relativePath)
    {
        Resource resource = resourceLoader.getResource("classpath:" + relativePath);

        try {
            //return new InputStreamReader(this.getClass().getResourceAsStream(relativePath), "UTF-8");
            return new InputStreamReader(resource.getInputStream(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to read input", e);
        } catch (IOException e) {
            throw new IllegalStateException("Input not found", e);
        }
    }
}
