package org.cmo.cancerhotspots.controller;

import org.cmo.cancerhotspots.persistence.MutationRepository;
import org.cmo.cancerhotspots.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Selcuk Onur Sumer
 */
//@RestController // shorthand for @Controller, @ResponseBody
@RequestMapping(value = "/admin")
public class AdminController
{
    private final MutationRepository mutationRepository;
    private final DataImportService dataImportService;

    @Autowired
    public AdminController(MutationRepository mutationRepository,
        DataImportService dataImportService)
    {
        this.mutationRepository = mutationRepository;
        this.dataImportService = dataImportService;
    }

    @RequestMapping(value = "/create/variants",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public String createVariants()
    {
        dataImportService.createVariantFile(mutationRepository.findAll());

        return "variant file creation initialized";
    }

    @RequestMapping(value = "/create/hotspots",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public String createHotspots()
    {
        dataImportService.createHotspotFile(mutationRepository.findAll());

        return "hotspots file creation initialized";
    }

    @RequestMapping(value = "/create/variant_composition",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public String generateVariantComposition()
    {
        dataImportService.generateVariantComposition(
            mutationRepository.findAll());

        return "variant composition extraction initialized";
    }

    @RequestMapping(value = "/create/tumor_type_composition",
        method = {RequestMethod.GET, RequestMethod.POST},
        produces = "application/json")
    public String generateTumorTypeComposition()
    {
        dataImportService.generateTumorTypeComposition(
            mutationRepository.findAll());

        return "tumor type composition extraction initialized";
    }
}
