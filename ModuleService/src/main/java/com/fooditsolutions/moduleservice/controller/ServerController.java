package com.fooditsolutions.moduleservice.controller;

import com.fooditsolutions.util.model.Server;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ServerController {
    public static List<Server> getServerListFromJson(String JsonClients){
        List<Server> clients = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JsonClients);
        for(int i=0; i <= jsonArray.length(); i++){
            Server server = new Server();
            server.setDBB_ID((BigDecimal) jsonArray.getJSONObject(i).get("dbb_ID"));
            server.setID((String) jsonArray.getJSONObject(i).get("ID"));
            server.setCLIENT_DBB_ID((BigDecimal) jsonArray.getJSONObject(i).get("CLIENT_DBB_ID"));
            clients.add(server);
        }
        return clients;
    }

    public static Dictionary<BigDecimal,Server> getServerDictionaryFromJson(String JsonClients){
        Dictionary<BigDecimal,Server> servers = new Hashtable<>();
        JSONArray jsonArray = new JSONArray(JsonClients);
        for(int i=0; i < jsonArray.length(); i++){
            Server server = new Server();
            server.setDBB_ID(BigDecimal.valueOf((long) jsonArray.getJSONObject(i).get("dbb_ID")));
            server.setID((String) jsonArray.getJSONObject(i).get("id"));
            server.setCLIENT_DBB_ID(BigDecimal.valueOf((long) jsonArray.getJSONObject(i).get("client_DBB_ID")));
            servers.put(server.getDBB_ID(),server);
        }

        return servers;
    }
}
