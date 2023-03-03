package com.fooditsolutions.datastoreservice;

import com.fooditsolutions.datastoreservice.controller.DBThunderbird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/datastore")
public class DatastoreServiceResource {

    @GET
    @Produces("application/json")
    public List<DatastoreObject> getDatastoreResources(@Context HttpServletRequest request, @Context HttpServletResponse response) {
        response.addCookie(new Cookie("SessionKey","QWERDCD151515151551"));
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

    @POST
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addDatastoreResources(List<DatastoreObject> datastoreObjects) {
        Datastores.addDatastores(datastoreObjects);
        String s = "";
    }
    @DELETE
    @Produces("application/json")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteDatastoreResources(List<DatastoreObject> datastoreObjects) {
        Datastores.deleteDatastores(datastoreObjects);
        String s = "";
    }

    @DELETE
    @Path("/{datastoreKey}")
    @Produces("application/json")
    public void delDatastoreRequestFrom(
            @PathParam("datastoreKey") String datastoreKey) {
        Datastores.deleteDatastore(datastoreKey);

    }
}
