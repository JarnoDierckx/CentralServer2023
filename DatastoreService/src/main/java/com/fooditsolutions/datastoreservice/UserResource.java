package com.fooditsolutions.datastoreservice;

import com.fooditsolutions.datastoreservice.controller.DBThunderbird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.User;
import org.json.JSONArray;


import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

@Path("/user")
public class UserResource {

        @GET
        @Produces("application/json")
        public List<User> getUsers(@QueryParam("datastoreKey") String datastoreKey) {
            JSONArray jsonUsers = new JSONArray();
            for (DatastoreObject ds : Datastores.getDatastores()) {
                if (datastoreKey.equals(ds.getKey())) {
                    jsonUsers = DBThunderbird.executeSQL(ds, "SELECT * FROM CS_USER");
                }
            }
            List<User> users = new ArrayList<>();
            for (int i = 0; i < jsonUsers.length(); i++) {
                User user = new User();

                user.setName((String) jsonUsers.getJSONObject(i).get("USERNAME"));
                user.setPassword((String) jsonUsers.getJSONObject(i).get("PASSWD"));
                users.add(user);
            }
            return users;
        }

    @GET
    @Produces("application/json")
    @Path("/{userId}")
    public List<User> getUser(@QueryParam("userId") String userId,@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonUsers = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonUsers = DBThunderbird.executeSQL(ds, "SELECT * FROM CS_USER WHERE ID = '"+userId+"';");
            }
        }
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonUsers.length(); i++) {
            User user = new User();

            user.setName((String) jsonUsers.getJSONObject(i).get("USERNAME"));
            user.setPassword((String) jsonUsers.getJSONObject(i).get("PASSWD"));
            users.add(user);
        }
        return users;
    }

    @GET
    @Produces("application/json")
    @Path("/{userId}/validate")
    public String getUser(@QueryParam("userId") String userId,
                              @QueryParam("datastoreKey") String datastoreKey,
                              @QueryParam("pwd") String pwd) {
        JSONArray jsonUsers = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonUsers = DBThunderbird.executeSQL(ds, "SELECT * FROM CS_USER WHERE PASSWD = '"+pwd+"';");
            }
        }
        if(jsonUsers.length()==1){
            return "{'valid':'true'}";
        }else{
            return "{'valid':'false'}";
        }

    }
}
