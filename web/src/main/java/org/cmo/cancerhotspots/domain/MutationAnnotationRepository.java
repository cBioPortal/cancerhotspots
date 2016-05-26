package org.cmo.cancerhotspots.domain;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationAnnotationRepository
{
    List<MutationAnnotation> findAll();
}
