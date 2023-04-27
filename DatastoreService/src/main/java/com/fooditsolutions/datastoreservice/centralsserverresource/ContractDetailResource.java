package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.ContractDetail;
import org.json.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    @GET
    @Path("/singleDetail/{ContractDetailID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<ContractDetail> getContractDetail(@PathParam("ContractDetailID") String contractDetailID, @QueryParam("datastoreKey") String datastoreKey){
        JSONArray jsonContractDetail=new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContractDetail = DBFirebird.executeSQL(ds, "SELECT * FROM CONTRACT_DETAIL WHERE ID = '"+contractDetailID+"'");
            }
        }
        return JsonToContractDetail(jsonContractDetail);
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


        String[] sql = new String[contractDetail.length];
        for (int i = 0; i < contractDetail.length; i++) {
            if (contractDetail[i] != null) {
                sql[i] = contractDetail[i].getUpdateStatement();
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
     *
     * @param datastoreKey    is to specify and access the required database.
     * @param contractDetails is looped over. Each object gets its own query string that gets looped over to execute each one.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public int[] createContractDetails(@QueryParam("datastoreKey") String datastoreKey, ContractDetail[] contractDetails){
        String[] sql=new String[contractDetails.length];
        for (int i=0;i< contractDetails.length;i++){
            sql[i]=contractDetails[i].getInsertStatement();
        }
        List<String> sqlRead=new ArrayList<>();
        for (ContractDetail contractDetail : contractDetails) {
            String contractID = "SELECT ID FROM CONTRACT_DETAIL WHERE CONTRACT_ID = '" + contractDetail.getContract_ID();
            String moduleDBBID;
            String purchaseDate;
            String purchasePrice;
            if (contractDetail.getModule_DBB_ID() != null){
                moduleDBBID="' AND MODULE_DBB_ID = " + contractDetail.getModule_DBB_ID();
            }else {
                moduleDBBID="' AND MODULE_DBB_ID is null";
            }
            if (contractDetail.getPurchase_Date() != null){
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd.MM.yyyy");
                purchaseDate=" AND PURCHASE_DATE = '" + sdf.format(contractDetail.getPurchase_Date());
            }else {
                purchaseDate=" AND PURCHASE_DATE is null";
            }
            if (contractDetail.getPurchase_price() !=null && contractDetail.getPurchase_Date() != null){//dates need to be surrounded by ''
                purchasePrice= "' AND PURCHASE_PRICE = " + contractDetail.getPurchase_price();
            }else if(contractDetail.getPurchase_price() !=null && contractDetail.getPurchase_Date() == null){
                purchasePrice= " AND PURCHASE_PRICE = " + contractDetail.getPurchase_price();
            } else if (contractDetail.getPurchase_price() ==null && contractDetail.getPurchase_Date() != null) {
                purchasePrice= "' AND PURCHASE_PRICE IS NULL";
            } else {
                purchasePrice= " AND PURCHASE_PRICE IS NULL";
            }

            sqlRead.add(contractID + moduleDBBID + purchaseDate + purchasePrice);
        }
        int[] ID=new int[sqlRead.size()];
        JSONArray[] JSONID = new JSONArray[sqlRead.size()];

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                for (String s : sql) {
                    if (s != null) {
                        DBFirebird.executeSQLInsert(ds, s);
                    }
                }
                for (int i=0;i<sqlRead.size();i++){
                    JSONID[i]=DBFirebird.executeSQL(ds, sqlRead.get(i));
                }
                System.out.println("Insert successful");
            }
        }
        for (int i=0;i<JSONID.length;i++){
            ID[i] = (int) JSONID[i].getJSONObject(0).opt("ID");
        }
        return ID;
    }

    @DELETE
    @Path("/{id}")
    public void deleteContractDetails(@QueryParam("datastoreKey") String datastoreKey, @PathParam("id") int id){
        String sql = "DELETE FROM CONTRACT_DETAIL WHERE ID ="+id;
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                //executeSQLInsert does the job just fine
                DBFirebird.executeSQLInsert(ds, sql);
                System.out.println("Delete successfull");
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
            if (jsonContracts.getJSONObject(i).opt("JGR") != null) {
                contractDetail.setJgr((int) jsonContracts.getJSONObject(i).opt("JGR"));
            }
            contractDetail.setJgr_not_indexed((BigDecimal) jsonContracts.getJSONObject(i).opt("JGR_NOT_INDEXED"));
            contractDetail.setJgr_indexed((BigDecimal) jsonContracts.getJSONObject(i).opt("JGR_INDEXED"));
            contractDetail.setSource((String) jsonContracts.getJSONObject(i).opt("SOURCE"));

            contractDetails.add(contractDetail);
        }

        return contractDetails;
    }
}
