package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.model.MutationAnnotation;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationAnnotationService
{
    List<MutationAnnotation> getAllMutationAnnotations();
}
