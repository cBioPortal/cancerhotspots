package org.cmo.cancerhotspots.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

// Copied mainly from https://github.com/cBioPortal/cbioportal/blob/041c3e4e2dde0d4e7d99fa79d13d1bb1b0d502a2/web/src/main/java/org/cbioportal/proxy/ProxyController.java
@RestController
@RequestMapping(value = "/proxy")
public class ProxyController {

    @RequestMapping("/**")
    public String proxy(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request)
        throws URISyntaxException {

        String queryString = request.getQueryString();
        String path = request.getServletPath().substring("/proxy".length());
        URI uri = new URI("http:/" + path + (queryString == null ? "" : "?" + queryString));

        HttpHeaders httpHeaders = new HttpHeaders();
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            httpHeaders.setContentType(MediaType.valueOf(contentType));
        }

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate.exchange(uri, method, new HttpEntity<>(body, httpHeaders), String.class).getBody();
    }
}
