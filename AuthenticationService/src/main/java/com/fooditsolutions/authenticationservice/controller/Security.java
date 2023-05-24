package com.fooditsolutions.authenticationservice.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

public class Security {
    /**
     * Uses the KeyGenerator class to generate a secure session key using the Advanced Encryption Standard (AES) algorithm.
     */
    public static String getKeyFromGenerator() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();
        byte[] rawData = key.getEncoded();
        String encodedKey = Base64.getEncoder().encodeToString(rawData);
        encodedKey=encodedKey.replace("/","");

        System.out.println("KEY: " +encodedKey);
        return encodedKey;
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
     * generates a json web token that is viable for an hour to authenticate users trying to log in
     */
    public static String GenerateJWT(String id) {
        Instant now = Instant.now();
        Instant expirationTime = now.plus(Duration.ofHours(1));

        String jws = JWT.create()
                .withIssuer("FIT")
                .withSubject(id)
                .withExpiresAt(Date.from(expirationTime))
                .sign(Algorithm.HMAC256("sleutel"));
        return jws;
    }
}
