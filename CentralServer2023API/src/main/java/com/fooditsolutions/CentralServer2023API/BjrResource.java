package com.fooditsolutions.CentralServer2023API;


import com.fooditsolutions.CentralServer2023API.model.Bjr;
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
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/bjr?datastoreKey=");
        return responseString;
    }
}
