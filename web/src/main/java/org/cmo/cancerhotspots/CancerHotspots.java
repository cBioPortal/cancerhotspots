package org.cmo.cancerhotspots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Selcuk Onur Sumer
 */
@SpringBootApplication // shorthand for @Configuration, @EnableAutoConfiguration, @ComponentScan
//@EnableSwagger2 // enable swagger2 documentation
public class CancerHotspots extends SpringBootServletInitializer
{
    public static void main(String[] args)
    {
        SpringApplication.run(CancerHotspots.class, args);
    }
}
