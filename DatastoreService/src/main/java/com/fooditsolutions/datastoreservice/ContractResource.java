package com.fooditsolutions.datastoreservice;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.controller.Util;
import com.fooditsolutions.datastoreservice.model.centralserver.Contract;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import org.json.JSONArray;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/contract")
public class ContractResource {
    @PostConstruct
    public void init(){
        System.out.println("DataStoreService");
    }

    @GET
    @Produces("application/json")
    public List<Contract> getContracts(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonContracts = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContracts = DBFirebird.executeSQL(ds, "SELECT * FROM CONTRACT");
            }
        }
        List<Contract> contracts = new ArrayList<>();
        for (int i = 0; i < jsonContracts.length(); i++) {
            Contract contract = new Contract();

            contract.setID((int) jsonContracts.getJSONObject(i).opt("ID"));
            contract.setContract_number((String) jsonContracts.getJSONObject(i).opt("CONTRACT_NUMBER"));
            contract.setCLIENT_ID((BigDecimal) jsonContracts.getJSONObject(i).opt("CLIENT_ID"));
            contract.setBJR_ID((int) jsonContracts.getJSONObject(i).opt("BJR_ID"));
            contract.setStart_date((Date) jsonContracts.getJSONObject(i).opt("START_DATE"));
            Util.structureSQL(contract.getStart_date());
            contract.setEnd_date((Date) jsonContracts.getJSONObject(i).opt("END_DATE"));
            contract.setSource((String) jsonContracts.getJSONObject(i).opt("SOURCE"));
            contract.setInvoice_frequency((String) jsonContracts.getJSONObject(i).opt("INVOICE_FREQUENCY"));
            contract.setIndex_frequency((String) jsonContracts.getJSONObject(i).opt("INDEX_FREQUENCY"));
            if(jsonContracts.getJSONObject(i).opt("BASE_INDEX_YEAR")!=null) {
                contract.setBase_index_year((int) jsonContracts.getJSONObject(i).opt("BASE_INDEX_YEAR"));
            }
            if(jsonContracts.getJSONObject(i).opt("INDEX_START")!=null) {
                contract.setIndex_start((BigDecimal) jsonContracts.getJSONObject(i).opt("INDEX_START"));
            }
            if(jsonContracts.getJSONObject(i).opt("INDEX_LAST_INVOICE")!=null) {
                contract.setIndex_last_invoice((BigDecimal) jsonContracts.getJSONObject(i).opt("INDEX_LAST_INVOICE"));
            }
            if(jsonContracts.getJSONObject(i).opt("AMOUNT_LAST_INVOICE")!=null) {
                contract.setAmount_last_invoice((BigDecimal) jsonContracts.getJSONObject(i).opt("AMOUNT_LAST_INVOICE"));
            }
            if(jsonContracts.getJSONObject(i).opt("LAST_INVOICE_NUMBER")!=null) {
                contract.setLast_invoice_number((int) jsonContracts.getJSONObject(i).opt("LAST_INVOICE_NUMBER"));
            }
            if(jsonContracts.getJSONObject(i).opt("LAST_INVOICE_DATE")!=null) {
                contract.setLast_invoice_date((Date) jsonContracts.getJSONObject(i).opt("LAST_INVOICE_DATE"));
            }
            if(jsonContracts.getJSONObject(i).opt("LAST_INVOICE_PERIOD_START")!=null) {
                contract.setLast_invoice_period_start((Date) jsonContracts.getJSONObject(i).opt("LAST_INVOICE_PERIOD_START"));
            }
            if(jsonContracts.getJSONObject(i).opt("LAST_INVOICE_PERIOD_END")!=null) {
                contract.setLast_invoice_period_end((Date) jsonContracts.getJSONObject(i).opt("LAST_INVOICE_PERIOD_END"));
            }
            if(jsonContracts.getJSONObject(i).opt("JGR")!=null) {
                contract.setJgr((int) jsonContracts.getJSONObject(i).opt("JGR"));
            }
            contract.setComments((String) jsonContracts.getJSONObject(i).opt("COMMENTS"));
            if(jsonContracts.getJSONObject(i).opt("CREATED")!=null) {
                contract.setCreated((Date) jsonContracts.getJSONObject(i).opt("CREATED"));
            }
            if(jsonContracts.getJSONObject(i).opt("UPDATED")!=null) {
                contract.setUpdated((Date) jsonContracts.getJSONObject(i).opt("UPDATED"));
            }

            contracts.add(contract);
        }

        return contracts;
    }
}
