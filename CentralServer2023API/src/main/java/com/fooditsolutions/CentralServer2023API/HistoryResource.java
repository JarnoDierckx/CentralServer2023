package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.History;

import javax.ws.rs.*;

@Path("/history")
public class HistoryResource {

    /**
     * Retrieves all stored History objects.
     * @param full a boolean that when, if false, makes it so that the only object with an update action returned is the newest one.
     * @return a list of History objects.
     */
    @GET
    @Produces("application/json")
    public String retrieveHistory(@QueryParam("full") boolean full){
        String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_historyservice()+"/history?full="+full);
        return response;
    }

    /**
     * returns all History objects made when a Contract object was deleted.
     */
    @GET
    @Path("/deleted")
    @Produces("application/json")
    public String retrieveHistoryDeletedContracts(@QueryParam("full") boolean full){
        String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_historyservice()+"/history/deleted?full="+full);
        return response;
    }

    /**
     * Retrieves all history objects based on the given parameters.
     * @param attribute the object type the History objects refer to (Contract or ContractDetail).
     * @param attributeid the id of the object the History objects refer to.
     * @param full a boolean that when, if false, makes it so that the only object with an update action returned is the newest one.
     * @return a json object containing the History objects.
     */
    @GET
    @Path("/{ATTRIBUTE}/{ATTRIBUTEID}")
    @Produces("application/json")
    public String retrieveHistoryAttributeID(@PathParam("ATTRIBUTE") String attribute,
                                             @PathParam("ATTRIBUTEID") String attributeid,@QueryParam("full") boolean full){
        String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_historyservice()+"/history/"+attribute+"/"+ attributeid+"?full="+full);
        return response;
    }

}
