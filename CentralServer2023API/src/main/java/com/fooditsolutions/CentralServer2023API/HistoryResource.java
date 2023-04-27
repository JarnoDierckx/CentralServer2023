package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.History;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/history")
public class HistoryResource {

    @GET
    @Produces("application/json")
    public String retrieveHistory(){
        String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_historyservice()+"/history");
        return response;
    }

}
