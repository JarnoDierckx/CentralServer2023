package com.fooditsolutions.contractservice;

import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.controller.HttpController;


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
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract?datastoreKey="+ PropertiesController.getProperty().getDatastore());
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
