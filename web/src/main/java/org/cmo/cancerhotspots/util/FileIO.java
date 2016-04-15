package org.cmo.cancerhotspots.util;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * @author Selcuk Onur Sumer
 */
public class FileIO
{
    /**
     * Creates a reader for a resource in the relative/absolute path
     *
     * @param resourceURI path of the resource to be read
     * @return a reader of the resource
     */
    public static Reader getReader(String resourceURI)
    {
        // first try class path
        Resource resource = new ClassPathResource(resourceURI);

        // if not exists then try absolute path
        if (!resource.exists()) {
            resource = new PathResource(resourceURI);
        }

        try {
            return new InputStreamReader(resource.getInputStream(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to read input", e);
        } catch (IOException e) {
            throw new IllegalStateException("Input not found", e);
        }
    }

    /**
     * Creates a writer for a resource in the relative/absolute path
     *
     * @param resourceURI path of the resource to be read
     * @return a reader of the resource
     */
    public static Writer getWriter(String resourceURI)
    {
        // first try class path
        Resource resource = new ClassPathResource(resourceURI);

        // if not exists then try absolute path
        if (!resource.exists()) {
            resource = new PathResource(resourceURI);
        }

        try {
            return new OutputStreamWriter(new FileOutputStream(resource.getFile()), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to read input", e);
        } catch (IOException e) {
            throw new IllegalStateException("Input not found", e);
        }
    }

	/**
     * Initializes the CSV parser for the given bean list processor
     *
     * @param rowProcessor a BeanListProcessor instance
     * @return a CsvParser instance
     */
    public static CsvParser initCsvParser(BeanListProcessor<?> rowProcessor)
    {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.getFormat().setDelimiter('\t');
        parserSettings.setRowProcessor(rowProcessor);

        return new CsvParser(parserSettings);
    }
}
