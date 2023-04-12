package com.fooditsolutions.contractservice;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/server")
public class ServerResource {
    @GET
    @Produces("application/json")
    public String getServers() throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/server/all?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        return responseString;
    }
}
