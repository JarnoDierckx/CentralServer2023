package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.contractservice.model.Client;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.google.gson.*;
import org.json.JSONArray;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class ContractController {

    public static List<Contract> createContractInformation(String jsonContracts){
        List<Contract> contracts = new ArrayList<>();
        Contract[] contracts2;
        String jsonClient = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/client?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Client> clients = ClientController.getClientDictionaryFromJson(jsonClient);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();
        //Contract[] contracts1=gson.fromJson(responseString,Contract[].class);
        contracts2=gson.fromJson(jsonContracts,Contract[].class);
        JSONArray jsonArray = new JSONArray(jsonContracts);
        for(int i=0; i < contracts2.length; i++){


            contracts2[i].setClient(clients.get(contracts2[i].getClient_id()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contracts.add(contracts2[i]);
        }
        return contracts;
    }
}
