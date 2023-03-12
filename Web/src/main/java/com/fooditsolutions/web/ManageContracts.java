package com.fooditsolutions.web;

import com.fooditsolutions.web.model.Contract;
import com.fooditsolutions.web.model.ContractDetail;
import com.google.gson.*;
import org.primefaces.util.LangUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

@Named
@SessionScoped
public class ManageContracts extends HttpServlet {
    private List<Contract> contracts;
    private Contract[] contracts2;
    private Contract selectedItem;
    private ContractDetail[] details;

    /**
     * executes getContracts when generalContracts.xhtml is loaded.
     */
    @PostConstruct
    public void init(){
        try {
            getContracts();
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a GET request forward for all contracts that are currently stored.
     * The returned value is put into a string before it is turned into an array of Contract objects to be used by the datatable in generalContracts.xhtml
     */
    public void getContracts() throws IOException, ServletException {
        System.out.println("Starting read in ManageContracts");
        URL url = new URL("http://localhost:8080/CentralServer2023API-1.0-SNAPSHOT/api/crudContract/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("in " + in);

            StringBuilder response = new StringBuilder();

            //put return in a string
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String responseString = String.valueOf(response);
            System.out.println("ResponseString: "+responseString);

            //turn string into array of objects
            /* had to add the GsonBuilder() as there was an issue wiht the epoch date conversion
            * https://itecnote.com/tecnote/java-convert-string-date-to-object-yields-invalid-time-zone-indicator-0/
             */
            /*Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
                        }
                    })
                    .create();*/
            Gson gson = new Gson();
            //Contract[] contracts1=gson.fromJson(responseString,Contract[].class);
            contracts2=gson.fromJson(responseString,Contract[].class);
            String s = "";
            /*System.out.println("Contracts in array: "+ Arrays.toString(contracts1));
            contracts=Arrays.asList(contracts1);
            System.out.println("Contracts: "+contracts.toString());*/
            //return Arrays.asList(contracts1);
        }else {
            //return null;
        }
    }

    /**
     * Gets called when a user presses the button next to a contract entry.
     * It uses the id of the relevent contract to send a request forward for said contracts details.
     * When it gets those details back, they are put in an array and the user gets redirected to a page where the details are put into a datatable.
     */
    public String getContractDetails() throws IOException {
        URL url = new URL("http://localhost:8080/CentralServer2023API-1.0-SNAPSHOT/api/crudContract/"+selectedItem.getId());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("in " + in);

            StringBuilder response = new StringBuilder();

            //put return in a string
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String responseString = String.valueOf(response);
            System.out.println("ResponseString: "+responseString);
            Gson gson = new Gson();
            details=gson.fromJson(responseString,ContractDetail[].class);
            return "contractDetails.xhtml?faces-redirect=true&includeViewParams=true";
        }else {
            return null;
        }
    }

    /**
     * Takes the value in the search bar on generalContracts.xhtml and checks it against the values of several variables.
     * If any values match the searched string, the entire object will then be shown in the datatable.
     */
    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isBlank(filterText)) {
            return true;
        }

        Contract filterContract = (Contract) value;
        return filterContract.getContract_number().toLowerCase().contains(filterText)
                || String.valueOf(filterContract.getClient_id()).contains(filterText);
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }


    public Contract getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Contract selectedItem) {
        this.selectedItem = selectedItem;
    }

    public Contract[] getContracts2() {
        return contracts2;
    }

    public void setContracts2(Contract[] contracts2) {
        this.contracts2 = contracts2;
    }

    public ContractDetail[] getDetails() {
        return details;
    }

    public void setDetails(ContractDetail[] details) {
        this.details = details;
    }
}
