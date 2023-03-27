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
     * Receives and sends forward a request for all stored contracts.
     * @return gives back a json string containing all of said contracts.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<Contract> getContracts() throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("getContracts: "+responseString);
        List<Contract> response = new ArrayList<>();
        response = ContractController.createContractsInformation(responseString);
        return response;
    }

    /**
     * Incomplete function. It is meant to receive and sends forward a request for a specific Contract depending on the id send with the request.
     * It now instead returns "Hello world", acting as a placeholder.
     */
    @GET
    @Produces("application/json")
    @Path("/{contractId}")
    public Contract getContract(@PathParam("contractId") int contractId) throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract/"+contractId+"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("getContracts: "+responseString);
        Contract response = new Contract();
        response = ContractController.createContractInformation(responseString);
        return response;
    }

    /**
     * The endpoint to update the information of a contract.
     * @param contract is immediately parsed back into a json string and send forward to the datastoreService.
     */
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

    /**
     * The endpoint to create a new contract.
     * @param contract is immediately parsed back into a json string and send forward to the datastoreService.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void createContract(Contract contract) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contract);
        System.out.println(jsonString);

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);
    }

}
