/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal Cancer Hotspots.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cmo.cancerhotspots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Selcuk Onur Sumer
 */
@SpringBootApplication // shorthand for @Configuration, @EnableAutoConfiguration, @ComponentScan
@EnableSwagger2 // enable swagger2 documentation
public class CancerHotspots extends SpringBootServletInitializer
{
    public static void main(String[] args)
    {
        SpringApplication.run(CancerHotspots.class, args);
    }

    @Bean
    public Docket hotspotsApi() {
        // default swagger definition file location: <root>/v2/api-docs?group=api
        // default swagger UI location: <root>/swagger-ui.html
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("cancer_hotspots")
            .apiInfo(hotspotsApiInfo())
            .select()
            .paths(PathSelectors.regex("/api.*"))
            .build();
    }

//    @Bean
//    public Docket adminApi() {
//        // default swagger definition file location: <root>/v2/api-docs?group=api
//        // default swagger UI location: <root>/swagger-ui.html
//        return new Docket(DocumentationType.SWAGGER_2)
//            .groupName("cancer_hotspots_admin")
//            .apiInfo(hotspotsApiInfo())
//            .select()
//            .paths(PathSelectors.regex("/admin.*"))
//            .build();
//    }

    private ApiInfo hotspotsApiInfo() {
        return new ApiInfoBuilder()
            .title("Cancer Hotspots API")
            .description("Cancer Hotspots API")
            //.termsOfServiceUrl("http://terms-of-service-url")
            .contact("CMO, MSKCC")
            .license("GNU AFFERO GENERAL PUBLIC LICENSE Version 3")
            .licenseUrl("https://github.com/cBioPortal/cancerhotspots/blob/master/LICENSE")
            .version("2.0")
            .build();
    }
}
