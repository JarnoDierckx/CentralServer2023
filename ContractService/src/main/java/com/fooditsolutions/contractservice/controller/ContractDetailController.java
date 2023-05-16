package com.fooditsolutions.contractservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.contractservice.ContractResource;
import com.fooditsolutions.util.model.Contract;
import com.fooditsolutions.util.model.ContractDetail;
import com.fooditsolutions.util.model.ModuleId;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.google.gson.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class ContractDetailController {
    /**
     * turns the received json object into a List of ContractDetail objects.
     * if the detail has a getModule_DBB_ID variable, it is given a ModuleId.
     * if it otherwise has a free line of text the boolean HasFreeLine is set to true.
     * if the end date has passed the detail is set to inactive.
     * @param jsonContractDetails a json object containing the ContractDetail objects.
     * @return a List of ContractDetail objects with the correct information.
     */
    public static List<ContractDetail> createContractDetailInformation(String jsonContractDetails) throws JsonProcessingException {
        List<ContractDetail> contractsDetails = new ArrayList<>();
        ContractDetail[] ContractDetails2;
        String jsonModuleid = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/moduleid?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        Dictionary<BigDecimal, ModuleId> Moduleids = ModuleidController.getModuleIdDictionaryFromJson(jsonModuleid);

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
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ContractDetails2= mapper.readValue(jsonContractDetails,ContractDetail[].class);
        for(int i=0; i < ContractDetails2.length; i++){

            ContractDetails2[i].set_active(true);

            if (ContractDetails2[i].getFreeLine()!=null){
                ContractDetails2[i].setHasFreeLine(true);
                contractsDetails.add(ContractDetails2[i]);
            }else{
                ContractDetails2[i].setHasFreeLine(false);
                ModuleId moduleId = Moduleids.get(ContractDetails2[i].getModule_DBB_ID());
                ContractDetails2[i].setModuleId(moduleId);
                contractsDetails.add(ContractDetails2[i]);
            }

            if (ContractDetails2[i].getEnd_date()!=null){
                Date currentDate=new Date();
                if (ContractDetails2[i].getEnd_date().before(currentDate)){
                    ContractDetails2[i].set_active(false);
                }
            }

        }
        return  contractsDetails;
    }

    /**
     *  Calculates the Jgr_not_indexed and Jgr_indexed values of the given object.
     * @param contractDetail the to be calculated object.
     * @return an object with both values calculated if the required variables aren't empty.
     */
    public static ContractDetail calculate(ContractDetail contractDetail) throws IOException {
        if (contractDetail.getPurchase_price() != null && contractDetail.getJgr() > 0){
            BigDecimal calculation1=(BigDecimal.valueOf(contractDetail.getJgr()).divide(BigDecimal.valueOf(100),2, RoundingMode.HALF_UP));
            BigDecimal calculation2=contractDetail.getPurchase_price().multiply(calculation1);
            calculation2=calculation2.setScale(2,RoundingMode.HALF_EVEN);
            if(contractDetail.getJgr_not_indexed()!=null && !contractDetail.getJgr_not_indexed().equals(calculation2)){
                contractDetail.setJgr_not_indexed(calculation2);
                contractDetail.setWhatToDo("U");
            }else if (contractDetail.getJgr_not_indexed()==null){
                contractDetail.setJgr_not_indexed(calculation2);
                contractDetail.setWhatToDo("U");
            }
            ContractResource contractResource=new ContractResource();
            Contract contract = contractResource.getContract(contractDetail.getContract_ID());
            if (contract.getIndex_last_invoice() != null && contractDetail.getIndex_Start() != null){
                BigDecimal calculation3=(contractDetail.getJgr_not_indexed().multiply(contract.index_last_invoice));
                BigDecimal calculation4=calculation3.divide(contractDetail.getIndex_Start(), RoundingMode.HALF_UP);
                calculation4=calculation4.setScale(2, RoundingMode.HALF_EVEN);
                if (contractDetail.getJgr_indexed() != null && !contractDetail.getJgr_indexed().equals(calculation4)){
                    contractDetail.setJgr_indexed(calculation4);
                    contractDetail.setWhatToDo("U");
                }else if (contractDetail.getJgr_indexed() == null){
                    contractDetail.setJgr_indexed(calculation4);
                    contractDetail.setWhatToDo("U");
                }
            }
        }


        return contractDetail;
    }
}
