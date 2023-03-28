package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Contract;
import com.fooditsolutions.web.model.ContractDetail;
import org.primefaces.util.LangUtils;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.util.*;

@ManagedBean
@SessionScoped
public class ManageContracts extends HttpServlet implements Serializable {
    @Inject
    private EditContracts editContracts;
    private List<Contract> contracts;
    private Contract[] contracts2;
    private Contract selectedItem;
    private Contract updatedContract;
    private ContractDetail[] details;

    /**
     * executes getContracts when generalContracts.xhtml is loaded.
     * This makes sure all contracts are on the page when it is loaded.
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
     * All the dates in each contract are returned as 'long' variables and are parsed to proper dates.
     */
    public void getContracts() throws IOException, ServletException {
        System.out.println("Starting read in ManageContracts");
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract");
        System.out.println("getContracts: "+response);

        byte[] jsonData = response.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        contracts2 = mapper.readValue(jsonData, Contract[].class);

        for (Contract contract : contracts2) {
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

    /**
     * calls getContractDetails and puts the returned values in 'details'
     * @return redirects the user to the contractDetails.xhtml
     */
    public String ContractDetails() throws IOException {
        details=getContractDetails(selectedItem.id);
        return "contractDetails.xhtml?faces-redirect=true&includeViewParams=true";
    }

    /**
     * Gets called when a user presses the search icon next to a contract entry.
     * It uses the id of the relevant contract to send a request forward for said contracts details.
     * The received value is then parsed to a string.
     * @return the string is then further parsed to the ContractDetail class as the return value
     */
    public ContractDetail[] getContractDetails(int id) throws IOException {
        String responseContractDetails = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/"+id+"/contractdetails?checkCS=true");

        System.out.println("ResponseString: "+responseContractDetails);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(responseContractDetails,ContractDetail[].class);
    }

    /**
     * Creates a session object for the user and puts the object of the contract they selected in it, so it can be retrieved and used later on.
     * @return redirects the user to editContract.xhtml
     */
    public String editContract(){
        //getContractDetails();

        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute("contract", selectedItem);
        return "editContract.xhtml?faces-redirect=true";
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
