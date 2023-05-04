package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.controller.Util;
import com.fooditsolutions.datastoreservice.model.centralserver.History;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.datastoreservice.model.centralserver.Client;
import com.fooditsolutions.datastoreservice.model.centralserver.Contract;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.ContractDetail;
import org.json.JSONArray;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Path("/contract")
public class ContractResource {
    @PostConstruct
    public void init() {
        System.out.println("DataStoreService contracts");
    }


    /**
     * Endpoint called to retrieve all contracts stored in the database.
     * After the database is queried, A list of Contracts is made and each object it the retrieved json string has its properties put into the corresponding properties in the Contract object.
     *
     * @param datastoreKey is to specify what database needs to be used for this transaction.
     * @return sends back the list of Contracts.
     */
    @GET
    @Produces("application/json")
    public List<Contract> getContracts(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonContracts = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContracts = DBFirebird.executeSQL(ds, "SELECT * FROM CONTRACT");
            }
        }
        return JsonToContract(jsonContracts);
    }

    @GET
    @Produces("application/json")
    @Path("/{ContractId}")
    public Contract getContract(@PathParam("ContractId") int contractId,
                                @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonContracts = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonContracts = DBFirebird.executeSQL(ds, "SELECT * FROM CONTRACT WHERE ID=" + contractId);
            }
        }
        return JsonToContract(jsonContracts).get(0);
    }

    /**
     * Endpoint to update a single contract.
     * The contract object send as a parameter is used to build the query string that will be used.
     *
     * @param datastoreKey is to specify which database to query
     * @param contract     is the contract that will update the original depending on the id.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateContract(@QueryParam("datastoreKey") String datastoreKey, Contract contract) throws IllegalAccessException, InstantiationException {
        String sql = contract.getUpdateStatement();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                DBFirebird.executeSQLUpdate(ds, sql);
                System.out.println("update successfull");
            }
        }
    }

    /**
     * Endpoint to create a single contract.
     * The contract object send as a parameter is used to build the query string that will be used.
     *
     * @param datastoreKey is to specify which database to query
     * @param contract     is the contract that will be inserted into the database.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public int createContract(@QueryParam("datastoreKey") String datastoreKey, Contract contract) throws JsonProcessingException {
        String sql = contract.getInsertStatement();
        System.out.println("sql statement " + sql);
        String sqlRead = "SELECT ID FROM CONTRACT WHERE CONTRACT_NUMBER = '" + contract.getContract_number() + "' AND CLIENT_ID = " + contract.getClient_id() + " AND SOURCE = '" + contract.getSource() + "'";
        int ID;
        JSONArray JSONID = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                DBFirebird.executeSQLInsert(ds, sql);
                JSONID = DBFirebird.executeSQL(ds, sqlRead);
                System.out.println("Insert successfull");
            }
        }

        ID = (int) JSONID.getJSONObject(0).opt("ID");

        return ID;
    }

    /**
     * Creates a sql statement based on the given contract id and executes it to delete the contract.
     * unfinished
     * @param datastoreKey is to specify which database to query.
     * @param contractID is to specify which contract needs to be deleted.
     */
    /*@DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{ContractId}")
    public void deleteContract(@QueryParam("datastoreKey")String datastoreKey, @PathParam("ContractId") int contractID){
        String sql = "DELETE FROM CONTRACT WHERE ID ="+contractID;

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                //executeSQLInsert does the job just fine
                DBFirebird.executeSQLInsert(ds, sql);
                System.out.println("Delete successfull");
            }
        }
    }*/

    /**
     * Takes a array of contracts in json format and one by one enters the contracts values into proper contract objects.
     *
     * @param jsonContracts the json array containg all the contracts
     * @return A list of Contract objects
     */
    public List<Contract> JsonToContract(JSONArray jsonContracts) {
        List<Contract> contracts = new ArrayList<>();
        for (int i = 0; i < jsonContracts.length(); i++) {
            Contract contract = new Contract();

            contract.setId((int) jsonContracts.getJSONObject(i).opt("ID"));
            contract.setContract_number((String) jsonContracts.getJSONObject(i).opt("CONTRACT_NUMBER"));
            contract.setClient_id((BigDecimal) jsonContracts.getJSONObject(i).opt("CLIENT_ID"));
            contract.setStart_date((Date) jsonContracts.getJSONObject(i).opt("START_DATE"));
            Util.structureSQL(contract.getStart_date());
            contract.setEnd_date((Date) jsonContracts.getJSONObject(i).opt("END_DATE"));
            contract.setSource((String) jsonContracts.getJSONObject(i).opt("SOURCE"));
            contract.setInvoice_frequency((String) jsonContracts.getJSONObject(i).opt("INVOICE_FREQUENCY"));
            contract.setIndex_frequency((String) jsonContracts.getJSONObject(i).opt("INDEX_FREQUENCY"));
            if (jsonContracts.getJSONObject(i).opt("BASE_INDEX_YEAR") != null) {
                contract.setBase_index_year((int) jsonContracts.getJSONObject(i).opt("BASE_INDEX_YEAR"));
            }
            if (jsonContracts.getJSONObject(i).opt("INDEX_START") != null) {
                contract.setIndex_start((BigDecimal) jsonContracts.getJSONObject(i).opt("INDEX_START"));
            }
            if (jsonContracts.getJSONObject(i).opt("INDEX_LAST_INVOICE") != null) {
                contract.setIndex_last_invoice((BigDecimal) jsonContracts.getJSONObject(i).opt("INDEX_LAST_INVOICE"));
            }
            if (jsonContracts.getJSONObject(i).opt("AMOUNT_LAST_INVOICE") != null) {
                contract.setAmount_last_invoice((BigDecimal) jsonContracts.getJSONObject(i).opt("AMOUNT_LAST_INVOICE"));
            }
            if (jsonContracts.getJSONObject(i).opt("LAST_INVOICE_NUMBER") != null) {
                contract.setLast_invoice_number((int) jsonContracts.getJSONObject(i).opt("LAST_INVOICE_NUMBER"));
            }
            if (jsonContracts.getJSONObject(i).opt("LAST_INVOICE_DATE") != null) {
                contract.setLast_invoice_date((Date) jsonContracts.getJSONObject(i).opt("LAST_INVOICE_DATE"));
            }
            if (jsonContracts.getJSONObject(i).opt("NEXT_INVOICE_DATE") != null) {
                contract.setLast_invoice_date((Date) jsonContracts.getJSONObject(i).opt("NEXT_INVOICE_DATE"));
            }
            if (jsonContracts.getJSONObject(i).opt("JGR") != null) {
                contract.setJgr((int) jsonContracts.getJSONObject(i).opt("JGR"));
            }
            contract.setComments((String) jsonContracts.getJSONObject(i).opt("COMMENTS"));
            if (jsonContracts.getJSONObject(i).opt("IS_ACTIVE") != null) {
                contract.set_active((boolean) jsonContracts.getJSONObject(i).opt("IS_ACTIVE"));
            }
            contract.setServer_ID((String) jsonContracts.getJSONObject(i).opt("SERVER_ID"));
            contract.setTotal_price((BigDecimal) jsonContracts.getJSONObject(i).opt("TOTAL_PRICE"));
            contracts.add(contract);
        }

        return contracts;
    }
}
