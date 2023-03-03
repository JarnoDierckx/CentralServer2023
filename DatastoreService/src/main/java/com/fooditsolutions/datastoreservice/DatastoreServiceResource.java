package com.fooditsolutions.datastoreservice;

import com.fooditsolutions.datastoreservice.controller.DBThunderbird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;


import javax.ws.rs.*;
import java.util.List;


@Path("/datastore")
public class DatastoreServiceResource {

    @GET
    @Produces("application/json")
    public List<DatastoreObject> getDatastoreResources() {
        return Datastores.getDatastores();
    }

    @GET
    @Path("/{datastoreKey}")
    @Produces("application/json")
    public String getRequestFrom(
            @PathParam("datastoreKey") String datastoreKey,
            @QueryParam("query") String query) {
        String result = "";

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                result = DBThunderbird.executeSQL(ds, query).toString();
            }
        }
        return result;
    }
}
