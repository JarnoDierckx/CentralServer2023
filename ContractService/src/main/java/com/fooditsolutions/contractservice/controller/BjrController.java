package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.util.model.Bjr;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class BjrController {
    public static List<Bjr> getBjrListFromJson(String JsonBjr){
        List<Bjr> bjrs = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JsonBjr);
        for(int i=0; i <= jsonArray.length(); i++){
            Bjr bjr = new Bjr();
            bjr.setId((int) jsonArray.getJSONObject(i).get("id"));
            bjr.setName((String) jsonArray.getJSONObject(i).get("name"));
            bjrs.add(bjr);
        }
        return bjrs;
    }

    public static Dictionary<Integer,Bjr> getBjrDictionaryFromJson(String JsonBjr){
        Dictionary<Integer,Bjr> clients = new Hashtable<>();
        JSONArray jsonArray = new JSONArray(JsonBjr);
        for(int i=0; i < jsonArray.length(); i++){
            Bjr bjr = new Bjr();
            bjr.setId((int) jsonArray.getJSONObject(i).get("id"));
            bjr.setName((String) jsonArray.getJSONObject(i).get("name"));
            clients.put(bjr.getId(),bjr);
        }

        return clients;
    }
}
