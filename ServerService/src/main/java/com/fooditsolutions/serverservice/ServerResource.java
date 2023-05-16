package com.fooditsolutions.serverservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.serverservice.model.Server;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/server")
public class ServerResource {

    /**
     * Retrieves all server objects stored in the database.
     * @return A List of server objects.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public List<Server> getServer() throws JsonProcessingException {
        List<Server> result = new ArrayList<>();
        String jsonServers = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/server/all?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Server[] servers=mapper.readValue(jsonServers,Server[].class);
        result = Arrays.asList(servers);
        return result;
    }

    /**
     * retrieves a single server.
     * unfinished.
     */
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    @Path("/{serverid}")
    public Server getServer(@PathParam("serverid") String serverId) {
        Server result = new Server();

        return result;
    }
}
