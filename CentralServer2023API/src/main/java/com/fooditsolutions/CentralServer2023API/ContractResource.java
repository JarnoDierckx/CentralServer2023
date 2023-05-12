package com.fooditsolutions.CentralServer2023API;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.CentralServer2023API.enums.ModuleCompare;
import com.fooditsolutions.CentralServer2023API.model.CompareContractCS;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.*;
import com.sun.org.apache.xpath.internal.operations.Mod;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fooditsolutions.CentralServer2023API.controller.ContractController.checkContractCs;
import static com.fooditsolutions.CentralServer2023API.controller.ContractController.checkForEmptyModule;

@Path("/crudContract")
public class ContractResource {

    List<Server> allServers=new ArrayList<>();
    List<Module> allModules=new ArrayList<>();

    @PostConstruct
    public void init() {
        System.out.println("Api Service started");
    }

    /**
     * sends forward GET request for all contracts.
     * Makes a call for all modules a servers and then checks for each contract if they have modules bound to them that don't yet have a contractDetail object.
     * The recieved value is then returned back.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<Contract> getContracts() throws IOException, ServletException {
        String response = "";
        //System.out.println("Starting read in ContractResource");

        response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice() + "/contract/");
        //System.out.println("getContracts: "+response);

        byte[] jsonData = response.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Contract> contracts = Arrays.asList(mapper.readValue(jsonData, Contract[].class));

        String serverResponse=HttpController.httpGet(PropertiesController.getProperty().getBase_url_moduleservice()+"/module/all");
        allModules=Arrays.asList(mapper.readValue(serverResponse, Module[].class));

        serverResponse=HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/server");
        allServers=Arrays.asList(mapper.readValue(serverResponse, Server[].class));

        for (Contract contract : contracts) {
            if (contract.getServer_ID()!=null && contract.getSource().equals("CS")){
                boolean hasEmpty = checkForEmptyModules(contract);
                if (hasEmpty) {
                    contract.setHasEmptyModule(true);
                }
            }
        }

        return contracts;

    }

    /**
     * makes a call for a single contract based on ID and returns it.
     */
    @GET
    @Path("/{contractId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public Contract getContract(@PathParam("contractId") String contractID) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String responseContract = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice() + "/contract/" + contractID);
        byte[] jsonData2 = responseContract.getBytes();
        Contract contract = mapper.readValue(jsonData2, Contract.class);

        return contract;
    }

    /**
     * sends forward GET request for the contract details of whatever ID is send along.
     * It then checks if the associated contract has modules that don't yet have a contractDetail object and adds them to the list.
     * The list is then returned back.
     */
    @GET
    @Path("/{ContractID}/contractdetails")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<ContractDetail> getContractDetails(@PathParam("ContractID") String contractID,
                                                   @QueryParam("checkCS") boolean checkCS) throws IOException, ServletException {
        Contract contract = getContract(contractID);

        //System.out.println("Starting read in ContractResource");
        String responseContractDetails = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice() + "/contractDetail/" + contractID);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        byte[] jsonData = responseContractDetails.getBytes();
        ContractDetail[] contractDetails = mapper.readValue(jsonData, ContractDetail[].class);

        List<ContractDetail> contractDetailList = new ArrayList<>(Arrays.asList(contractDetails));

