package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.model.MutationAnnotation;

/**
 * @author Selcuk Onur Sumer
 */
public interface MutationFilterService
{
    boolean filterByType(MutationAnnotation mutation);
}
