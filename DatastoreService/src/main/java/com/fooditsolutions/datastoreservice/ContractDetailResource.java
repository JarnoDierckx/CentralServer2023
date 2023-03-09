package com.fooditsolutions.datastoreservice;

import com.fooditsolutions.datastoreservice.controller.DBThunderbird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.Contract;
import com.fooditsolutions.datastoreservice.model.centralserver.ContractDetail;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/contractDetail")
public class ContractDetailResource {

    /**
     * Recieves a contract ID and passes it to the database in a query for that contracts details.
     * All entries are put into objects, which are further put into a list that is then send back down the line.
     */
    @GET
    @Path("/{ContractID}")
    @Produces("application/json")
    public List<ContractDetail> getContracts(@PathParam("ContractID") String contractID, @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonContracts = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContracts = DBThunderbird.executeSQL(ds, "SELECT * FROM CONTRACT_DETAIL WHERE CONTRACT_ID = '"+contractID+"'");
            }
        }
        List<ContractDetail> contractDetails = new ArrayList<>();
        for (int i = 0; i < jsonContracts.length(); i++) {
            ContractDetail contractDetail = new ContractDetail();
            contractDetail.setID((int) jsonContracts.getJSONObject(i).opt("ID"));
            contractDetail.setContract_ID((int) jsonContracts.getJSONObject(i).opt("CONTRACT_ID"));
            contractDetail.setModule_DBB_ID((BigDecimal) jsonContracts.getJSONObject(i).opt("MODULE_DBB_ID"));
            contractDetail.setPurchase_Date((Date) jsonContracts.getJSONObject(i).opt("PURCHASE_DATE"));
            contractDetail.setAmount((int) jsonContracts.getJSONObject(i).opt("AMOUNT"));
            contractDetail.setPurchase_price((BigDecimal) jsonContracts.getJSONObject(i).opt("PURCHASE_PRICE"));
            contractDetail.setIndex_Start((BigDecimal) jsonContracts.getJSONObject(i).opt("INDEX_START"));
            contractDetail.setRenewal((String) jsonContracts.getJSONObject(i).opt("RENEWAL"));

            contractDetails.add(contractDetail);
        }

        return contractDetails;
    }
}
