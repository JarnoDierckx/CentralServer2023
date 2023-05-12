package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.Module;
import com.fooditsolutions.datastoreservice.model.centralserver.ModuleId;
import com.fooditsolutions.datastoreservice.model.centralserver.Server;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("/server")
public class ServerResource{
    @GET
    @Produces("application/json")
    public List<Server> getServers(@QueryParam("datastoreKey") String datastoreKey,
                                   @QueryParam("client") String clientId) {
        JSONArray jsonValues = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                String sql = "SELECT * FROM SERVER WHERE CLIENT_DBB_ID IS NOT NULL";
                if(clientId!=null && clientId!=""){
                    sql += " AND CLIENT_DBB_ID = " + clientId;
                }
                jsonValues = DBFirebird.executeSQL(ds, sql);
            }
        }
        List<Server> objectList = new ArrayList<>();
        for (int i = 0; i < jsonValues.length(); i++) {
            Server object = new Server();
            object.setDBB_ID((BigDecimal) jsonValues.getJSONObject(i).opt("DBB_ID"));
            object.setID((String) jsonValues.getJSONObject(i).opt("ID"));
            object.setCLIENT_DBB_ID((BigDecimal) jsonValues.getJSONObject(i).opt("CLIENT_DBB_ID"));
            if (jsonValues.getJSONObject(i).opt("ISTRIAL") != null){
                object.setTrial((boolean) jsonValues.getJSONObject(i).opt("ISTRIAL"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOAM") != null){
                object.setMobileDevicesNOAM((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOAM"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOPM") != null){
                object.setMobileDevicesNOPM((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOPM"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOBS") != null){
                object.setMobileDevicesNOBS((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOBS"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCP") != null){
                object.setMobileDevicesNOCP((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCP"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNODS") != null){
                object.setMobileDevicesNODS((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNODS"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCA") != null){
                object.setMobileDevicesNOCA((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCA"));
            }
            objectList.add(object);
        }

        return objectList;
    }



    @GET
    @Produces("application/json")
    @Path("/all")
    public List<Server> getAllServers(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonValues = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                String sql = "SELECT * FROM SERVER WHERE CLIENT_DBB_ID IS NOT NULL";
                jsonValues = DBFirebird.executeSQL(ds, sql);
            }
        }
        List<Server> objectList = new ArrayList<>();
        for (int i = 0; i < jsonValues.length(); i++) {
            Server object = new Server();
            object.setDBB_ID((BigDecimal) jsonValues.getJSONObject(i).get("DBB_ID"));
            object.setID((String) jsonValues.getJSONObject(i).get("ID"));
            object.setCLIENT_DBB_ID((BigDecimal) jsonValues.getJSONObject(i).get("CLIENT_DBB_ID"));
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOAM") != null){
                object.setMobileDevicesNOAM((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOAM"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOPM") != null){
                object.setMobileDevicesNOPM((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOPM"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOBS") != null){
                object.setMobileDevicesNOBS((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOBS"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCP") != null){
                object.setMobileDevicesNOCP((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCP"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNODS") != null){
                object.setMobileDevicesNODS((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNODS"));
            }
            if (jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCA") != null){
                object.setMobileDevicesNOCA((int) jsonValues.getJSONObject(i).opt("MOBILEDEVICESNOCA"));
            }

            objectList.add(object);
        }

        return objectList;
    }

    @GET
    @Produces("application/json")
    @Path("/{serverId}")
    public Server getServerId(
            @PathParam("serverId") String objectId,
            @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonValues = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonValues = DBFirebird.executeSQL(ds, "SELECT * FROM MODULE WHERE DBB_ID="+objectId);
            }
        }
        List<Server> objectList = new ArrayList<>();
        for (int i = 0; i < jsonValues.length(); i++) {
            Server object = new Server();
            object.setDBB_ID((BigDecimal) jsonValues.getJSONObject(i).get("DBB_ID"));
            object.setID((String) jsonValues.getJSONObject(i).get("ID"));
            object.setCLIENT_DBB_ID((BigDecimal) jsonValues.getJSONObject(i).get("CLIENT_DBB_ID"));
            objectList.add(object);
        }

        return objectList.get(0);
    }
}


