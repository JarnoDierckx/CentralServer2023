package com.fooditsolutions.contractservice;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/clients")
public class ClientResource {

    /**
     * Retrieves all clients stored in the database.
     */
    @GET
    @Produces("application/json")
    public String getClients() {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        return responseString;
    }
}
