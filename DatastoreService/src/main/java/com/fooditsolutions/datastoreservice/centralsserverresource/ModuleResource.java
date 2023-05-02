package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.Module;
import com.fooditsolutions.datastoreservice.model.centralserver.ModuleId;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


    @Path("/module")
    public class ModuleResource {
        @GET
        @Produces("application/json")
        public List<Module> getModuleIds(@QueryParam("datastoreKey") String datastoreKey,
                                         @QueryParam("server") String serverId,
                                         @QueryParam("client") String clientId) {
            JSONArray jsonModuleIds = new JSONArray();

            for (DatastoreObject ds : Datastores.getDatastores()) {
                if (datastoreKey.equals(ds.getKey())) {
                    String sql = "SELECT * FROM MODULE";
                    if(clientId!=null && clientId!="") {
                        sql += " Left join SERVER ON MODULE.SERVER_DBB_ID = SERVER.DBB_ID";
                    }
                    sql += " WHERE SERVER_DBB_ID IS NOT NULL";
                    if(clientId!=null && clientId!="") {
                        sql += " AND SERVER.CLIENT_DBB_ID = " + clientId;
                    }
                    if(serverId!=null && serverId!=""){
                        sql += " AND SERVER_DBB_ID = " + serverId;
                    }
                    jsonModuleIds = DBFirebird.executeSQL(ds, sql);
                }
            }
            List<Module> moduleList = new ArrayList<>();
            for (int i = 0; i < jsonModuleIds.length(); i++) {
                Module module = new Module();
                module.setDBB_ID((BigDecimal) jsonModuleIds.getJSONObject(i).get("DBB_ID"));
                module.setName((String) jsonModuleIds.getJSONObject(i).get("NAME"));
                module.setSERVER_DBB_ID((BigDecimal) jsonModuleIds.getJSONObject(i).get("SERVER_DBB_ID"));
                module.setModules_Index((int) jsonModuleIds.getJSONObject(i).get("MODULES_INDEX"));
                module.setValiduntil((Date)jsonModuleIds.getJSONObject(i).get("VALIDUNTIL"));
                module.setTrial((boolean) jsonModuleIds.getJSONObject(i).get("ISTRIAL"));
                moduleList.add(module);
            }

            return moduleList;
        }

        @GET
        @Produces("application/json")
        @Path("/{moduleId}")
        public Module getModuleId(
                @PathParam("moduleId") String moduleId,
                @QueryParam("datastoreKey") String datastoreKey) {
            JSONArray jsonModules = new JSONArray();

            for (DatastoreObject ds : Datastores.getDatastores()) {
                if (datastoreKey.equals(ds.getKey())) {
                    jsonModules = DBFirebird.executeSQL(ds, "SELECT * FROM MODULE WHERE DBB_ID="+moduleId);
                }
            }
            List<Module> moduleList = new ArrayList<>();
            for (int i = 0; i < jsonModules.length(); i++) {
                Module module = new Module();
                module.setDBB_ID((BigDecimal) jsonModules.getJSONObject(i).get("DBB_ID"));
                module.setName((String) jsonModules.getJSONObject(i).get("NAME"));
                module.setSERVER_DBB_ID((BigDecimal) jsonModules.getJSONObject(i).get("SERVER_DBB_ID"));
                module.setModules_Index((int) jsonModules.getJSONObject(i).get("MODULES_INDEX"));
                module.setValiduntil((Date)jsonModules.getJSONObject(i).get("VALIDUNTIL"));
                moduleList.add(module);
            }

            return moduleList.get(0);
        }
    }

