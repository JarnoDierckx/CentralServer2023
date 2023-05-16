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

    /**
     * Retrieves and returns all Modules related to a specific server and client.
     * @param datastoreKey the key of the used database.
     * @param serverId the server ID used for filtering the modules by server
     * @param clientId the client ID used for filtering the modules by client
     * @return List<Module>
     */
    @GET
    @Produces("application/json")
    public List<Module> getModuleIds(@QueryParam("datastoreKey") String datastoreKey,
                                     @QueryParam("server") String serverId,
                                     @QueryParam("client") String clientId) {
        JSONArray jsonModuleIds = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                String sql = "SELECT * FROM MODULE";
                if (clientId != null && clientId != "") {
                    sql += " Left join SERVER ON MODULE.SERVER_DBB_ID = SERVER.DBB_ID";
                }
                sql += " WHERE SERVER_DBB_ID IS NOT NULL";
                if (clientId != null && clientId != "") {
                    sql += " AND SERVER.CLIENT_DBB_ID = " + clientId;
                }
                if (serverId != null && serverId != "") {
                    sql += " AND SERVER_DBB_ID = " + serverId;
                }
                jsonModuleIds = DBFirebird.executeSQL(ds, sql);
            }
        }
        List<Module> moduleList = new ArrayList<>();
        for (int i = 0; i < jsonModuleIds.length(); i++) {
            Module module = new Module();
            module.setDBB_ID((BigDecimal) jsonModuleIds.getJSONObject(i).opt("DBB_ID"));
            module.setName((String) jsonModuleIds.getJSONObject(i).opt("NAME"));
            module.setSERVER_DBB_ID((BigDecimal) jsonModuleIds.getJSONObject(i).opt("SERVER_DBB_ID"));
            if (jsonModuleIds.getJSONObject(i).opt("MODULES_INDEX") != null) {
                module.setModules_Index((int) jsonModuleIds.getJSONObject(i).opt("MODULES_INDEX"));
            }
            module.setValiduntil((Date) jsonModuleIds.getJSONObject(i).opt("VALIDUNTIL"));
            if (jsonModuleIds.getJSONObject(i).opt("ISTRIAL") != null) {
                module.setTrial((boolean) jsonModuleIds.getJSONObject(i).opt("ISTRIAL"));
            }

            moduleList.add(module);
        }
        return moduleList;
    }

    /**
     * Retrieves a single Module objects from the database.
     * @param moduleId the ID for the module
     * @param datastoreKey the key for the used database.
     * @return a single Module.
     */
    @GET
    @Produces("application/json")
    @Path("/{moduleId}")
    public Module getModuleId(
            @PathParam("moduleId") String moduleId,
            @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonModules = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonModules = DBFirebird.executeSQL(ds, "SELECT * FROM MODULE WHERE DBB_ID=" + moduleId);
            }
        }
        List<Module> moduleList = new ArrayList<>();
        for (int i = 0; i < jsonModules.length(); i++) {
            Module module = new Module();
            module.setDBB_ID((BigDecimal) jsonModules.getJSONObject(i).opt("DBB_ID"));
            module.setName((String) jsonModules.getJSONObject(i).opt("NAME"));
            module.setSERVER_DBB_ID((BigDecimal) jsonModules.getJSONObject(i).opt("SERVER_DBB_ID"));
            if (jsonModules.getJSONObject(i).opt("MODULES_INDEX") != null) {
                module.setModules_Index((int) jsonModules.getJSONObject(i).opt("MODULES_INDEX"));
            }
            module.setValiduntil((Date) jsonModules.getJSONObject(i).opt("VALIDUNTIL"));
            if (jsonModules.getJSONObject(i).opt("ISTRIAL") != null) {
                module.setTrial((boolean) jsonModules.getJSONObject(i).opt("ISTRIAL"));
            }

            moduleList.add(module);
        }

        return moduleList.get(0);
    }

    /**
     * retrieves and returns a List of all Modules stored in the database that have a server ID.
     * @param datastoreKey the key for the used database.
     * @return List<Module>
     */
    @GET
    @Produces("application/json")
    @Path("/all")
    public List<Module> getModulesWithServerIDs(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonModules = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonModules = DBFirebird.executeSQL(ds, "SELECT * FROM MODULE WHERE SERVER_DBB_ID IS NOT NULL");
            }
        }
        List<Module> moduleList = new ArrayList<>();
        for (int i = 0; i < jsonModules.length(); i++) {
            Module module = new Module();
            module.setDBB_ID((BigDecimal) jsonModules.getJSONObject(i).opt("DBB_ID"));
            module.setName((String) jsonModules.getJSONObject(i).opt("NAME"));
            module.setSERVER_DBB_ID((BigDecimal) jsonModules.getJSONObject(i).opt("SERVER_DBB_ID"));
            if (jsonModules.getJSONObject(i).opt("MODULES_INDEX") != null) {
                module.setModules_Index((int) jsonModules.getJSONObject(i).opt("MODULES_INDEX"));
            }
            module.setValiduntil((Date) jsonModules.getJSONObject(i).opt("VALIDUNTIL"));
            if (jsonModules.getJSONObject(i).opt("ISTRIAL") != null) {
                module.setTrial((boolean) jsonModules.getJSONObject(i).opt("ISTRIAL"));
            }

            moduleList.add(module);
        }
        return moduleList;
    }
}

