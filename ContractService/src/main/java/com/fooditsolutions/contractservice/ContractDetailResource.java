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
     * @param contractID is the ID of the contract in question and is send forward along with the request in the url.
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

    /**
     * The endpoint to get the details of a specific contract.
     *
     * @param contractID is the ID of the contract in question and is send forward along with the request in the url.
     * @return takes the received value from the datastoreService, turns it into more usable information and sends it back.
     */
    @GET
    @Path("/noCalc/{ContractID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getContractDetailsNoCalculate(@PathParam("ContractID") String contractID){
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + contractID + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        //System.out.println("getContractDetails: "+responseString);
        return responseString;
    }

    /**
     * retrieves a single ContractDetail object based on the given ID
     * @param contractDetailID the ID for the object.
     * @return a single object.
     * @throws IOException
     */
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
     * Those with whatToDo set to 'C' are sent to the POST endpoint in this class.
     * Those with whatToDo set to 'D' are sent to the DELETE endpoint in this class.
     * Those with whatToDo set to 'U' have their new values filtered out and send to the PUT endpoint in the datastore service.
     * A History object is also created for and send to the datastore service.
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
                    deleteContractDetails(contractDetails[i].getID(),name);
                }
            }
        }
        if (detailsToCreate.size() > 0) {
            ContractDetail[] detailsToCreateArray = detailsToCreate.toArray(new ContractDetail[0]);
            createContractDetails(detailsToCreateArray,name);
        }

        if (detailsToUpdate.size() > 0) {
            ContractDetail[] originalContractDetails = new ContractDetail[detailsToUpdate.size()];
            ContractDetail[] detailDifferences = new ContractDetail[detailsToUpdate.size()];
            List<String> historyDesc = new ArrayList<>();
            String desc = "";

            for (int i = 0; i < detailsToUpdate.size(); i++) {
                //get the original values to compare with
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
                            //compare to original value with the value from the to be updated object.
                            if (value2 != null && !field.getName().equals("moduleId") && !field.getName().equals("whatToDo")) {
                                if (value1 == null || !value1.equals(value2)) {
                                    counter++;
                                    //put the new value into a different object to be sent to the db.
                                    if (newDetail) {
                                        field.set(detailDifferences[i], value2);
                                    } else {
                                        field.set(detailDifferences[i], value2);
                                    }
                                    //set the description for the history object.
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

    /**
     * Deletes a single ContractDetail object and the associated History objects.
     * @param id the id of the object
     * @param name the name of the person who initiated the delete process.
     */
    @DELETE
    @Path("/{id}/{name}")
    public void deleteContractDetails(@PathParam("id") int id,@PathParam("name") String name) throws IOException {
        ContractDetail detail=getContractDetail(id);
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + id + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());

        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_historyservice()+"/history"+id);

        History history = new History();
        history.setAttribute("contractDetail");
        history.setAttribute_id(detail.getContract_ID());
        history.setAction(Action.DELETE);
        history.setActor(name);
        if (!detail.isHasFreeLine()){
            history.setDescription("Name module: "+detail.getModuleId().getName());
        }else{
            history.setDescription("Free line: "+detail.getFreeLine());
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = mapper.writeValueAsString(history);
        HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);
    }

    /**
     * Deletes all details of a contract along with their History objects.
     * @param contractID the ID of the contract where the details need to be deleted.
     */
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

    /**
     * Deletes a single contractDetail object along with its history without creating a History object for it.
     * @param id the id for the object
     */
    @DELETE
    @Path("/noHistory/{id}")
    public void deleteContractDetailsNoHistory(@PathParam("id") int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contractDetail/" + id + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        HttpController.httpDelete("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api"+"/history"+id);
    }
}
