package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.contractservice.model.Client;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ClientController {

    public static List<Client> getClientListFromJson(String JsonClients){
        List<Client> clients = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JsonClients);
        for(int i=0; i <= jsonArray.length(); i++){
            Client client = new Client();
            client.setId((BigDecimal) jsonArray.getJSONObject(i).get("dbb_ID"));
            client.setName((String) jsonArray.getJSONObject(i).get("name"));
            clients.add(client);
        }
        return clients;
    }

    public static Dictionary<BigDecimal,Client> getClientDictionaryFromJson(String JsonClients){
        Dictionary<BigDecimal,Client> clients = new Hashtable<>();
        JSONArray jsonArray = new JSONArray(JsonClients);
        for(int i=0; i < jsonArray.length(); i++){
            Client client = new Client();
            client.setId(BigDecimal.valueOf((long)jsonArray.getJSONObject(i).get("dbb_ID")));
            client.setName((String) jsonArray.getJSONObject(i).get("name"));
            clients.put(client.getId(),client);
        }

        return clients;
    }
}
