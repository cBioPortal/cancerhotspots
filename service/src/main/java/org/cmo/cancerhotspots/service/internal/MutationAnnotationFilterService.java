package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.model.MutationAnnotation;
import org.cmo.cancerhotspots.service.MutationFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class MutationAnnotationFilterService implements MutationFilterService
{
    List<String> mutationTypes;

    @Autowired
    public MutationAnnotationFilterService(
        @Value("${hotspot.filter.mutationType}") String mutationTypesFilter)
    {
        this.mutationTypes = this.parseList(mutationTypesFilter);
    }

    private List<String> parseList(String listStr)
    {
        String[] parts = listStr.split(",");
        return Arrays.asList(parts);
    }

    public List<String> getMutationTypes()
    {
        return mutationTypes;
    }

	/**
     * If the annotation's variant classification is one of the provided
     * mutation types, then the mutation is accepted.
     *
     * @param annotation mutation annotation instance
     * @return  true if mutation type matches the variant classification
     */
    public boolean filterByType(MutationAnnotation annotation)
    {
        boolean filter = false;

        for (String type : mutationTypes)
        {
            String type1 = annotation.getVariantClassification().toLowerCase();
            String type2 = type.toLowerCase();

            if(type1.contains(type2) ||
               type2.contains(type1))
            {
                filter = true;
                break;
            }
        }

        return filter;
    }
}
