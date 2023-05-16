package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.util.model.ModuleId;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ModuleidController {
    /**
     * turns a json object into a List of ModuleId objects.
     * @param JsonModuleIdr a json object containing ModuleId objects.
     * @return A List of ModuleId objects.
     */
    public static List<ModuleId> getModuleIdListFromJson(String JsonModuleIdr){
        List<ModuleId> moduleIds = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JsonModuleIdr);
        for(int i=0; i <= jsonArray.length(); i++){
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id((BigDecimal) jsonArray.getJSONObject(i).get("dbb_id"));
            moduleId.setName((String) jsonArray.getJSONObject(i).get("name"));
            moduleIds.add(moduleId);
        }
        return moduleIds;
    }

    /**
     * turns a json object into a Dictionary of ModuleIds with a BigDecimal ID as key.
     * @param JsonModuleId a json object containing the ModuleIds
     * @return Dictionary of ModuleIds with a BigDecimal ID as key
     */
    public static Dictionary<BigDecimal, ModuleId> getModuleIdDictionaryFromJson(String JsonModuleId){
        Dictionary<BigDecimal, ModuleId> ModuleIds = new Hashtable<>();
        JSONArray jsonArray = new JSONArray(JsonModuleId);
        for(int i=0; i < jsonArray.length(); i++){
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id(BigDecimal.valueOf((long) jsonArray.getJSONObject(i).get("dbb_id")));
            moduleId.setName((String) jsonArray.getJSONObject(i).get("name"));
            ModuleIds.put(moduleId.getDbb_id(),moduleId);
        }

        return ModuleIds;
    }
}
