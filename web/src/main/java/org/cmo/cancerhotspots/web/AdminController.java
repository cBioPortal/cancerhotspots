package org.cmo.cancerhotspots.web;

import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.cmo.cancerhotspots.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Selcuk Onur Sumer
 */
@RestController // shorthand for @Controller, @ResponseBody
//@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value = "/admin")
public class AdminController
{
    private final HotspotMutationService hotspotMutationService;
    private final DataImportService dataImportService;

    @Autowired
    public AdminController(HotspotMutationService hotspotMutationService,
        DataImportService dataImportService)
    {
        this.hotspotMutationService = hotspotMutationService;
        this.dataImportService = dataImportService;
    }

//    @RequestMapping(value = "/create/variants",
//        method = {RequestMethod.GET, RequestMethod.POST},
//        produces = "application/json")
    public String createVariants()
    {
        dataImportService.createVariantFile(hotspotMutationService.getAllHotspotMutations());

        return "variant file creation initialized";
    }

//    @RequestMapping(value = "/create/hotspots",
//        method = {RequestMethod.GET, RequestMethod.POST},
//        produces = "application/json")
    public String createHotspots()
    {
        dataImportService.createHotspotFile(hotspotMutationService.getAllHotspotMutations());

        return "hotspots file creation initialized";
    }

//    @RequestMapping(value = "/create/variant_composition",
//        method = {RequestMethod.GET, RequestMethod.POST},
//        produces = "application/json")
    public String generateVariantComposition()
    {
        dataImportService.generateVariantComposition(
            hotspotMutationService.getAllHotspotMutations());

        return "variant composition extraction initialized";
    }

//    @RequestMapping(value = "/create/tumor_type_composition",
//        method = {RequestMethod.GET, RequestMethod.POST},
//        produces = "application/json")
    public String generateTumorTypeComposition()
    {
        dataImportService.generateTumorTypeComposition(
            hotspotMutationService.getAllHotspotMutations());

        return "tumor type composition extraction initialized";
    }
}
