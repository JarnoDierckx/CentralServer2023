package com.fooditsolutions.contractservice;

import com.fooditsolutions.contractservice.controller.HttpController;
import com.fooditsolutions.contractservice.controller.PropertiesController;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/contract")
public class ContractResource {
    @PostConstruct
    public void init(){
        System.out.println("ContractService");
    }
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public String getContracts() {
        String responseString = HttpController.httpGet("http://localhost:8080/DatastoreService-1.0-SNAPSHOT/api"+"/contract?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println(responseString);

        return responseString;
    }

    @GET
    @Produces("application/json")
    @Path("/{contractId}")
    public String hello(@PathParam("contractId") int contractId) {
        return "Hello, World!";
    }
}
