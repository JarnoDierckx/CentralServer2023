package com.fooditsolutions.authenticationservice;

import com.fooditsolutions.authenticationservice.controller.SessionController;
import com.fooditsolutions.authenticationservice.model.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/session")
public class SessionResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Session> GetSessions(){
        return SessionController.getSessions();
    }

    @GET
    @Path("/{sessionKey}/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public String ValidateSession(@PathParam("sessionKey") String sessionKey){
        if(SessionController.Validate(sessionKey)){
            return "{'valid':'true'}";
        }
        return "{'valid':'false'}";
    }
}
