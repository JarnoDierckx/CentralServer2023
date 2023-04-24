package com.fooditsolutions.moduleservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.model.*;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class ModuleController {
    public static List<Module> createModuleInformation(String jsonContracts) throws IOException {
        List<Module> contracts = new ArrayList<>();
        Module[] contracts2;
        String jsonServer = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/server?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, Server> servers = ServerController.getServerDictionaryFromJson(jsonServer);
        String jsonModuleId = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/moduleid?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<String, ModuleId> moduleids = ModuleidController.getModuleIdDictionaryFromJson(jsonModuleId);

        byte[] jsonData = jsonContracts.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Module[].class);

        //JSONArray jsonArray = new JSONArray(jsonContracts);
        for(int i=0; i < contracts2.length; i++){
            contracts2[i].setServer(servers.get(contracts2[i].getSERVER_DBB_ID()));
            contracts2[i].setModuleid(moduleids.get(contracts2[i].getName()));
            //contract.setName((String) jsonArray.getJSONObject(i).get("name"));
            contracts.add(contracts2[i]);
        }
        return contracts;
    }
}
