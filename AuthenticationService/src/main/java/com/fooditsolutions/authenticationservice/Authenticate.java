package com.fooditsolutions.authenticationservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fooditsolutions.authenticationservice.controller.SessionController;
import com.fooditsolutions.authenticationservice.model.Session;
import com.fooditsolutions.authenticationservice.model.User;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;

/**
 * Authenticates userdata passed to it through api calls
 */
@Path("auth")
public class Authenticate {
    private final String DB_URL = "jdbc:firebirdsql:localhost:/data/CENTRALSERVER.FDB";
    private final String USER = "sysdba";
    private final String PASS = "masterkey";
    User testUser = new User("Jarno", "jarno@mail.com", "ww");
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
    public String authenticateLogin(User loginUser) throws NoSuchAlgorithmException, ClassNotFoundException, IOException {
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        System.out.println("API GET");

        URL url = new URL("http://localhost:8080/DatastoreService-1.0-SNAPSHOT/api/user?datastoreKey=C287746F288DF2CB7292DD2EE29CFECD");
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
            System.out.println(responseString);

            int loginUserID = 0;
            Gson gson = new Gson();
            User[] users = gson.fromJson(responseString, User[].class);
            for (User user : users) {
                if (user.getEmail().equals(loginUser.getEmail())) {
                    loginUserID = user.getId();
                }
            }

            url = new URL("http://localhost:8080/DatastoreService-1.0-SNAPSHOT/api/user/" + loginUserID + "/validate?datastoreKey=C287746F288DF2CB7292DD2EE29CFECD&pwd=" + loginUser.getPassword());
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
                System.out.println(responseString);
            }
            if (responseString.contains("true")){
                String sessionKey = getKeyFromGenerator();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                Session session = new Session(sessionKey,timestamp,loginUserID);

                SessionController.AddSession(session);

                System.out.println("Session key:" + sessionKey);

                return sessionKey;
            }

            // print result
        } else {
            System.out.println("GET request did not work.");
        }
        return null;
    }

    @DELETE
    @Path("/{sessionKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void AuthenticateLogout(@PathParam("sessionKey") String sessionKey){
        SessionController.DeleteSession(sessionKey);
    }


    /**
     * generates a json web token that is viable for an hour to authenticate users trying to log in
     */
    public String GenerateJWT(String id) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(Duration.ofHours(1));

        String jws = JWT.create()
                .withIssuer("FIT")
                .withSubject(id)
                .withExpiresAt(Date.from(expirationTime))
                .sign(Algorithm.HMAC256("sleutel"));
        return jws;
    }


    /**
     * Uses a secure random number generator and an array of bytes that get hashed to create a secure session key and returns it.
     */
    private String generateSessionKey() throws NoSuchAlgorithmException {
        // Generate a random session key using a secure random number generator and hash it
        SecureRandom random = new SecureRandom();
        byte[] sessionKeyBytes = new byte[16];
        random.nextBytes(sessionKeyBytes);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedSessionKeyBytes = md.digest(sessionKeyBytes);

        // Convert the hashed session key to a string representation
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedSessionKeyBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Uses the KeyGenerator class to generate a secure session key using the Advanced Encryption Standard (AES) algorithm.
     */
    private static String getKeyFromGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey().toString();
    }
}