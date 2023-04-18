package com.fooditsolutions.contractservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.model.Bjr;
import com.fooditsolutions.contractservice.model.Client;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.contractservice.model.Server;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.google.gson.*;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

public class ContractController {

    /**
     * Gives all received contracts the bjr and client variables they can't directly get through the database.
     * It also checks if there is still a server object stored with the same server_DBB_ID, if not they are set to inactive.
     * @param jsonContracts A json string containing a list of Contract objects.
     * @return A list of contracts with the needed information.
     */
    public static List<Contract> createContractsInformation(String jsonContracts) throws IOException {
        List<Contract> contracts = new ArrayList<>();
        Contract[] contracts2;
        String jsonClient = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Client> clients = ClientController.getClientDictionaryFromJson(jsonClient);
        String jsonBjr = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/bjr?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<Integer, Bjr> bjrs = BjrController.getBjrDictionaryFromJson(jsonBjr);
        String jsonServers =HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/server/all?datastoreKey="+ PropertiesController.getProperty().getDatastore());


        byte[] jsonData = jsonContracts.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract[].class);
        Server[] servers=mapper.readValue(jsonServers,Server[].class);

        //JSONArray jsonArray = new JSONArray(jsonContracts);
        for(int i=0; i < contracts2.length; i++){
            contracts2[i].is_active=false;
            if (contracts2[i].server_DBB_ID != null){
                for (Server server:servers){
                    if (Objects.equals(contracts2[i].server_DBB_ID, server.getDBB_ID())){
                        contracts2[i].is_active=true;
                    }
                }
            }

            contracts2[i].setClient(clients.get(contracts2[i].getClient_id()));
            contracts2[i].setBjr(bjrs.get(contracts2[i].getBjr_id()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contracts.add(contracts2[i]);
        }
        return contracts;
    }

    /**
     * Gives the received contract the bjr and client variables it can't directly get through the database.
     * It also checks if there is still a server object stored with the same server_DBB_ID, if not it is set to inactive.
     * @param jsonContract a json string containing a single Contract object
     * @return a single Contract object with a client and bjr variable
     */
    public static Contract createContractInformation(String jsonContract) throws IOException {
        Contract contract = new Contract();
        Contract contracts2;
        String jsonClient = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Client> clients = ClientController.getClientDictionaryFromJson(jsonClient);
        String jsonBjr = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/bjr?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<Integer, Bjr> bjrs = BjrController.getBjrDictionaryFromJson(jsonBjr);

        byte[] jsonData = jsonContract.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract.class);

        //JSONArray jsonArray = new JSONArray(jsonContracts);

            contracts2.setClient(clients.get(contracts2.getClient_id()));
            contracts2.setBjr(bjrs.get(contracts2.getBjr_id()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contract=contracts2;

        return contract;
    }

}
