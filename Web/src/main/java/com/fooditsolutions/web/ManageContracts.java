package com.fooditsolutions.web;

import com.fooditsolutions.web.model.Contract;
import com.google.gson.Gson;
import org.primefaces.util.LangUtils;

import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ManageContracts {
    private List<Contract> contracts;
    private Contract selectedItem;



    public List<Contract> getContracts() throws IOException, ServletException {

        System.out.println("Starting read in ManageContracts");
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
            String responseString = String.valueOf(response);
            //return String.valueOf(response);
            Gson gson = new Gson();
            Contract[] contracts1=gson.fromJson(responseString,Contract[].class);
            contracts=Arrays.asList(contracts1);
            return Arrays.asList(contracts1);
        }else {
            return null;
        }
    }

    public boolean globalFilterFunction(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase();
        if (LangUtils.isBlank(filterText)) {
            return true;
        }

        Contract filterContract = (Contract) value;
        return filterContract.getContract_number().toLowerCase().contains(filterText)
                || String.valueOf(filterContract.getCLIENT_ID()).contains(filterText);
    }

    public String viewContract() {
        return "selected.xhtml?faces-redirect=true&includeViewParams=true";
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
}
