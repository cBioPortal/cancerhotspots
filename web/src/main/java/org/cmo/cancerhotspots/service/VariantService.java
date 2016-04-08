package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.VariantComposition;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantService
{
    VariantComposition getVariantComposition(String aminoAcidChange);
    VariantComposition getVariantComposition(String hugoSymbol, String aminoAcidChange);
}
