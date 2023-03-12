package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.contractservice.model.Bjr;
import com.fooditsolutions.contractservice.model.ModuleId;
import org.json.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class ModuleidController {
    public static List<ModuleId> getModeuleIdListFromJson(String JsonModeuleIdr){
        List<ModuleId> moduleIds = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(JsonModeuleIdr);
        for(int i=0; i <= jsonArray.length(); i++){
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id((BigDecimal) jsonArray.getJSONObject(i).get("dbb_id"));
            moduleId.setName((String) jsonArray.getJSONObject(i).get("name"));
            moduleIds.add(moduleId);
        }
        return moduleIds;
    }

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
