package com.sunfield.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamicRoute")
public class DynamicRouteController {

    @Autowired
    private DynamicRouteService service;

    @RequestMapping("/buildRouteDefinition")
    public String buildRouteDefinition(String id,String url,String path) {
        return service.buildRouteDefinition(id,url,path);
    }
    @RequestMapping("/allReplace")
    public String allReplace(String routeDefinition) {
        return service.allReplace(routeDefinition);
    }

}
