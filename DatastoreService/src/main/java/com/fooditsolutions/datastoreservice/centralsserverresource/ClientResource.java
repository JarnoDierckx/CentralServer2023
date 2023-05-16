package com.fooditsolutions.datastoreservice.centralsserverresource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.datastoreservice.controller.DBFirebird;
import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import com.fooditsolutions.datastoreservice.model.User;
import com.fooditsolutions.datastoreservice.model.centralserver.Client;
import org.json.JSONArray;

import javax.ws.rs.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/client")
public class ClientResource {

    /**
     * retrieves all clients from the database and turns them into Client objects.
     * @param datastoreKey the key for the database used.
     * @return a List of Client objects.
     */
    @GET
    @Produces("application/json")
    public List<Client> getClients(@QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonClients = new JSONArray();
        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonClients = DBFirebird.executeSQL(ds, "SELECT * FROM CLIENT");
            }
        }
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < jsonClients.length(); i++) {
            Client client = new Client();
            client.setDBB_ID((BigDecimal) jsonClients.getJSONObject(i).get("DBB_ID"));
            client.setName((String) jsonClients.getJSONObject(i).get("NAME"));
            if(jsonClients.getJSONObject(i).opt("PHONE")!=null) {
                client.setPhone((String) jsonClients.getJSONObject(i).get("PHONE"));
            }
            if(jsonClients.getJSONObject(i).opt("MOBILEPHONE")!=null) {
                client.setMobilePhone((String) jsonClients.getJSONObject(i).get("MOBILEPHONE"));
            }
            if(jsonClients.getJSONObject(i).opt("EMEGENCYPHONE")!=null) {
            client.setEmergancyPhone((String) jsonClients.getJSONObject(i).get("EMEGENCYPHONE"));
            }
            if(jsonClients.getJSONObject(i).opt("FAX")!=null) {
            client.setFax((String) jsonClients.getJSONObject(i).get("FAX"));
            }
            if(jsonClients.getJSONObject(i).opt("EMAIL")!=null) {
            client.setEmail((String) jsonClients.getJSONObject(i).get("EMAIL"));
            }
            if(jsonClients.getJSONObject(i).opt("ADDRESS")!=null) {
            client.setAddress((String) jsonClients.getJSONObject(i).get("ADDRESS"));
            }
            if(jsonClients.getJSONObject(i).opt("URL")!=null) {
            client.setURL((String) jsonClients.getJSONObject(i).get("URL"));
            }
            if(jsonClients.getJSONObject(i).opt("DESCRIPTION")!=null) {
            client.setDescription((String) jsonClients.getJSONObject(i).get("DESCRIPTION"));
            }


            clients.add(client);
        }
        return clients;

    }

    /**
     * retrieves the client in the database that corresponds to the given ID and turns it into a Client object.
     * @param clientId the ID of the client
     * @param datastoreKey
     * @return the Client object
     */
    @GET
    @Produces("application/json")
    @Path("/{customerId}")
    public Client getClient(@PathParam("customerId") String clientId, @QueryParam("datastoreKey") String datastoreKey) {
        JSONArray jsonClients = new JSONArray();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                jsonClients = DBFirebird.executeSQL(ds, "SELECT * FROM CLIENT WHERE DBB_ID=" +clientId);
            }
        }
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < jsonClients.length(); i++) {
            Client client = new Client();

            client.setDBB_ID((BigDecimal) jsonClients.getJSONObject(i).get("DBB_ID"));
            client.setName((String) jsonClients.getJSONObject(i).get("NAME"));
            if(jsonClients.getJSONObject(i).opt("PHONE")!=null) {
                client.setPhone((String) jsonClients.getJSONObject(i).get("PHONE"));
            }
            if(jsonClients.getJSONObject(i).opt("MOBILEPHONE")!=null) {
                client.setMobilePhone((String) jsonClients.getJSONObject(i).get("MOBILEPHONE"));
            }
            if(jsonClients.getJSONObject(i).opt("EMEGENCYPHONE")!=null) {
                client.setEmergancyPhone((String) jsonClients.getJSONObject(i).get("EMEGENCYPHONE"));
            }
            if(jsonClients.getJSONObject(i).opt("FAX")!=null) {
                client.setFax((String) jsonClients.getJSONObject(i).get("FAX"));
            }
            if(jsonClients.getJSONObject(i).opt("EMAIL")!=null) {
                client.setEmail((String) jsonClients.getJSONObject(i).get("EMAIL"));
            }
            if(jsonClients.getJSONObject(i).opt("ADDRESS")!=null) {
                client.setAddress((String) jsonClients.getJSONObject(i).get("ADDRESS"));
            }
            if(jsonClients.getJSONObject(i).opt("URL")!=null) {
                client.setURL((String) jsonClients.getJSONObject(i).get("URL"));
            }
            if(jsonClients.getJSONObject(i).opt("DESCRIPTION")!=null) {
                client.setDescription((String) jsonClients.getJSONObject(i).get("DESCRIPTION"));
            }


            clients.add(client);
        }
        return clients.get(0);

    }

}
