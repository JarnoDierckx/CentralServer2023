package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.History;

import javax.ws.rs.*;

@Path("/history")
public class HistoryResource {

    @GET
    @Produces("application/json")
    public String retrieveHistory(@QueryParam("full") boolean full){
        String response= HttpController.httpGet("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api"+"/history?full="+full);
        return response;
    }

    @GET
    @Path("/deleted")
    @Produces("application/json")
    public String retrieveHistoryDeletedContracts(@QueryParam("full") boolean full){
        String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_historyservice()+"/history/deleted?full="+full);
        return response;
    }

    @GET
    @Path("/{ATTRIBUTE}/{ATTRIBUTEID}")
    @Produces("application/json")
    public String retrieveHistoryAttributeID(@PathParam("ATTRIBUTE") String attribute,
                                             @PathParam("ATTRIBUTEID") String attributeid,@QueryParam("full") boolean full){
        String response= HttpController.httpGet("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api"+"/history/"+attribute+"/"+ attributeid+"?full="+full);
        return response;
    }

}
