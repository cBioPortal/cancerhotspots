package org.cmo.cancerhotspots.domain;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationAnnotationRepository
{
    Iterable<MutationAnnotation> findAll();
}
