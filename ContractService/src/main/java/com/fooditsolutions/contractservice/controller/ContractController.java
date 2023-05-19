package com.fooditsolutions.contractservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.model.Client;
import com.fooditsolutions.util.model.Contract;
import com.fooditsolutions.util.model.Server;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class ContractController {

    /**
     * Gives all received contracts the server and client variables they can't directly get through the database.
     * any contracts that are past their end date are set to inactive
     * @param jsonContracts A json string containing a list of Contract objects.
     * @return A list of contracts with the needed information.
     */
    public static List<Contract> createContractsInformation(String jsonContracts) throws IOException {
        List<Contract> contracts = new ArrayList<>();
        Contract[] contracts2;
        String jsonClient = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Client> clients = ClientController.getClientDictionaryFromJson(jsonClient);
        String jsonServers =HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/server/all?datastoreKey="+ PropertiesController.getProperty().getDatastore());


        byte[] jsonData = jsonContracts.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract[].class);
        Server[] servers=mapper.readValue(jsonServers,Server[].class);

        //JSONArray jsonArray = new JSONArray(jsonContracts);
        for (Contract contract : contracts2) {
            contract.is_active = true;
            if (contract.getEnd_date()!=null){
                Date currentDate=new Date();
                if (contract.getEnd_date().before(currentDate)){
                    contract.set_active(false);
                }
            }

            contract.setClient(clients.get(contract.getClient_id()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contracts.add(contract);
        }
        return contracts;
    }

    /**
     * Gives the received contract the server and client variables it can't directly get through the database.
     * @param jsonContract a json string containing a single Contract object
     * @return a single Contract object with a client and bjr variable
     */
    public static Contract createContractInformation(String jsonContract) throws IOException {
        Contract contract = new Contract();
        Contract contracts2;
        String jsonClient = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Client> clients = ClientController.getClientDictionaryFromJson(jsonClient);

        byte[] jsonData = jsonContract.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract.class);

        //JSONArray jsonArray = new JSONArray(jsonContracts);

            contracts2.setClient(clients.get(contracts2.getClient_id()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contract=contracts2;

        return contract;
    }

}
