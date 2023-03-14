package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.contractservice.model.Client;
import com.fooditsolutions.contractservice.model.Contract;
import com.fooditsolutions.contractservice.model.ContractDetail;
import com.fooditsolutions.contractservice.model.ModuleId;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class ContractDetailController {
    public static List<ContractDetail> createContractDetailInformation(String jsonContractDetails) {
        List<ContractDetail> contractsDetails = new ArrayList<>();
        ContractDetail[] ContractDetails2;
        PropertiesController.getProperty().setDatastore("C287746F288DF2CB7292DD2EE29CFECD");
        String jsonModuleid = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/moduleid?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, ModuleId> Moduleids = ModuleidController.getModuleIdDictionaryFromJson(jsonModuleid);

        /* had to add the GsonBuilder() as there was an issue wiht the epoch date conversion
         * https://itecnote.com/tecnote/java-convert-string-date-to-object-yields-invalid-time-zone-indicator-0/
         */
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
                    }
                })
                .create();
        ContractDetails2=gson.fromJson(jsonContractDetails,ContractDetail[].class);
        for(int i=0; i < ContractDetails2.length; i++){

            ModuleId moduleId = Moduleids.get(ContractDetails2[i].getModule_DBB_ID());
            ContractDetails2[i].setModuleId(moduleId);
            contractsDetails.add(ContractDetails2[i]);

        }
        return  contractsDetails;
    }
}