        if (checkCS && contract.getSource().equals("CS")) {
            List<CompareContractCS> compareContractCSList = getCSDif(contract.getId());
            for (CompareContractCS com : compareContractCSList) {
                if (com.getModuleSyncStatus() == ModuleCompare.CENTRALSERVER) {
                    ContractDetail contractDetail = new ContractDetail();
                    contractDetail.setContract_ID(contract.id);
                    contractDetail.setModuleId(com.getModuleId());
                    contractDetail.setModule_DBB_ID(com.getModuleId().getDbb_id());
                    contractDetailList.add(contractDetail);
                }
            }
        }else if(contract.getSource().equals("MOB")){
            String serverResponse=HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice()+"/server");
            allServers=Arrays.asList(mapper.readValue(serverResponse, Server[].class));
            for (Server server:allServers){
                if (server.getID().equals(contract.getServer_ID())){
                    boolean hasAM=false;
                    boolean hasPM=false;
                    boolean hasBS=false;
                    boolean hasCP=false;
                    boolean hasDS=false;
                    boolean hasCA=false;
                    for (ContractDetail detail: contractDetailList){
                        if (detail.isHasFreeLine()&&detail.getFreeLine().contains("Mobile Devices NO AM")){
                            hasAM=true;
                        }
                        if (detail.isHasFreeLine()&&detail.getFreeLine().contains("Mobile Devices NO PM")){
                            hasPM=true;
                        }
                        if (detail.isHasFreeLine()&&detail.getFreeLine().contains("Mobile Devices NO BS")){
                            hasBS=true;
                        }
                        if (detail.isHasFreeLine()&&detail.getFreeLine().contains("Mobile Devices NO CP")){
                            hasCP=true;
                        }
                        if (detail.isHasFreeLine()&&detail.getFreeLine().contains("Mobile Devices NO DS")){
                            hasDS=true;
                        }
                        if (detail.isHasFreeLine()&&detail.getFreeLine().contains("Mobile Devices NO CA")){
                            hasCA=true;
                        }
                    }
                    if (server.getMobileDevicesNOAM()>0 && !hasAM){
                        ContractDetail contractDetail = new ContractDetail();
                        contractDetail.setContract_ID(contract.id);
                        contractDetail.setAmount(server.getMobileDevicesNOAM());
                        contractDetail.setFreeLine("Mobile Devices NO AM");
                        contractDetail.setHasFreeLine(true);
                        contractDetail.set_active(true);
                        contractDetailList.add(contractDetail);
                    }
                    if (server.getMobileDevicesNOPM()>0 && !hasPM){
                        ContractDetail contractDetail = new ContractDetail();
                        contractDetail.setContract_ID(contract.id);
                        contractDetail.setAmount(server.getMobileDevicesNOPM());
                        contractDetail.setFreeLine("Mobile Devices NO PM");
                        contractDetail.setHasFreeLine(true);
                        contractDetail.set_active(true);
                        contractDetailList.add(contractDetail);
                    }
                    if (server.getMobileDevicesNOBS()>0 && !hasBS){
                        ContractDetail contractDetail = new ContractDetail();
                        contractDetail.setContract_ID(contract.id);
                        contractDetail.setAmount(server.getMobileDevicesNOBS());
                        contractDetail.setFreeLine("Mobile Devices NO BS");
                        contractDetail.setHasFreeLine(true);
                        contractDetail.set_active(true);
                        contractDetailList.add(contractDetail);
                    }
                    if (server.getMobileDevicesNOCP()>0 && !hasCP){
                        ContractDetail contractDetail = new ContractDetail();
                        contractDetail.setContract_ID(contract.id);
                        contractDetail.setAmount(server.getMobileDevicesNOCP());
                        contractDetail.setFreeLine("Mobile Devices NO CP");
                        contractDetail.setHasFreeLine(true);
                        contractDetail.set_active(true);
                        contractDetailList.add(contractDetail);
                    }
                    if (server.getMobileDevicesNODS()>0 && !hasDS){
                        ContractDetail contractDetail = new ContractDetail();
                        contractDetail.setContract_ID(contract.id);
                        contractDetail.setAmount(server.getMobileDevicesNODS());
                        contractDetail.setFreeLine("Mobile Devices NO DS");
                        contractDetail.setHasFreeLine(true);
                        contractDetail.set_active(true);
                        contractDetailList.add(contractDetail);
                    }
                    if (server.getMobileDevicesNOCA()>0 && !hasCA){
                        ContractDetail contractDetail = new ContractDetail();
                        contractDetail.setContract_ID(contract.id);
                        contractDetail.setAmount(server.getMobileDevicesNOCA());
                        contractDetail.setFreeLine("Mobile Devices NO CA");
                        contractDetail.setHasFreeLine(true);
                        contractDetail.set_active(true);
                        contractDetailList.add(contractDetail);
                    }
                }
            }
        }
        return contractDetailList;
    }

    /**
     * The endpoint called to update a single contract's general information.
     *
     * @param contract is immediately parsed to a json string and send forward to the contract service.
     */
    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void updateContract(Contract contract, @PathParam("name") String name) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contract);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_contractservice() + "/contract/"+name, jsonString);
    }

    /**
     * The endpoint called to update a contract's details.
     *
     * @param contractDetails is immediately parsed to a json String and send forward to the contracts service.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/detail/{name}")
    public void updateContractDetails(ContractDetail[] contractDetails,@PathParam("name") String name) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contractDetails);
        //System.out.println(jsonString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_contractservice() + "/contractDetail/"+ name +"?datastoreKey=", jsonString);
    }

    /**
     * The endpoint to create a new contract.
     *
     * @param contract is immediately parsed back into a json string and send forward to the datastoreService.
     */
    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String createContract(Contract contract,@PathParam("name") String name) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contract);

        return HttpController.httpPost(PropertiesController.getProperty().getBase_url_contractservice() + "/contract/"+name, jsonString);
    }

    /**
     */
    @Path("/detail/{name}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void createContractDetails(ContractDetail[] contractDetails,@PathParam("name") String name) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contractDetails);
        //.out.println(jsonString);

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_contractservice() + "/contractDetail/"+name, jsonString);
    }


    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/{ContractID}/csdif")
    public List<CompareContractCS> getCSDif(@PathParam("ContractID") int contractID) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ContractDetail> contractDetail = getContractDetails(String.valueOf(contractID), false);

        String responseContract = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice() + "/contract/" + contractID);
        byte[] jsonData2 = responseContract.getBytes();
        Contract contract = mapper.readValue(jsonData2, Contract.class);

        String responseModule = HttpController.httpGet(PropertiesController.getProperty().getBase_url_moduleservice() + "/module?client=" + contract.client.getDBB_ID());
        byte[] jsonData3 = responseModule.getBytes();
        Module[] modules = mapper.readValue(jsonData3, Module[].class);

        List<CompareContractCS> compareContractCSList = checkContractCs(contractDetail, Arrays.asList(modules));
        return compareContractCSList;


    }

    @DELETE
    @Path("/{name}/{ContractId}")
    public void deleteContract(@PathParam("ContractId") int contractID,@PathParam("name") String name) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_contractservice()+"/contract/"+name+"/"+ contractID);
    }

    @DELETE
    @Path("/detail/{id}")
    public void deleteContractDetails(@PathParam("id") int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_contractservice() + "/contractDetail/" + id);
    }

    /**
     * Receives a contract object and checks if it has any associated modules that don't yet have a contractDetail object.
     */
    public boolean checkForEmptyModules(Contract contract) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<ContractDetail> contractDetail = getContractDetailsWithContract(contract, false);

        /*String responseModule = HttpController.httpGet(PropertiesController.getProperty().getBase_url_moduleservice() + "/module?client=" + contract.client.getDBB_ID());
        byte[] jsonData3 = responseModule.getBytes();
        Module[] modules = mapper.readValue(jsonData3, Module[].class);*/
        List<Module> modules=new ArrayList<>();

        Server contractServer = null;
        for (Server server:allServers){
            if (server.getID().equals(contract.getServer_ID())){
                contractServer=server;
                break;
            }
        }
        if (contractServer != null){
            for (Module module:allModules){
                assert contractServer != null;
                if (module.getSERVER_DBB_ID().equals(contractServer.getDBB_ID())){
                    modules.add(module);
                }
            }

            return checkForEmptyModule(contractDetail, modules);
        }
        return false;



    }

    /**
     * Calls for the contractDetails from the given contract.
     */
    public List<ContractDetail> getContractDetailsWithContract(Contract contract,
                                                               @QueryParam("checkCS") boolean checkCS) throws IOException, ServletException {

        //System.out.println("Starting read in ContractResource");
        String responseContractDetails = HttpController.httpGet(PropertiesController.getProperty().getBase_url_contractservice() + "/contractDetail/noCalc/" + contract.getId());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        byte[] jsonData = responseContractDetails.getBytes();
        ContractDetail[] contractDetails = mapper.readValue(jsonData, ContractDetail[].class);

        List<ContractDetail> contractDetailList = new ArrayList<>(Arrays.asList(contractDetails));

        if (checkCS) {
            List<CompareContractCS> compareContractCSList = getCSDif(contract.getId());
            for (CompareContractCS com : compareContractCSList) {
                if (com.getModuleSyncStatus() == ModuleCompare.CENTRALSERVER) {
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
}
