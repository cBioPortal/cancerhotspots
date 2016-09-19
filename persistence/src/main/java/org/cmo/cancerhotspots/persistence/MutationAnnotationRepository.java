package org.cmo.cancerhotspots.persistence;

import org.cmo.cancerhotspots.model.MutationAnnotation;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationAnnotationRepository
{
    Iterable<MutationAnnotation> findAll();
}
