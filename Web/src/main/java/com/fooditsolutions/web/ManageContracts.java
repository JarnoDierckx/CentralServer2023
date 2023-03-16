package com.fooditsolutions.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Contract;
import com.fooditsolutions.web.model.ContractDetail;
import com.google.gson.*;
import javafx.scene.control.TableColumn;
import org.primefaces.event.CellEditEvent;
import org.primefaces.util.LangUtils;

import javax.annotation.ManagedBean;
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
import java.sql.Date;
import java.sql.Time;
import java.util.*;

@ManagedBean
@SessionScoped
public class ManageContracts extends HttpServlet {
    private List<Contract> contracts;
    private Contract[] contracts2;
    private Contract selectedItem;
    private Contract updatedContract;
    private ContractDetail[] details;

    /**
     * executes getContracts when generalContracts.xhtml is loaded.
     */
    @PostConstruct
    public void init(){
        try {
            PropertiesController.init();
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
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract");
        System.out.println("getContracts: "+response);

        /*URL url = new URL("http://localhost:8080/CentralServer2023API-1.0-SNAPSHOT/api/crudContract/");
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

            Gson gson = new Gson();
            //Contract[] contracts1=gson.fromJson(responseString,Contract[].class);
            contracts2=gson.fromJson(responseString,Contract[].class);

            for (Contract contract : contracts2) {
                System.out.println(contract.start_date);
                contract.start_date= new Date(contract.start_date.getTime());
                contract.last_invoice_date= new Date(contract.last_invoice_date.getTime());
                contract.last_invoice_period_start= new Date(contract.last_invoice_period_start.getTime());
                contract.last_invoice_period_end= new Date(contract.last_invoice_period_end.getTime());
            }
        }*/

        /*Gson gson = new Gson();
        //Contract[] contracts1=gson.fromJson(responseString,Contract[].class);
        contracts2=gson.fromJson(response,Contract[].class);*/

        byte[] jsonData = response.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract[].class);

        for (Contract contract : contracts2) {
            System.out.println(contract.start_date);
            if (contract.start_date != null) {
                contract.start_date = new Date(contract.start_date.getTime());
            }
            if (contract.last_invoice_date != null) {
                contract.last_invoice_date = new Date(contract.last_invoice_date.getTime());
            }
            if (contract.last_invoice_period_start != null) {
                contract.last_invoice_period_start = new Date(contract.last_invoice_period_start.getTime());
            }
            if (contract.last_invoice_period_end != null) {
                contract.last_invoice_period_end = new Date(contract.last_invoice_period_end.getTime());

            }
        }
    }

    public String ContractDetails() throws IOException {
        getContractDetails();
        return "contractDetails.xhtml?faces-redirect=true&includeViewParams=true";
    }

    /**
     * Gets called when a user presses the button next to a contract entry.
     * It uses the id of the relevent contract to send a request forward for said contracts details.
     * When it gets those details back, they are put in an array and the user gets redirected to a page where the details are put into a datatable.
     */
    public void getContractDetails() throws IOException {
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
        }
    }

    public String editContract() throws IOException {
        //getContractDetails();
        System.out.println(selectedItem.contract_number);
        return "editContract.xhtml?faces-redirect=true&includeViewParams=true";
    }

    public void updateContract() throws IOException {
        //selectedItem.start_date= new java.util.Date(selectedItem.start_date.getTime());
        System.out.println(selectedItem.start_date);
        long time=selectedItem.start_date.getTime();


        Gson gson = new Gson();
        String contractString=gson.toJson(selectedItem);
        //String detailString=gson.toJson(details);
        System.out.println("update: "+contractString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract", contractString);
    }

    public void onCellEdit(CellEditEvent event){
        Object oldValue=event.getOldValue();
        Object newValue=event.getNewValue();
        System.out.println(newValue);
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

    public Contract getUpdatedContract() {
        return updatedContract;
    }

    public void setUpdatedContract(Contract updatedContract) {
        this.updatedContract = updatedContract;
    }
}
