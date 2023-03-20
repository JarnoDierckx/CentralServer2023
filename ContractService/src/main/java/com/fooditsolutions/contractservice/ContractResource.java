package com.fooditsolutions.contractservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.controller.ClientController;
import com.fooditsolutions.contractservice.controller.ContractController;
import com.fooditsolutions.contractservice.model.Client;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.contractservice.model.ContractDetail;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.controller.HttpController;
import com.google.gson.Gson;


import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

@Path("/contract")
public class ContractResource {
    @PostConstruct
    public void init(){
        System.out.println("ContractService");
    }

    /**
     * Simply sends the recieved request forward and the then returned value to where it recieved the initial request from.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<Contract> getContracts() throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("getContracts: "+responseString);
        List<Contract> response = new ArrayList<>();
        response = ContractController.createContractInformation(responseString);
        return response;
    }

    @GET
    @Produces("application/json")
    @Path("/{contractId}")
    public String hello(@PathParam("contractId") int contractId) {
        return "Hello, World!";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void updateContract(Contract contract) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contract);
        System.out.println(jsonString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);
    }
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/detail")
    public void updateContractDetails(ContractDetail[] contract) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contract);
        System.out.println(jsonString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract/detail?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);
    }
}
