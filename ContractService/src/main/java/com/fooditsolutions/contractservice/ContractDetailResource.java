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
import java.util.ArrayList;
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
    public List<ContractDetail> getContractDetails(@PathParam("ContractID") String contractID) throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail/"+ contractID +"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        System.out.println("getContractDetails: "+responseString);

        List<ContractDetail> contractDetails = ContractDetailController.createContractDetailInformation(responseString);
        List<ContractDetail> newContractDetails= new ArrayList<>();
        for (ContractDetail contractDetail: contractDetails){
            contractDetail=ContractDetailController.calculate(contractDetail);
            newContractDetails.add(contractDetail);
        }

        return newContractDetails;
    }

    /**
     * Receives al ContractDetail objects from the edit page.
     * Those with whatToDo set to 'U' are sent to the PUT endpoint in the datastore service.
     * Those with whatToDo set to 'C' are sent to the POST endpoint in this class.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void updateContractDetails(ContractDetail[] contractDetails) throws IOException {
        List<ContractDetail> detailsToUpdate=new ArrayList<>();
        List<ContractDetail> detailsToCreate=new ArrayList<>();
        for (int i=0;i<contractDetails.length;i++){
            if (contractDetails[i].getWhatToDo() !=null){
                if (contractDetails[i].getWhatToDo().contains("U")){
                    detailsToUpdate.add(contractDetails[i]);
                }else if(contractDetails[i].getWhatToDo().contains("C")){
                    detailsToCreate.add(contractDetails[i]);
                } else if (contractDetails[i].getWhatToDo().contains("D")) {
                    deleteContractDetails(contractDetails[i].getID());
                }
            }
        }
        ContractDetail[] detailsToCreateArray=new ContractDetail[detailsToCreate.size()];
        createContractDetails(detailsToCreate.toArray(detailsToCreateArray));

        if (detailsToUpdate.size()>0){
            //Creating the ObjectMapper object
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //Converting the Object to JSONString
            String jsonString = mapper.writeValueAsString(detailsToUpdate);
            System.out.println(jsonString);

            HttpController.httpPut(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail?datastoreKey="+ PropertiesController.getProperty().getDatastore(), jsonString);
        }


    }

    /**
     * @param contractDetails array of ContractDetails objects that are send to the POST endpoint in the datastore service
     */
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
    @DELETE
    @Path("/{id}")
    public void deleteContractDetails(@PathParam("id") int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contractDetail/"+id+"?datastoreKey="+PropertiesController.getProperty().getDatastore());
    }
}
