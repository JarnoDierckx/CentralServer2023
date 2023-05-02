package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.History;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/history")
public class HistoryResource {

    @GET
    @Produces("application/json")
    public String retrieveHistory(@QueryParam("full") boolean full){
        String response= HttpController.httpGet("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api"+"/history?full="+full);
        return response;
    }

}
