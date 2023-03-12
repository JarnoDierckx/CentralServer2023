package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.centralserver.Bjr;
import com.fooditsolutions.datastoreservice.model.centralserver.Client;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/bjr")
public class BjrResource {
    @GET
    @Produces("application/json")
    public List<Bjr> getBjrs(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonBjrs = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonBjrs = DBFirebird.executeSQL(ds, "SELECT * FROM BJR");
            }
        }
        List<Bjr> bjrs = new ArrayList<>();
        for (int i = 0; i < jsonBjrs.length(); i++) {
            Bjr bjr = new Bjr();
            bjr.setId((Integer) jsonBjrs.getJSONObject(i).get("ID"));
            bjr.setName((String) jsonBjrs.getJSONObject(i).get("NAME"));
            bjrs.add(bjr);
            }

        return bjrs;

    }

    @GET
    @Produces("application/json")
    @Path("/{bjrId}")
    public Bjr getBjr(
            @PathParam("bjrId") String bjrId,
            @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonBjrs = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonBjrs = DBFirebird.executeSQL(ds, "SELECT * FROM BJR WHERE ID="+bjrId);
            }
        }
        List<Bjr> bjrs = new ArrayList<>();
        for (int i = 0; i < jsonBjrs.length(); i++) {
            Bjr bjr = new Bjr();
            bjr.setId((Integer) jsonBjrs.getJSONObject(i).get("ID"));
            bjr.setName((String) jsonBjrs.getJSONObject(i).get("NAME"));
            bjrs.add(bjr);
        }

        return bjrs.get(0);

    }
}
