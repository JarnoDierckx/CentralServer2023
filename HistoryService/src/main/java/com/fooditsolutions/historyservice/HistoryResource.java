
package com.fooditsolutions.historyservice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.historyservice.controller.HistoryController;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.enums.Action;
import com.fooditsolutions.util.model.History;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Path("/history")
public class HistoryResource {
    @PostConstruct
    public void init(){
        System.out.println("HistoryService");
        try {
            PropertiesController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @GET
    @Produces("application/json")
    public List<History> getHistory(@QueryParam("full") boolean full) throws IOException {

        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/history?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("HistoryService: "+responseString);
        List<History> result = new ArrayList<>();
        result = HistoryController.createHistoryInformation(responseString);
        result = HistoryController.getFull(full,result);
        return result;
    }

    @GET
    @Produces("application/json")
    @Path("/{ATTRIBUTE}")
    public List<History> getHistoryAttribute(@PathParam("ATTRIBUTE") String attribute,
                                             @QueryParam("full") boolean full) throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/history/"+attribute+"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("HistoryService: "+responseString);
        List<History> result = new ArrayList<>();
        result = HistoryController.createHistoryInformation(responseString);
        result = HistoryController.getFull(full,result);
        return result;
    }



    @GET
    @Produces("application/json")
    @Path("/{ATTRIBUTE}/{ATTRIBUTEID}")
    public List<History> getHistoryAttributeId(@PathParam("ATTRIBUTE") String attribute,
                                               @PathParam("ATTRIBUTEID") String attributeid,
                                               @QueryParam("full") boolean full) throws IOException {

        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/history/"+attribute+"/"+ attributeid+"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("HistoryService: "+responseString);
        List<History> result = new ArrayList<>();
        result = HistoryController.createHistoryInformation(responseString);
        result = HistoryController.getFull(full,result);
        return result;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public void createHistory(History history) throws JsonProcessingException {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        history.setTs(ts.toString());
        String jsonString = mapper.writeValueAsString(history);
        System.out.println("HistoryService " +jsonString);

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_datastoreservice()+"/history?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);


    }

    @DELETE
    @Consumes("application/json")
    @Path("/{id}")
    public void deleteAllAssociatedHistory(@PathParam("id")int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice()+"/history/"+id+"?datastoreKey="+PropertiesController.getProperty().getDatastore());
    }

}