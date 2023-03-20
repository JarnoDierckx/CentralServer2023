package com.fooditsolutions.contractservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fooditsolutions.contractservice.controller.ContractDetailController;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.contractservice.model.ContractDetail;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/contractDetail")
public class ContractDetailResource {


    /**
     * sends forward GET request for the contract details of whatever ID is send along.
     * The recieved value is then returned back.
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
}
