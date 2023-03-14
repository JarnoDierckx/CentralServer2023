package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.CentralServer2023API.model.Contract;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.google.gson.Gson;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Path("/crudContract")
public class ContractResource {

    @PostConstruct
    public void init() {
        System.out.println("Api Service started");
    }

    /**
     * sends forward GET request for all contracts.
     * The recieved value is then returned back.
     */
    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getContract() throws IOException, ServletException {

        System.out.println("Starting read in ContractResource");
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

            return String.valueOf(response);
        }else {
            return null;
        }

    }

    /**
     * sends forward GET request for the contract details of whatever ID is send along.
     * The recieved value is then returned back.
     */
    @GET
    @Path("/{ContractID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public String getContractDetails(@PathParam("ContractID") String contractID) throws IOException, ServletException {

        System.out.println("Starting read in ContractResource");
        URL url = new URL("http://localhost:8080/ContractService-1.0-SNAPSHOT/api/contractDetail/"+contractID);
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

            return String.valueOf(response);
        }else {
            return null;
        }
    }
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/json")
    public void updateContract(Contract contract){
        Gson gson =new Gson();
        String contractString=gson.toJson(contract);
        HttpController.httpPost(PropertiesController.getProperty().getBase_url_contractservice()+"/contract/update?datastoreKey="+ PropertiesController.getProperty().getDatastore(), contractString);
    }
}
