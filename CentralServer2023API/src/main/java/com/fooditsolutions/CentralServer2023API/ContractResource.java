package com.fooditsolutions.CentralServer2023API;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.CentralServer2023API.enums.ModuleCompare;
import com.fooditsolutions.CentralServer2023API.model.CompareContractCS;
import com.fooditsolutions.CentralServer2023API.model.Contract;
import com.fooditsolutions.CentralServer2023API.model.ContractDetail;
import com.fooditsolutions.CentralServer2023API.model.Module;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fooditsolutions.CentralServer2023API.controller.ContractController.checkContractCs;

@Path("/crudContract")
public class ContractResource {

    @PostConstruct
    public void init() {
        System.out.println("Api Service started");
    }

    /**
     * sends forward GET request for all contracts.
     * The recieved value is then returned back.
     */
    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getContracts() throws IOException, ServletException {
        String response = "";
        System.out.println("Starting read in ContractResource");

        response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/contract");
        System.out.println("getContracts: "+response);



            return response;

    }

    @GET
    @Path("/{contractId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public Contract getContact(@PathParam("contractId") String contractID) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String responseContract = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/contract/"+contractID);
        byte[] jsonData2 = responseContract.getBytes();
        Contract contract = mapper.readValue(jsonData2, Contract.class);

        return contract;
    }

    /**
     * sends forward GET request for the contract details of whatever ID is send along.
     * The recieved value is then returned back.
     */
    @GET
    @Path("/{ContractID}/contractdetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<ContractDetail> getContractDetails(@PathParam("ContractID") String contractID,
                                                   @QueryParam("checkCS") boolean checkCS) throws IOException, ServletException {
        Contract contract = getContact(contractID);

        System.out.println("Starting read in ContractResource");
        String responseContractDetails = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/contractDetail/"+contractID);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
         byte[] jsonData = responseContractDetails.getBytes();
        ContractDetail[] contractDetails = mapper.readValue(jsonData, ContractDetail[].class);

        List<ContractDetail> contractDetailList = new ArrayList<>(Arrays.asList(contractDetails));

        if(checkCS){
            List<CompareContractCS> compareContractCSList = getCSDif(contract.getId());
            for(CompareContractCS com : compareContractCSList){
                if(com.getModuleSyncStatus()== ModuleCompare.CENTRALSERVER){
                    ContractDetail contractDetail = new ContractDetail();
                    contractDetail.setContract_ID(contract.id);
                    contractDetail.setModuleId(com.getModuleId());
                    contractDetail.setModule_DBB_ID(com.getModuleId().getDbb_id());
                    contractDetailList.add(contractDetail);
                }
            }
        }


        return contractDetailList;
    }

    /**
     * The endpoint called to update a single contract's general information.
     * @param contract is immediately parsed to a json string and send forward to the contract service.
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

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_contractservice()+"/contract", jsonString);
    }

    /**
     * The endpoint called to update a contract's details.
     * @param contractDetails is immediately parsed to a json String and send forward to the contracts service.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/detail")
    public void updateContractDetails(ContractDetail[] contractDetails) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contractDetails);
        System.out.println(jsonString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_contractservice()+"/contractDetail?datastoreKey=", jsonString);
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

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_contractservice()+"/contract", jsonString);
    }

    @Path("/detail")
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

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_contractservice() + "/contractDetail", jsonString);
    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/{ContractID}/csdif")
    public List<CompareContractCS> getCSDif(@PathParam("ContractID") int contractID) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ContractDetail> contractDetail = getContractDetails(String.valueOf(contractID),false);

        String responseContract = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/contract/"+contractID);
        byte[] jsonData2 = responseContract.getBytes();
        Contract contract = mapper.readValue(jsonData2, Contract.class);

        String responseModule = HttpController.httpGet(PropertiesController.getProperty().getBase_url_moduleservice()+"/module?client="+contract.client.getDBB_ID());
        byte[] jsonData3 = responseModule.getBytes();
        Module[] modules = mapper.readValue(jsonData3, Module[].class);

        List<CompareContractCS> compareContractCSList =  checkContractCs(contractDetail, Arrays.asList(modules));
        return compareContractCSList;


    }

    @DELETE
    @Path("/{ContractId}")
    public void deleteContract(@PathParam("ContractId") int contractID) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_contractservice()+"/contract/"+ contractID);
    }
}
