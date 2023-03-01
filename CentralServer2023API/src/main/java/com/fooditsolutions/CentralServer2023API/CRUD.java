package com.fooditsolutions.CentralServer2023API;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Receives api calls from the front end and passes them to the correct Service.
 */
@Path("/crud")
public class CRUD {

    @PostConstruct
    public void init(){
        System.out.println("Api Service started");
    }

    /**
     * Receives login credentials and passes them to the AuthenticationService, it then receives a session key for the user that gets passed back as a response.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public String Read(User loginUser) throws IOException {
        String POST_PARAMS = String.format("{\"email\": \"%s\",\"password\": \"%s\"}", loginUser.getEmail(), loginUser.getPassword());
        System.out.println(POST_PARAMS);

        URL url=new URL("http://localhost:8080/AuthenticationService-1.0-SNAPSHOT/authapi/auth/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        System.out.println("POST Response Code :  " + responseCode);
        System.out.println("POST Response Message : " + connection.getResponseMessage());

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Authentication successful, retrieve the session key from the API response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = in.readLine();
            in.close();
            System.out.println(response);
            return response;
        } else {
            // Authentication failed, return null
            return null;
        }

    }
}
