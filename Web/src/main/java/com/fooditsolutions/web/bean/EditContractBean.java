package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.*;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.SortMeta;


import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean
@SessionScoped
public class EditContractBean implements Serializable {

    private Contract selectedContract;
    //This is the one that goes to the webpage
    private Contract updatingContract;
    private ContractDetail[] selectedContractDetails;
    //this is the one that goes to the webpage
    private ContractDetail[] updatingContractDetails;
    private List<ContractDetail> updatingContractDetailsList=new ArrayList<>();
    private ModuleId[] moduleIds;
    private int counter;
    private String warningModule="";
    private List<SortMeta> sortBy;
    private Client[] clients;

    private boolean isAfterCreate;
    private java.util.Date purchaseDate;
    private int quantity;
    private BigDecimal startIndex;


    /**
     *The function creates a session object, checks if one already exists and then retrieves the Contract object created before this class and the webpage it manages
     * where loaded. The Contract object is then put into updatingContract so that editContract.xhtml can put it into a form.
     * updatingContract's details are then also retrieved from the ManageContract class and put into updatingContractDetails before also being used in a form.
     * updatingContract can probably be received in the same way.
     * It also retrieves the stored values if the user was redirected to edit contracts after creating a new contract.
     */
    @PostConstruct
    public void Init() throws IOException, ServletException {
        System.out.println("Edit contract");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            if (session.getAttribute("contract")!=null){
                selectedContract = (Contract) session.getAttribute("contract");
            }else{
                selectedContract= retrieveLastContract();
                isAfterCreate=true;
                if (session.getAttribute("purchaseDate")!= null){
                    purchaseDate= (Date) session.getAttribute("purchaseDate");
                    session.removeAttribute("purchaseDate");
                }
                if (session.getAttribute("quantity")!= null){
                    quantity= (int) session.getAttribute("quantity");
                    session.removeAttribute("quantity");
                }
                if (session.getAttribute("startIndex")!= null){
                    startIndex= (BigDecimal) session.getAttribute("startIndex");
                    session.removeAttribute("startIndex");
                }
            }
        }
        updatingContract=new Contract();
        try {
            BeanUtils.copyProperties(updatingContract, selectedContract);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ManageContractBean manageContractBean =new ManageContractBean();
        updatingContractDetails= manageContractBean.getContractDetails(updatingContract.id,true);
        counter=0;
        for (ContractDetail detail:updatingContractDetails){
            if (detail.getID()==0){
                counter--;
                detail.setID(counter);
                if (isAfterCreate){
                    if (purchaseDate != null){
                        detail.setPurchase_Date(purchaseDate);
                    }
                    if (quantity != 0){
                        detail.setAmount(quantity);
                    }
                    if (startIndex != null){
                        detail.setIndex_Start(startIndex);
                    }
                    if (selectedContract.jgr != 0){
                        detail.setJgr(selectedContract.jgr);
                    }
                    detail.setWhatToDo("C");
                }
            }
        }
        updatingContractDetailsList= Arrays.asList(updatingContractDetails);
        moduleIds=retrieveModuleIds();
        sortBy = new ArrayList<>();
        clients=retrieveClients();
    }

    /**
     * Used after a new contract has been made, retrieves the newest contract stored, so it can be properly edited.
     * @return the contract object with the highest id, as this would be the newest.
     */
    public Contract retrieveLastContract() throws IOException, ServletException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract");
        System.out.println("getContracts: "+response);

        byte[] jsonData = response.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Contract[] contracts2;
        contracts2 = mapper.readValue(jsonData, Contract[].class);

        List<Contract> contractList= Arrays.asList(contracts2);

        Contract contract2=contractList.stream().max(Comparator.comparing(Contract::getId)).orElseThrow(NoSuchElementException::new);
        if (contract2.start_date != null) {
            contract2.start_date = new java.sql.Date(contract2.start_date.getTime());
        }
        if (contract2.last_invoice_date != null) {
            contract2.last_invoice_date = new java.sql.Date(contract2.last_invoice_date.getTime());
        }
        if (contract2.last_invoice_period_start != null) {
            contract2.last_invoice_period_start = new java.sql.Date(contract2.last_invoice_period_start.getTime());
        }
        if (contract2.last_invoice_period_end != null) {
            contract2.last_invoice_period_end = new java.sql.Date(contract2.last_invoice_period_end.getTime());
        }

