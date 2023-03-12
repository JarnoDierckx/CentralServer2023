package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.Bjr;
import com.fooditsolutions.datastoreservice.model.centralserver.ModeuleId;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/moduleid")
public class ModuleIdResource {
    @GET
    @Produces("application/json")
    public List<ModeuleId> getModuleIds(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonModuleIds = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonModuleIds = DBFirebird.executeSQL(ds, "SELECT * FROM MODULEID");
            }
        }
        List<ModeuleId> modeuleIdList  = new ArrayList<>();
        for (int i = 0; i < jsonModuleIds.length(); i++) {
            ModeuleId modeuleId = new ModeuleId();
            modeuleId.setDbb_id((BigDecimal) jsonModuleIds.getJSONObject(i).get("DBB_ID"));
            modeuleId.setName((String) jsonModuleIds.getJSONObject(i).get("NAME"));
            modeuleIdList.add(modeuleId);
        }

        return modeuleIdList;
    }

    @GET
    @Produces("application/json")
    @Path("/{moduleidId}")
    public ModeuleId getModuleId(
            @PathParam("moduleidId") String moduleidId,
            @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonModuleIds = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonModuleIds = DBFirebird.executeSQL(ds, "SELECT * FROM MODULEID WHERE DBB_ID="+moduleidId);
            }
        }
        List<ModeuleId> modeuleIdList  = new ArrayList<>();
        for (int i = 0; i < jsonModuleIds.length(); i++) {
            ModeuleId modeuleId = new ModeuleId();
            modeuleId.setDbb_id((BigDecimal) jsonModuleIds.getJSONObject(i).get("DBB_ID"));
            modeuleId.setName((String) jsonModuleIds.getJSONObject(i).get("NAME"));
            modeuleIdList.add(modeuleId);
        }

        return modeuleIdList.get(0);
    }
}
