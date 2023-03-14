package com.fooditsolutions.authenticationservice;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.authenticationservice.controller.Security;
import com.fooditsolutions.authenticationservice.controller.SessionController;
import com.fooditsolutions.authenticationservice.model.Session;
import com.fooditsolutions.authenticationservice.model.User;
import com.google.gson.Gson;
import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

/**
 * Authenticates userdata passed to it through api calls
 */
@Path("auth")
public class AuthenticationResource {
    //User testUser = new User("Jarno", "jarno@mail.com", "ww");
    //private List<User> users;

    @PostConstruct
    public void init() {
        System.out.println("Authentication service started");
    }

    /**
     * Receives user credentials and tries to match them to a user int the database, if it matches it calls for the creation of a session key and sends it back as a response.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticateLogin(User loginUser) throws NoSuchAlgorithmException {
        System.out.println("API GET");
        //PropertiesController.getProperty().setDatastore("C287746F288DF2CB7292DD2EE29CFECD");
        System.out.println(PropertiesController.getProperty().getDatastore());
        String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/user?datastoreKey=" + PropertiesController.getProperty().getDatastore());

        System.out.println(responseString);

        int loginUserID = 0;
        Gson gson = new Gson();
        User[] users = gson.fromJson(responseString, User[].class);
        for (User user : users) {
            if (user.getEmail().equals(loginUser.getEmail()) || user.getName().equals(loginUser.getEmail())) {
                loginUserID = user.getId();
                System.out.println(loginUserID);
            }
        }
        if (loginUserID == 0) {
            return "Error: Username/Email and Password are incorrect!";
        }

        responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice() + "/user/" + loginUserID + "/validate?datastoreKey=" + PropertiesController.getProperty().getDatastore() + "&pwd=" + DigestUtils.md5Hex(loginUser.getPassword()));
        System.out.println(responseString);

        if (responseString.contains("true")) {
            String sessionKey = Security.getKeyFromGenerator();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            Session session = new Session(sessionKey, timestamp, loginUserID);

            SessionController.AddSession(session);

            System.out.println("Session key:" + sessionKey);

            return sessionKey;
        } else {
            return "Error: Username/Email and Password are incorrect!";
        }


    }

    /**
     * revieves a sessionkey to delete from the list of running sessions preventing a user from accessing any other pages unless they log back in.
     */
    @DELETE
    @Path("/{sessionKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void AuthenticateLogout(@PathParam("sessionKey") String sessionKey) {
        SessionController.DeleteSession(sessionKey);
    }


}