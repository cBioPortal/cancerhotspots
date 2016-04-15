package org.cmo.cancerhotspots.service;

import org.cmo.cancerhotspots.domain.HotspotMutation;
import org.cmo.cancerhotspots.domain.VariantComposition;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface VariantService
{
    VariantComposition getVariantComposition(String aminoAcidChange);
    VariantComposition getVariantComposition(String hugoSymbol, String aminoAcidChange);
    List<VariantComposition> getAllVariantCompositions();
}
