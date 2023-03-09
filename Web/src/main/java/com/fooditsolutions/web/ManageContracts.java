package com.fooditsolutions.web;

import com.fooditsolutions.web.model.Contract;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageContracts {
    private List<Contract> contracts = new ArrayList<>();

    public void getContracts() throws IOException, ServletException {

        System.out.println("Starting read");
        URL url = new URL("http://localhost:8080/ContractService-1.0-SNAPSHOT/api/contract");
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

            //return String.valueOf(response);
            Gson gson = new Gson();

        }

    }
}
