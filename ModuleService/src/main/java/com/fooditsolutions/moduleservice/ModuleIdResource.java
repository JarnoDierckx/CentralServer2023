package com.fooditsolutions.moduleservice;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/moduleid")
public class ModuleIdResource {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getModuleIds() {
        String urlString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/moduleid?datastoreKey="+PropertiesController.getProperty().getDatastore());
        return urlString;
    }
}
