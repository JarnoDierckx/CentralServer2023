package com.fooditsolutions.contractservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.controller.ContractDetailController;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.contractservice.model.ContractDetail;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("/contractDetail")
public class ContractDetailResource {


    /**
     * The endpoint to get the details of a specific contract.
     * @param contractID is the ID of the contract in question and is sends forward along with the request in the url.
     * @return takes the received value from the datastoreService, turns it into more usable information and sends it back.
     */
    @GET
    @Path("/{ContractID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<ContractDetail> getContractDetails(@PathParam("ContractID") String contractID) throws JsonProcessingException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail/"+ contractID +"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("getContractDetails: "+responseString);

        List<ContractDetail> contractDetails = ContractDetailController.createContractDetailInformation(responseString);


        return contractDetails;
    }

    /**
     * The endpoint to update a contract's details.
     * @param contractDetails is put into a json string and send towards the datastoreService.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void updateContractDetails(ContractDetail[] contractDetails) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contractDetails);
        System.out.println(jsonString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void createContractDetails(ContractDetail[] contractDetails) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contractDetails);
        System.out.println(jsonString);

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);
    }
}
