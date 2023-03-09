package com.fooditsolutions.authenticationservice;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.authenticationservice.controller.Security;
import com.fooditsolutions.authenticationservice.controller.SessionController;
import com.fooditsolutions.authenticationservice.model.Session;
import com.fooditsolutions.authenticationservice.model.User;
import com.google.gson.Gson;

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
        /*
        URL url = new URL("http://localhost:8080/DatastoreService-1.0-SNAPSHOT/api/user?datastoreKey="+ PropertiesController.getProperty().getDatastore());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("in " + in);

            StringBuilder response = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseString = String.valueOf(response);

         */
            String responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/user?datastoreKey="+ PropertiesController.getProperty().getDatastore());
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
            if (loginUserID==0){
                return "Error: Username/Email and Password are incorrect!";
            }
            /*
            url = new URL("http://localhost:8080/DatastoreService-1.0-SNAPSHOT/api/user/" + loginUserID + "/validate?datastoreKey="+ PropertiesController.getProperty().getDatastore()+"&pwd=" + loginUser.getPassword());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                System.out.println("in " + in);

                response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                responseString = String.valueOf(response);

             */
                responseString = HttpController.httpGet(PropertiesController.getProperty().getBase_url_datastoreservice()+"/user/" + loginUserID + "/validate?datastoreKey="+ PropertiesController.getProperty().getDatastore()+"&pwd=" + loginUser.getPassword());
                System.out.println(responseString);

            if (responseString.contains("true")){
                String sessionKey = Security.getKeyFromGenerator();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                Session session = new Session(sessionKey,timestamp,loginUserID);

                SessionController.AddSession(session);

                System.out.println("Session key:" + sessionKey);

                return sessionKey;
            }else{
                return "Error: Username/Email and Password are incorrect!";
            }

            // print result
        /*} else {
            System.out.println("GET request did not work.");
        }*/
        //return null;
    }

    @DELETE
    @Path("/{sessionKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void AuthenticateLogout(@PathParam("sessionKey") String sessionKey){
        SessionController.DeleteSession(sessionKey);
    }







}