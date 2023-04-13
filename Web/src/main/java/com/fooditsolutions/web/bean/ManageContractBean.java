package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Contract;
import com.fooditsolutions.web.model.ContractDetail;
import org.primefaces.model.SortMeta;
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

import static java.lang.Integer.parseInt;

@ManagedBean
@SessionScoped
public class ManageContractBean extends HttpServlet implements Serializable {
    @Inject
    private EditContractBean editContractBean;
    private List<Contract> contracts;
    private List<Contract> filteredContracts;
    private Contract[] contracts2;
    private Contract selectedItem;
    private Contract updatedContract;
    private ContractDetail[] details;
    private List<ContractDetail> detailList;
    private List<SortMeta> sortBy;
    private boolean activeFilter = true;

    /**
     * Executes getContracts when generalContracts.xhtml is loaded.
     * This makes sure all contracts are on the page when it is loaded.
     * It also initializes the List used to sort items.
     */
    @PostConstruct
    public void init(){
        try {
            PropertiesController.init();
            retrieveContracts();

        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
        sortBy = new ArrayList<>();
    }

    /**
     * Sends a GET request forward for all contracts that are currently stored.
     * The returned value is put into a string before it is turned into an array of Contract objects to be used by the datatable in generalContracts.xhtml
     * All the dates in each contract are returned as 'long' variables and are parsed to proper dates.
     */
    public void retrieveContracts() throws IOException, ServletException {
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
        contracts= Arrays.asList(contracts2);
        filteredContracts=new ArrayList<>();
        for (Contract contract:contracts){
            if (contract.is_active){
                filteredContracts.add(contract);
            }
        }
    }

    /**
     * calls getContractDetails and puts the returned values in 'details'
     * @return redirects the user to the contractDetails.xhtml
     */
    public String ContractDetails() throws IOException {
        detailList=new ArrayList<>();
        details=getContractDetails(selectedItem.id,false);
        detailList= Arrays.asList(details);
        return "contractDetails.xhtml?faces-redirect=true&includeViewParams=true";
    }

    /**
     * Gets called when a user presses the search icon next to a contract entry.
     * It uses the id of the relevant contract to send a request forward for said contracts details.
     * The received value is then parsed to a string.
     * @return the string is then further parsed to the ContractDetail class as the return value
     */
    public ContractDetail[] getContractDetails(int id, boolean checkCS) throws IOException {
        String responseContractDetails = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/"+id+"/contractdetails?checkCS="+checkCS);

        System.out.println("ResponseString: "+responseContractDetails);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(responseContractDetails,ContractDetail[].class);
    }

    /**
     * Creates a session object for the user and puts the object of the contract they selected in it, so it can be retrieved and used later on.
     * Also deletes an existing contract and Editcontracts attribute that may already exist in the session.
     * @return redirects the user to editContract.xhtml
     */
    public String editContract(){
        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("contract")!=null) {
            //selectedItem = (Contract) session.getAttribute("contract");
            session.removeAttribute("contract");
        }
        if (session.getAttribute("EditContractBean")!=null){
            session.removeAttribute("EditContractBean");
        }


        session.setAttribute("contract", selectedItem);
        return "editContract.xhtml?faces-redirect=true";
    }
    public String createContract(){
        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("createContractBean")!=null){
            session.removeAttribute("createContractBean");
        }

        return "createContract.xhtml?faces-redirect=true";
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
                || filterContract.getClient().getName().toLowerCase().contains(filterText);
    }

    /**
     * Takes two string values, parses them to integers and compares them.
     * @return Integer.compare, to check which one is higher/lower.
     */
    public int customSortFunction(String nr1, String nr2) {
        // Convert the string value to a number for comparison
        int num1 = parseInt(nr1, 10);
        int num2 = parseInt(nr2, 10);

        // Compare the numeric values instead of the strings
        return Integer.compare(num1, num2);
    }

    public void updateActiveFilter() {
        // Update the filter value based on the value of the checkbox
        if (activeFilter) {
            filteredContracts=new ArrayList<>();
            for (Contract contract:contracts){
                if (contract.is_active){
                    filteredContracts.add(contract);
                }
            }
        } else {
            filteredContracts=new ArrayList<>();
            for (Contract contract:contracts){
                if (!contract.is_active){
                    filteredContracts.add(contract);
                }
            }
        }
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

    public List<SortMeta> getSortBy() {
        return sortBy;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public List<ContractDetail> getDetailList() {
        return detailList;
    }

    public boolean isActiveFilter() {
        return activeFilter;
    }

    public void setActiveFilter(boolean activeFilter) {
        this.activeFilter = activeFilter;
    }

    public List<Contract> getFilteredContracts() {
        return filteredContracts;
    }

    public void setFilteredContracts(List<Contract> filteredContracts) {
        this.filteredContracts = filteredContracts;
    }
}
