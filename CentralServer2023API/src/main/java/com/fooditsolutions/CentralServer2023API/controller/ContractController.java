package com.fooditsolutions.CentralServer2023API.controller;

import com.fooditsolutions.CentralServer2023API.enums.ModuleCompare;
import com.fooditsolutions.CentralServer2023API.model.CompareContractCS;
import com.fooditsolutions.CentralServer2023API.model.ContractDetail;
import com.fooditsolutions.CentralServer2023API.model.Module;

import java.math.BigDecimal;
import java.util.*;

public class ContractController {
    public static List<CompareContractCS> checkContractCs(List<ContractDetail> contractDetails, List<Module> modules){
        HashMap<BigDecimal,ContractDetail> cContractDetail = new HashMap();
        HashMap<BigDecimal,Module> cModules = new HashMap();
        List<CompareContractCS> compareContractCSs = new ArrayList<>();

        for(ContractDetail cd : contractDetails){
            cContractDetail.put(cd.getModule_DBB_ID(),cd);
        }
        //The modules details are stored in ModuleId. The link between the module and a server is in the table Module
        for(Module mod : modules){
            if (mod.getModuleid()!=null){
                BigDecimal Moduleid_id = mod.getModuleid().getDbb_id();
                CompareContractCS compareContractCS = new CompareContractCS();
                compareContractCS.setModuleId_DBB_ID(Moduleid_id);
                compareContractCS.setModuleId(mod.getModuleid());
                cModules.put(Moduleid_id,mod);
                if(cContractDetail.containsKey(Moduleid_id)){
                    compareContractCS.setModuleSyncStatus(ModuleCompare.SYNC);
                    cContractDetail.remove(Moduleid_id);
                }else{
                    compareContractCS.setModuleSyncStatus(ModuleCompare.CENTRALSERVER);
                }
                compareContractCSs.add(compareContractCS);
            }

        }

        for(Map.Entry<BigDecimal,ContractDetail> set : cContractDetail.entrySet()){
            CompareContractCS compareContractCS = new CompareContractCS();
            compareContractCS.setModuleId_DBB_ID(set.getValue().getModule_DBB_ID());
            compareContractCS.setModuleSyncStatus(ModuleCompare.CONTRACTS);
            compareContractCS.setModuleId(set.getValue().getModuleId());
            compareContractCSs.add(compareContractCS);
        }

        return compareContractCSs;

    }
}
