package com.fooditsolutions.authenticationservice;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import javax.annotation.PostConstruct;
import javax.crypto.KeyGenerator;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
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
    public String authenticateLogin(User loginUser) throws NoSuchAlgorithmException, ClassNotFoundException {
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        System.out.println("API GET");

        ResultSet resultSet;

        // Connect to db, try to get correct user
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("connection succeeded");
            String sql = "SELECT USERID, EMAIL, PASSWORD FROM TEST_LOGIN where Email like ? AND PASSWORD like ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, loginUser.getEmail());
                statement.setString(2, loginUser.getPassword());
                resultSet = statement.executeQuery();

                //TODO uitvogelen of er iets word teruggegeven

                // Authentication successful, generate a session key and return it
                String sessionKey = getKeyFromGenerator();
                testUser.setSessionKey(sessionKey);//TODO Niet dit
                System.out.println("Session key:" + sessionKey);
                return sessionKey;
            }
        } catch (SQLException e) {
            System.out.println("connection failed");
        }
        return null;
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