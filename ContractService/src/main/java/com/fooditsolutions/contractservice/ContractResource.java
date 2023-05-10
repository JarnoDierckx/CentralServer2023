package com.fooditsolutions.contractservice;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.controller.ContractController;
import com.fooditsolutions.util.enums.Action;
import com.fooditsolutions.util.model.*;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.controller.HttpController;


import javax.annotation.PostConstruct;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("/contract")
public class ContractResource {
    @PostConstruct
    public void init() {
        //System.out.println("ContractService");
    }

    /**
     * Receives and sends forward a request for all stored contracts.
     *
     * @return gives back a json string containing all of said contracts.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<Contract> getContracts() throws IOException {
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contract?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        //System.out.println("getContracts: " + responseString);
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
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contract/" + contractId + "?datastoreKey=" + PropertiesController.getProperty().getDatastore());
        //System.out.println("getContracts: " + responseString);
        Contract response = new Contract();
        response = ContractController.createContractInformation(responseString);
        return response;
    }

    /**
     * The endpoint to update the information of a contract.
     *
     * @param contract is immediately parsed back into a json string and send forward to the datastoreService.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/{name}")
    public void updateContract(Contract contract,@PathParam("name")String name) throws IOException, IllegalAccessException {
        StringBuilder desc = new StringBuilder();
        Contract originalContract = getContract(contract.getId());
        Contract differences = new Contract();
        Field[] fields = originalContract.getClass().getDeclaredFields();
        int counter = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value1 = field.get(originalContract);
            Object value2 = field.get(contract);
            if (value2 == null) {
                if (value1 != null) {
                    field.set(differences, value2);
                    desc.append(field.getName()).append(": ").append(value1).append(" to ").append(value2).append(", ");
                    counter++;
                }
            } else if (value1 == null || !value1.equals(value2)) {
                if (!(value2.getClass().equals(Client.class)) && !(value2.equals(true))) {
                    if (!isNullOrZero(value1) || !isNullOrZero(value2)) {
                        field.set(differences, value2);
                        desc.append(field.getName()).append(": ").append(value1).append(" to ").append(value2).append(", ");
                        counter++;
                    }
                }
            }
        }
        if (counter > 0) {
            differences.id = contract.id;
            //Creating the ObjectMapper object
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            //Converting the Object to JSONString
            String jsonString = mapper.writeValueAsString(contract);
            //System.out.println(jsonString);

            HttpController.httpPut(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contract?datastoreKey=" + PropertiesController.getProperty().getDatastore(), jsonString);

            desc= new StringBuilder(desc.substring(0, desc.length() - 2));

            History history = new History();
            history.setAttribute("contract");
            history.setAttribute_id(contract.getId());
            history.setAction(Action.UPDATE);
            history.setDescription(String.valueOf(desc));
            history.setActor(name);

            jsonString = mapper.writeValueAsString(history);
            HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);
        }
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
    public String createContract(Contract contract,@PathParam("name")String name) throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(contract);
        //System.out.println(jsonString);

        String ID = HttpController.httpPost(PropertiesController.getProperty().getBase_url_datastoreservice() + "/contract?datastoreKey=" + PropertiesController.getProperty().getDatastore(), jsonString);

        History history = new History();
        history.setAttribute("contract");
        history.setAttribute_id(Long.parseLong(ID));
        history.setAction(Action.CREATE);
        history.setActor(name);

        jsonString = mapper.writeValueAsString(history);
        HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);

        return ID;
    }

    @DELETE
    @Path("/{name}/{ContractId}")
    public void deleteContract(@PathParam("ContractId") int contractID,@PathParam("name") String name) throws IOException {
        Contract contract= getContract(contractID);
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_datastoreservice()+"/contract/"+ contractID +"?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_contractservice()+"/contractDetail/all/"+contractID);

        History history =new History();
        history.setAttribute("contract");
        history.setAttribute_id(contractID);
        history.setAction(Action.DELETE);
        history.setActor(name);
        history.setDescription("Server ID:"+contract.getServer_ID()+", contract number: "+contract.getContract_number()+", client:"+contract.getClient().getName());

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String jsonString = mapper.writeValueAsString(history);
        HttpController.httpPost("http://localhost:8080/HistoryService-1.0-SNAPSHOT/api" + "/history", jsonString);
    }

    private boolean isNullOrZero(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Integer && ((Integer) obj).intValue() == 0) {
            return true;
        }
        if (obj instanceof Double && ((Double) obj).doubleValue() == 0.0) {
            return true;
        }
        if (obj instanceof BigDecimal && ((BigDecimal) obj).compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        if (obj instanceof String && obj.equals("")){
            return true;
        }
        return false;
    }
}
