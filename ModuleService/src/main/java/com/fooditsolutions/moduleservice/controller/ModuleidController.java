package com.fooditsolutions.moduleservice.controller;

import com.fooditsolutions.util.model.ModuleId;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ModuleidController {
    /**
     * receives a list of ModuleIds in json format and turns them into a List of ModuleIds.
     * @param JsonModuleId A list of moduleIds in json format, given as a string
     * @return a List of ModuleIds
     */
    public static List<ModuleId> getModuleIdListFromJson(String JsonModuleId){
        List<ModuleId> moduleIds = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JsonModuleId);
        for(int i=0; i <= jsonArray.length(); i++){
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id((BigDecimal) jsonArray.getJSONObject(i).get("dbb_id"));
            moduleId.setName((String) jsonArray.getJSONObject(i).get("name"));
            moduleIds.add(moduleId);
        }
        return moduleIds;
    }

    /**
     * receives a list of ModuleIds in json format and turns them into a dictionary of Strings with the modules names and proper ModuleId objects.
     * @param JsonModuleId A list of moduleIds in json format, given as a string
     * @return the Dictionary list Dictionary<String, ModuleId>
     */
    public static Dictionary<String, ModuleId> getModuleIdDictionaryFromJson(String JsonModuleId){
        Dictionary<String, ModuleId> ModuleIds = new Hashtable<>();
        JSONArray jsonArray = new JSONArray(JsonModuleId);
        for(int i=0; i < jsonArray.length(); i++){
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id(BigDecimal.valueOf((long) jsonArray.getJSONObject(i).get("dbb_id")));
            moduleId.setName((String) jsonArray.getJSONObject(i).get("name"));
            ModuleIds.put(moduleId.getName(),moduleId);
        }

        return ModuleIds;
    }
}
