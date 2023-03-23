package com.fooditsolutions.contractservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.model.Bjr;
import com.fooditsolutions.contractservice.model.Client;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.google.gson.*;
import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class ContractController {

    public static List<Contract> createContractInformation(String jsonContracts) throws IOException {
        List<Contract> contracts = new ArrayList<>();
        Contract[] contracts2;
        String jsonClient = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Client> clients = ClientController.getClientDictionaryFromJson(jsonClient);
        String jsonBjr = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/bjr?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<Integer, Bjr> bjrs = BjrController.getBjrDictionaryFromJson(jsonBjr);

        /* had to add the GsonBuilder() as there was an issue with the epoch date conversion
         * https://itecnote.com/tecnote/java-convert-string-date-to-object-yields-invalid-time-zone-indicator-0/
         */
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();
        //Gson gson = new Gson();
        //Contract[] contracts1=gson.fromJson(responseString,Contract[].class);
        contracts2=gson.fromJson(jsonContracts,Contract[].class);

        byte[] jsonData = jsonContracts.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract[].class);

        //JSONArray jsonArray = new JSONArray(jsonContracts);
        for(int i=0; i < contracts2.length; i++){
            contracts2[i].setClient(clients.get(contracts2[i].getClient_id()));
            contracts2[i].setBjr(bjrs.get(contracts2[i].getBjr_id()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contracts.add(contracts2[i]);
        }
        return contracts;
    }
}
