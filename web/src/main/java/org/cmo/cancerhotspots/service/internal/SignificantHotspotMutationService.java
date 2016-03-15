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
