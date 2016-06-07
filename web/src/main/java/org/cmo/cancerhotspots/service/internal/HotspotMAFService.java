package org.cmo.cancerhotspots.service.internal;

import org.cmo.cancerhotspots.domain.MutationAnnotation;
import org.cmo.cancerhotspots.domain.MutationAnnotationRepository;
import org.cmo.cancerhotspots.service.MutationAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class HotspotMAFService implements MutationAnnotationService
{
    private final MutationAnnotationRepository mutationAnnotationRepository;

    @Autowired
    public HotspotMAFService(MutationAnnotationRepository mutationAnnotationRepository)
    {
        this.mutationAnnotationRepository = mutationAnnotationRepository;
    }

    @Override
    public List<MutationAnnotation> getAllMutationAnnotations()
    {
        Iterable<MutationAnnotation> annotations = mutationAnnotationRepository.findAll();

        // post process to construct missing fields (if any)
        return this.postProcess(annotations);
    }

    private List<MutationAnnotation> postProcess(Iterable<MutationAnnotation> annotations)
    {
        List<MutationAnnotation> list = new ArrayList<>();

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

            list.add(annotation);
        }

        return list;
    }
}
