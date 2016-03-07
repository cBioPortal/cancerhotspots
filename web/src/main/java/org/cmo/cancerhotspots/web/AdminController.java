package org.cmo.cancerhotspots.web;

import org.cmo.cancerhotspots.service.HotspotMutationService;
import org.cmo.cancerhotspots.service.VariantDataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    private final VariantDataImportService variantImportService;

    @Autowired
    public AdminController(HotspotMutationService hotspotMutationService,
        VariantDataImportService variantImportService)
    {
        this.hotspotMutationService = hotspotMutationService;
        this.variantImportService = variantImportService;
    }

//    @RequestMapping(value = "/create/variants",
//        method = {RequestMethod.GET, RequestMethod.POST},
//        produces = "application/json")
    public String createVariants()
    {
        variantImportService.createVariantFile(hotspotMutationService.getAllHotspotMutations());

        return "variant file creation initialized";
    }

}
