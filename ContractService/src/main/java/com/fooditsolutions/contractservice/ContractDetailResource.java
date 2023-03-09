package com.fooditsolutions.contractservice;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

@Path("/contractDetail")
public class ContractDetailResource {


    /**
     * sends forward GET request for the contract details of whatever ID is send along.
     * The recieved value is then returned back.
     */
    @GET
    @Path("/{ContractID}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getContractDetails(@PathParam("ContractID") String contractID) {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail/"+ contractID +"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("getContractDetails: "+responseString);

        return responseString;
    }
}
