package com.fooditsolutions.contractservice;

import com.fooditsolutions.contractservice.model.Bjr;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BjrResource {

    @GET
    @Produces("application/json")
    public String getContracts() throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/bjr?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        return responseString;
    }
}
