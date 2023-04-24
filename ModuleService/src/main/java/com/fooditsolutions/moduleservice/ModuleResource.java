package com.fooditsolutions.moduleservice;

import com.fooditsolutions.moduleservice.controller.ModuleController;
import com.fooditsolutions.util.model.Module;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/module")
public class ModuleResource {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<Module> getObjects(@QueryParam("client") String clientId) throws IOException {
        String urlString =PropertiesController.getProperty().getBase_url_datastoreservice()+"/module?datastoreKey="+ PropertiesController.getProperty().getDatastore();
        if(clientId != null && clientId != ""){
            urlString += "&client="+ clientId;
        }
        String responseString = HttpController.httpGet(urlString);
        System.out.println("getModules: "+responseString);
        List<Module> response = new ArrayList<>();
        response = ModuleController.createModuleInformation(responseString);
        return response;
    }
}
