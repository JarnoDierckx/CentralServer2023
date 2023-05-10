package com.fooditsolutions.contractservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.controller.ContractDetailController;
import com.fooditsolutions.util.enums.Action;
import com.fooditsolutions.util.model.ContractDetail;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.History;
import com.fooditsolutions.util.model.ModuleId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/contractDetail")
public class ContractDetailResource {


    /**
     * The endpoint to get the details of a specific contract.
     *
     * @param contractID is the ID of the contract in question and is sends forward along with the request in the url.
     * @return takes the received value from the datastoreService, turns it into more usable information and sends it back.
     */
    @GET
    @Path("/{ContractID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<ContractDetail> getContractDetails(@PathParam("ContractID") String contractID) throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + contractID + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        //System.out.println("getContractDetails: "+responseString);

        List<ContractDetail> contractDetails = ContractDetailController.createContractDetailInformation(responseString);
        List<ContractDetail> newContractDetails = new ArrayList<>();
        for (ContractDetail contractDetail : contractDetails) {
            contractDetail = ContractDetailController.calculate(contractDetail);
            newContractDetails.add(contractDetail);
        }
        return newContractDetails;
    }
    @GET
    @Path("/noCalc/{ContractID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getContractDetailsNoCalculate(@PathParam("ContractID") String contractID){
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + contractID + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        //System.out.println("getContractDetails: "+responseString);
        return responseString;
    }

    @GET
    @Path("/singleDetail/{ContractDetailID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public ContractDetail getContractDetail(@PathParam("ContractDetailID") int contractDetailID) throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + "singleDetail/" + contractDetailID + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        List<ContractDetail> contractDetails = ContractDetailController.createContractDetailInformation(responseString);
        List<ContractDetail> newContractDetails = new ArrayList<>();
        for (ContractDetail contractDetail : contractDetails) {
            contractDetail = ContractDetailController.calculate(contractDetail);
            newContractDetails.add(contractDetail);
        }

        return newContractDetails.get(0);
    }

    /**
     * Receives al ContractDetail objects from the edit page.
     * Those with whatToDo set to 'U' are sent to the PUT endpoint in the datastore service.
     * Those with whatToDo set to 'C' are sent to the POST endpoint in this class.
     */
    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void updateContractDetails(ContractDetail[] contractDetails,@PathParam("name") String name) throws IOException, IllegalAccessException {
        List<ContractDetail> detailsToUpdate = new ArrayList<>();
        List<ContractDetail> detailsToCreate = new ArrayList<>();
        for (int i = 0; i < contractDetails.length; i++) {
            if (contractDetails[i].getWhatToDo() != null) {
                if (contractDetails[i].getWhatToDo().contains("U")) {
                    detailsToUpdate.add(contractDetails[i]);
                } else if (contractDetails[i].getWhatToDo().contains("C")) {
                    detailsToCreate.add(contractDetails[i]);
                } else if (contractDetails[i].getWhatToDo().contains("D")) {
                    deleteContractDetails(contractDetails[i].getID());
                }
            }
        }
        if (detailsToCreate.size() > 0) {
            ContractDetail[] detailsToCreateArray = new ContractDetail[detailsToCreate.size()];
            createContractDetails(detailsToCreate.toArray(detailsToCreateArray),name);
        }

        if (detailsToUpdate.size() > 0) {
            ContractDetail[] originalContractDetails = new ContractDetail[detailsToUpdate.size()];
            ContractDetail[] detailDifferences = new ContractDetail[detailsToUpdate.size()];
            List<String> historyDesc = new ArrayList<>();
            String desc = "";

            for (int i = 0; i < detailsToUpdate.size(); i++) {
                originalContractDetails[i] = getContractDetail(detailsToUpdate.get(i).getID());
            }

            for (int i = 0; i < detailsToUpdate.size(); i++) {
                int counter=0;
                for (ContractDetail originalContractDetail : originalContractDetails) {
                    if (detailsToUpdate.get(i).getID() == originalContractDetail.getID()) {
                        boolean newDetail = false;
                        if (detailDifferences[i] == null) {
                            detailDifferences[i] = new ContractDetail();
                            detailDifferences[i].setID(detailsToUpdate.get(i).getID());
                            newDetail = true;
                        }
                        Field[] fields = ContractDetail.class.getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            Object value1 = field.get(originalContractDetail);
                            Object value2 = field.get(detailsToUpdate.get(i));
                            if (value2 != null && !field.getName().equals("moduleId") && !field.getName().equals("whatToDo")) {
                                if (value1 == null || !value1.equals(value2)) {
                                    counter++;
                                    if (newDetail) {
                                        field.set(detailDifferences[i], value2);
                                    } else {
                                        field.set(detailDifferences[i], value2);
                                    }
                                    desc += field.getName() + ": " + value1 + " to " + value2 + ", ";
                                }
                            }
                        }
                    }
                }
                if (!desc.equals("")) {
                    desc = desc.substring(0, desc.length() - 2);
                    historyDesc.add(desc);
                }
                desc = "";
                if (counter==0){
                    detailDifferences[i]=null;
                }
            }
            List<ContractDetail> notNullDifferences=new ArrayList<>();
            for (ContractDetail detailDifference : detailDifferences) {
                if (detailDifference != null) {
                    notNullDifferences.add(detailDifference);
                }
            }


            //Creating the ObjectMapper object
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //Converting the Object to JSONString
            String jsonString = mapper.writeValueAsString(notNullDifferences);
            //System.out.println(jsonString);

            HttpController.httpPut(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail?datastoreKey=" + PropertiesController.getProperty().getDatastore(), jsonString);

            for (int i = 0; i < notNullDifferences.size(); i++) {
                History history = new History();
                history.setAttribute("contractDetail");
                history.setAttribute_id(notNullDifferences.get(i).getID());
                history.setAction(Action.UPDATE);
                history.setDescription(String.valueOf(historyDesc.get(i)));
                history.setActor(name);

                jsonString = mapper.writeValueAsString(history);
                HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);
            }
        }


    }

    /**
     * @param contractDetails array of ContractDetails objects that are send to the POST endpoint in the datastore service
     */
    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void createContractDetails(ContractDetail[] contractDetails,@PathParam("name") String name) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contractDetails);
        //.out.println(jsonString);

        String responseString = HttpController.httpPost(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail?datastoreKey=" + PropertiesController.getProperty().getDatastore(), jsonString);
        //System.out.println(responseString);

        int[] ID = mapper.readValue(responseString, int[].class);
        for (int j : ID) {
            History history = new History();
            history.setAttribute("contractDetail");
            history.setAttribute_id(j);
            history.setAction(Action.CREATE);
            history.setActor(name);

            jsonString = mapper.writeValueAsString(history);
            HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);
        }

    }

    @DELETE
    @Path("/{id}")
    public void deleteContractDetails(@PathParam("id") int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + id + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());

        History history = new History();
        history.setAttribute("contractDetail");
        history.setAttribute_id(id);
        history.setAction(Action.DELETE);
        history.setActor("Temp");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = mapper.writeValueAsString(history);
        HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);
    }

    @DELETE
    @Path("/all/{id}")
    public void deleteAllDetails(@PathParam("id") int contractID) throws IOException {
        String response=getContractDetailsNoCalculate(String.valueOf(contractID));
        ContractDetail[] details;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        details = mapper.readValue(response, ContractDetail[].class);

        for (ContractDetail detail:details){
            deleteContractDetailsNoHistory(detail.getID());
        }
    }

    @DELETE
    @Path("/noHistory/{id}")
    public void deleteContractDetailsNoHistory(@PathParam("id") int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + id + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
    }
}
