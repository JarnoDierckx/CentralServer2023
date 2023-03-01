package com.fooditsolutions.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class takes the credentials from index.xhtml and passes them to CentralServer2023API
 */
public class HandleLogin {
    String email;
    String password;

    /**
    *This function takes the credentials and puts them in a json string. That string gets send to CentralServer2023API, and after that PassCredentials recieves the session key provided the
     * credentials given match that of a registered user.
     */
    public String PassCredentials() throws IOException {
        String POST_PARAMS = String.format("{\"email\": \"%s\",\"password\": \"%s\"}", email, password);

        URL url=new URL("http://localhost:8080/CentralServer2023API-1.0-SNAPSHOT/api/crud/");
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
            return response;
        } else {
            // Authentication failed, return null
            return null;
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
