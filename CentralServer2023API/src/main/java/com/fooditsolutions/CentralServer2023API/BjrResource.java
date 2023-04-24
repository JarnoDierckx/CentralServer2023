package com.fooditsolutions.CentralServer2023API;


import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("/bjr")
public class BjrResource {

    @GET
    @Produces("application/json")
    public String getBjrs() throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/bjr");
        return responseString;
    }
}
