package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.TumorTypeComposition;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantService
{
    TumorTypeComposition getVariantComposition(String aminoAcidChange);
    TumorTypeComposition getVariantComposition(String hugoSymbol, String aminoAcidChange);
    List<TumorTypeComposition> getAllVariantCompositions();
}
