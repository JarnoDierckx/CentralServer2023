package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.ContractDetail;
import org.json.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Path("/contractDetail")
public class ContractDetailResource {

    /**
     * Receives a contract ID and passes it to the database in a query for that contracts details.
     * All entries are put into objects, which are further put into a list that is then send back down the line.
     */
    @GET
    @Path("/{ContractID}")
    @Produces("application/json")
    public List<ContractDetail> getContracts(@PathParam("ContractID") String contractID, @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonContracts = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContracts = DBFirebird.executeSQL(ds, "SELECT * FROM CONTRACT_DETAIL WHERE CONTRACT_ID = '"+contractID+"'");
            }
        }
        return JsonToContractDetail(jsonContracts);
    }

    /**
     * Endpoint called to update the details of a contract. The differences between the received objects and those
     * currently in the database are taken out so only those variables are send to the database.
     * @param datastoreKey is to specify and access the required database.
     * @param contractDetail is looped over. Each object gets its own query string that gets looped over to execute each one.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateContractDetails(@QueryParam("datastoreKey") String datastoreKey, ContractDetail[] contractDetail) throws IllegalAccessException {
        JSONArray jsonContracts = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContracts = DBFirebird.executeSQL(ds, "SELECT * FROM CONTRACT_DETAIL WHERE CONTRACT_ID = '"+contractDetail[0].getContract_ID()+"'");
            }
        }
        List<ContractDetail> originalContractDetails = JsonToContractDetail(jsonContracts);
        ContractDetail[] detailDifferences = new ContractDetail[contractDetail.length];


        for (int i = 0; i < contractDetail.length; i++) {
            for (ContractDetail originalContractDetail : originalContractDetails) {
                if (contractDetail[i].getID() == originalContractDetail.getID()) {
                    boolean newDetail = false;
                    if (detailDifferences[i] == null) {
                        detailDifferences[i] = new ContractDetail();
                        detailDifferences[i].setID(contractDetail[i].getID());
                        newDetail = true;
                    }
                    Field[] fields = ContractDetail.class.getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        Object value1 = field.get(originalContractDetail);
                        Object value2 = field.get(contractDetail[i]);
                        if (value1 != null && value2 != null) {
                            if (!value1.equals(value2)) {
                                if (newDetail) {
                                    field.set(detailDifferences[i], value2);
                                } else {
                                    field.set(detailDifferences[i], value2);
                                }
                            }
                        }
                    }
                }
            }
        }

        String[] sql = new String[detailDifferences.length];
        for (int i = 0; i < detailDifferences.length; i++) {
            if (detailDifferences[i] != null) {
                sql[i] = detailDifferences[i].getUpdateStatement();
            }
        }

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                for (String s : sql) {
                    if (s != null) {
                        DBFirebird.executeSQLUpdate(ds, s);
                    }
                }
                System.out.println("update successful");
            }
        }
    }

    /**
     * Receives an array of ContractDetail objects that need to be inserted into the database.
     * @param datastoreKey is to specify and access the required database.
     * @param contractDetails is looped over. Each object gets its own query string that gets looped over to execute each one.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createContractDetails(@QueryParam("datastoreKey") String datastoreKey, ContractDetail[] contractDetails){
        String[] sql=new String[contractDetails.length];
        for (int i=0;i< contractDetails.length;i++){
            sql[i]=contractDetails[i].getInsertStatement();
        }

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                for (String s : sql) {
                    if (s != null) {
                        DBFirebird.executeSQLInsert(ds, s);
                    }
                }
                System.out.println("Insert successful");
            }
        }
    }

    /**
     * Takes a jsonArray object, loops over every object in it and maps it to the corresponding fields in the ContractDetail class
     * @return A List<ContractDetail> item is returned.
     */
    public List<ContractDetail> JsonToContractDetail(JSONArray jsonContracts){
        List<ContractDetail> contractDetails = new ArrayList<>();
        for (int i = 0; i < jsonContracts.length(); i++) {
            ContractDetail contractDetail = new ContractDetail();
            contractDetail.setID((int) jsonContracts.getJSONObject(i).opt("ID"));
            contractDetail.setContract_ID((int) jsonContracts.getJSONObject(i).opt("CONTRACT_ID"));
            contractDetail.setModule_DBB_ID((BigDecimal) jsonContracts.getJSONObject(i).opt("MODULE_DBB_ID"));
            contractDetail.setPurchase_Date((Date) jsonContracts.getJSONObject(i).opt("PURCHASE_DATE"));
            if (jsonContracts.getJSONObject(i).opt("AMOUNT") != null) {
                contractDetail.setAmount((int) jsonContracts.getJSONObject(i).opt("AMOUNT"));
            }

            contractDetail.setPurchase_price((BigDecimal) jsonContracts.getJSONObject(i).opt("PURCHASE_PRICE"));
            contractDetail.setIndex_Start((BigDecimal) jsonContracts.getJSONObject(i).opt("INDEX_START"));
            contractDetail.setRenewal((String) jsonContracts.getJSONObject(i).opt("RENEWAL"));

            contractDetails.add(contractDetail);
        }

        return contractDetails;
    }
}
