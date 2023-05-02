package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.*;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.SortMeta;
import org.primefaces.util.LangUtils;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

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
    private boolean inActiveFilter = false;
    private History[] allHistory;
    private List<History> selectedHistory;
    private Client[] clients;
    private List<Client> clientList;
    private List<Client> selectedClientList =new ArrayList<>();
    private BigDecimal yearlyFacturationAmount = BigDecimal.valueOf(0);
    private BigDecimal MonthlyFacturationAmount = BigDecimal.valueOf(0);

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
            clients=retrieveClients();
            clientList= Arrays.asList(clients);
            allHistory=retrieveHistory();

        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
        sortBy = new ArrayList<>();
        for (Contract contract: contracts2){
            if (contract.is_active && contract.getInvoice_frequency().equals("J") && contract.getTotal_price() != null){
                yearlyFacturationAmount=yearlyFacturationAmount.add(contract.getTotal_price());
            }else if (contract.is_active && contract.getInvoice_frequency().equals("M") && contract.getTotal_price() != null){
                MonthlyFacturationAmount=MonthlyFacturationAmount.add(contract.getTotal_price());
            }
        }
    }

    /**
     * Sends a GET request forward for all contracts that are currently stored.
     * The returned value is put into a string before it is turned into a list of Contract objects to be used by the datatable in generalContracts.xhtml
     * All the dates in each contract are returned as 'long' variables and are parsed to proper dates.
     * filteredContracts is used to keep the active and inactive contracts apart
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
            if (contract.next_invoice_date != null) {
                contract.next_invoice_date = new Date(contract.next_invoice_date.getTime());
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
     * retrieves all clients
     * @return maps all values from the api response into an array of Client objects
     */
    public Client[] retrieveClients() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/clients");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Client[].class);
    }

    /**
     * calls getContractDetails and puts the returned values in 'details'
     * @return redirects the user to the contractDetails.xhtml
     */
    public String ContractDetails() throws IOException {
        detailList=new ArrayList<>();
        details=getContractDetails(selectedItem.id,false);
        detailList= Arrays.asList(details);
        selectedHistory=addSelectedHistory(allHistory);
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

    public History[] retrieveHistory() throws JsonProcessingException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/history?full=true");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, History[].class);
    }

    /**
     * Creates a session object for the user and puts the object of the contract they selected in it, so it can be retrieved and used later on.
     * Also deletes any existing contract and Editcontracts attributes that may already exist in the session.
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

    /**
     * First checks for an existing createContractBean and deletes it.
     * @return redirects to the create contract page
     */
    public String createContract(){
        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("createContractBean")!=null){
            session.removeAttribute("createContractBean");
        }
        session.setAttribute("allContracts",contracts2);

        return "createContract.xhtml?faces-redirect=true";
    }

    /**
     * Takes the value in the search bar on generalContracts.xhtml and checks the contract number and client name of each object.
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

    public List<History> addSelectedHistory(History[] history){
        List<History> selectedHistory=new ArrayList<>();
        for (History h: history){
            if (h.getAttribute_id()==selectedItem.getId()){
                selectedHistory.add(h);
            }
            for (ContractDetail detail: details){
                if (detail.getID()==h.getAttribute_id()){
                    selectedHistory.add(h);
                }
            }
        }
        return selectedHistory;
    }

    /**
     * currently redundant
     */
    public int customSortFunction(String s1, String s2) {
        if (s1.matches("\\d+") && s2.matches("\\d+")) {
            // both strings are just numbers
            return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
        } else if (s1.matches("\\d+")) {
            // s1 is just a number
            return -1;
        } else if (s2.matches("\\d+")) {
            // s2 is just a number
            return 1;
        } else {
            // neither string is just a number
            // compare as strings
            return s1.compareTo(s2);
        }
    }

    /**
     * Fills the list used in the datatable to determine if only active/inactive contracts should be shown
     */
    public void updateActiveFilter() {
        // Update the filter value based on the value of the checkbox and what clients are selected
        if (!inActiveFilter) {
            filteredContracts=new ArrayList<>();
            for (Contract contract:contracts){
                for (Client client:selectedClientList){
                    if (contract.is_active && contract.getClient_id().equals(client.getDBB_ID())){
                        filteredContracts.add(contract);
                    }
                }

            }
        } else {
            filteredContracts=new ArrayList<>();
            for (Contract contract:contracts){
                for (Client client:selectedClientList){
                    if (!contract.is_active && contract.getClient_id().equals(client.getDBB_ID())){
                        filteredContracts.add(contract);
                    }
                }

            }
        }
    }

    public void onClientSelect(SelectEvent<Client> event) {
        selectedClientList.add(event.getObject());
    }


    public List<Client> completeAutoComplete(String query) {
        // Filter the list of suggestion objects based on the user's input
        List<Client> filteredItems = new ArrayList<>();
        for (Client item : clients) {
            if (item.getName().toLowerCase().startsWith(query.toLowerCase())) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
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

    public boolean isInActiveFilter() {
        return inActiveFilter;
    }

    public void setInActiveFilter(boolean inActiveFilter) {
        this.inActiveFilter = inActiveFilter;
    }

    public List<Contract> getFilteredContracts() {
        return filteredContracts;
    }

    public void setFilteredContracts(List<Contract> filteredContracts) {
        this.filteredContracts = filteredContracts;
    }

    public BigDecimal getYearlyFacturationAmount() {
        return yearlyFacturationAmount;
    }

    public BigDecimal getMonthlyFacturationAmount() {
        return MonthlyFacturationAmount;
    }

    public Client[] getClients() {
        return clients;
    }

    public void setClients(Client[] clients) {
        this.clients = clients;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public List<Client> getSelectedClientList() {
        return selectedClientList;
    }

    public void setSelectedClientList(List<Client> selectedClientList) {
        this.selectedClientList = selectedClientList;
    }

    public List<History> getSelectedHistory() {
        return selectedHistory;
    }
}
