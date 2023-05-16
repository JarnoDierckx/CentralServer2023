package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.ModuleId;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/moduleid")
public class ModuleIdResource {

    /**
     * Retrieves and returns all ModuleId objects in the database.
     * @param datastoreKey the key for the used database.
     * @return List<ModuleId>
     */
    @GET
    @Produces("application/json")
    public List<ModuleId> getModuleIds(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonModuleIds = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonModuleIds = DBFirebird.executeSQL(ds, "SELECT * FROM MODULEID");
            }
        }
        List<ModuleId> moduleIdList = new ArrayList<>();
        for (int i = 0; i < jsonModuleIds.length(); i++) {
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id((BigDecimal) jsonModuleIds.getJSONObject(i).get("DBB_ID"));
            moduleId.setName((String) jsonModuleIds.getJSONObject(i).get("NAME"));
            moduleIdList.add(moduleId);
        }

        return moduleIdList;
    }


    /**
     * Retrieves and returns a single ModuleId object from the database.
     * @param moduleidId the ID of the to be retrieved object.
     * @param datastoreKey the key of the used database.
     * @return A ModuleId object.
     */
    @GET
    @Produces("application/json")
    @Path("/{moduleidId}")
    public ModuleId getModuleId(
            @PathParam("moduleidId") String moduleidId,
            @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonModuleIds = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonModuleIds = DBFirebird.executeSQL(ds, "SELECT * FROM MODULEID WHERE DBB_ID="+moduleidId);
            }
        }
        List<ModuleId> moduleIdList = new ArrayList<>();
        for (int i = 0; i < jsonModuleIds.length(); i++) {
            ModuleId moduleId = new ModuleId();
            moduleId.setDbb_id((BigDecimal) jsonModuleIds.getJSONObject(i).get("DBB_ID"));
            moduleId.setName((String) jsonModuleIds.getJSONObject(i).get("NAME"));
            moduleIdList.add(moduleId);
        }

        return moduleIdList.get(0);
    }
}
