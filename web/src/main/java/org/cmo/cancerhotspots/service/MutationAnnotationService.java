package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.MutationAnnotation;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationAnnotationService
{
    List<MutationAnnotation> getAllMutationAnnotations();
}
