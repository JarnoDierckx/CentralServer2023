package com.fooditsolutions.CentralServer2023API.controller;

import com.fooditsolutions.CentralServer2023API.enums.ModuleCompare;
import com.fooditsolutions.CentralServer2023API.model.CompareContractCS;
import com.fooditsolutions.util.model.*;

import java.math.BigDecimal;
import java.util.*;

public class ContractController {
    /**
     * Compares a list of contractDetail and module objects connected to a certain Contract.
     * All modules that aren't already a detail object are added to the list, so they can be displayed as empty objects on the front end.
     * @param contractDetails List of a contracts associated contractDetail objects.
     * @param modules List of modules bound to a contracts server
     * @return a List of CompareContractCS objects.
     */
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

    /**
     * Used to check if a contract has modules connected to it that don't yet have a contractDetail object.
     */
    public static boolean checkForEmptyModule(List<ContractDetail> contractDetails, List<Module> modules){
        HashMap<BigDecimal,ContractDetail> cContractDetail = new HashMap();

        for(ContractDetail cd : contractDetails){
            cContractDetail.put(cd.getModule_DBB_ID(),cd);
        }
        //The modules details are stored in ModuleId. The link between the module and a server is in the table Module
        for(Module mod : modules){
            if (mod.getModuleid()!=null){
                BigDecimal Moduleid_id = mod.getModuleid().getDbb_id();
                if(!cContractDetail.containsKey(Moduleid_id)){
                    return true;
                }
            }

        }
        return false;
    }
}