        return contract2;
    }

    /**
     * Sends a request to retrieve all stored clients.
     * @return an array of all clients.
     */
    public Client[] retrieveClients() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/clients");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Client[].class);
    }

    public void updateAll() throws IOException{
        updateContract();
        UpdateContractDetails();
    }

    /**
     *Takes updatingContract and parses it into a json string
     * An api call is then made to CentralServer2023API with the json string, so it can update the original Contract.
     */
    public void updateContract() throws IOException {

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContract);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract", jsonString);
    }

    /**
     * Takes updatingContractDetails and parses it into a json string
     * An api call is then made to CentralServer2023API with the json string, so it can update the original contract details.
     * All "whatToDo" values are reset, so it doesn't interfere with any sequential updates.
     */
    public void UpdateContractDetails() throws IOException {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContractDetailsList);

        System.out.println("update: "+jsonString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/detail", jsonString);
        for (ContractDetail contractDetail:updatingContractDetailsList){
            contractDetail.setWhatToDo("");
        }
    }

    /**
     * Sends a request for all stored software modules.
     * @return an array of all modules
     */
    public ModuleId[] retrieveModuleIds() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/module");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, ModuleId[].class);
    }

    /**
     * Gets called every time a cell is edited.
     * Discerns what objects need to be updated and what objects need to be inserted into the database.
     * If the new value is a module id, a warning is given if that module is already in the list.
     * The values for the module and client dropdowns are also properly updated so that the correct values are displayed.
     * The new value is also printed onto the console.
     */
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        warningModule="";

        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        if (editedDetail != null){
            for (ContractDetail detail: updatingContractDetailsList){
                if (newValue.equals(detail.getModule_DBB_ID()) && editedDetail.getID() != detail.getID()){
                    System.out.println("This contract already has this module.");
                    warningModule=" This contract already has this module.";
                }
            }
            for (ModuleId moduleId: moduleIds){
                if (moduleId.getDbb_id().equals(newValue)){
                    for (ContractDetail detail: updatingContractDetailsList) {
                        if (detail.getID() == editedDetail.getID()) {
                            detail.setModuleId(moduleId);
                        }
                    }
                }
            }
            if (editedDetail.getID()>0){
                for (ContractDetail detail: updatingContractDetailsList){
                    if (detail.getID()== editedDetail.getID()){
                        detail.setWhatToDo("U");
                    }
                }
            } else if(editedDetail.getID()<0){
                for (ContractDetail detail: updatingContractDetailsList){
                    if (detail.getID()== editedDetail.getID()){
                        detail.setWhatToDo("C");
                    }
                }
            }
        }

        Contract editedContract= context.getApplication().evaluateExpressionGet(context, "#{edit}", Contract.class);
        if (editedContract != null){
            for (Client client: clients){
                if (client.getDBB_ID().equals(newValue)){
                    updatingContract.client=client;
                    updatingContract.client_id=client.getDBB_ID();
                }
            }
        }
        System.out.println(newValue);
    }

    /**
     * Adds a new detail object to the list used in the datatable.
     */
    public void addRow() {
        ContractDetail detail = new ContractDetail();
        counter--;
        detail.setID(counter);
        detail.setContract_ID(updatingContract.id);
        List<ContractDetail> newList = new ArrayList<>(updatingContractDetailsList);
        newList.add(detail);
        updatingContractDetailsList = newList;
    }

    /**
     *sends a request with a contract id to delete the associated contract
     */
    public void deleteContract() throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/"+updatingContract.id);
    }

    public void useless(){

    }

    public Contract getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(Contract selectedContract) {
        this.selectedContract = selectedContract;
    }

    public Contract getUpdatingContract() {
        return updatingContract;
    }

    public void setUpdatingContract(Contract updatingContract) {
        this.updatingContract = updatingContract;
    }

    public ContractDetail[] getSelectedContractDetails() {
        return selectedContractDetails;
    }

    public void setSelectedContractDetails(ContractDetail[] selectedContractDetails) {
        this.selectedContractDetails = selectedContractDetails;
    }

    public ContractDetail[] getUpdatingContractDetails() {
        return updatingContractDetails;
    }

    public void setUpdatingContractDetails(ContractDetail[] updatingContractDetails) {
        this.updatingContractDetails = updatingContractDetails;
    }

    public List<ContractDetail> getUpdatingContractDetailsList() {
        return updatingContractDetailsList;
    }

    public void setUpdatingContractDetailsList(List<ContractDetail> updatingContractDetailsList) {
        this.updatingContractDetailsList = updatingContractDetailsList;
    }

    public ModuleId[] getModuleIds() {
        return moduleIds;
    }

    public void setModuleIds(ModuleId[] moduleIds) {
        this.moduleIds = moduleIds;
    }

    public String getWarningModule() {
        return warningModule;
    }

    public void setWarningModule(String warningModule) {
        this.warningModule = warningModule;
    }

    public List<SortMeta> getSortBy() {
        return sortBy;
    }

    public Client[] getClients() {
        return clients;
    }

    public void setClients(Client[] clients) {
        this.clients = clients;
    }
}