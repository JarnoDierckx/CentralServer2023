package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.Index;
import com.fooditsolutions.util.model.*;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.SortMeta;


import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@SessionScoped
public class EditContractBean implements Serializable {

    private Contract selectedContract;
    //updatingContract is the one that goes to the webpage
    private Contract updatingContract;
    private ContractDetail[] selectedContractDetails;
    //updatingContractDetails is the one that goes to the webpage
    private ContractDetail[] updatingContractDetails;
    private List<ContractDetail> updatingContractDetailsList = new ArrayList<>();
    private ModuleId[] moduleIds;
    private int counter;
    private int counterWhatToDo = 0;
    private String warningModule = "";
    private List<SortMeta> sortBy;
    private Client[] clients;
    private Index[] cpis;

    private boolean isAfterCreate;
    private int IDNewContract;
    private int quantity;


    /**
     * The function creates a session object, checks if one already exists and then retrieves the Contract object created before this class and the webpage it manages
     * where loaded. The Contract object is then put into updatingContract so that editContract.xhtml can put it into a form.
     * updatingContract's details are then also retrieved from the ManageContract class and put into updatingContractDetails before also being used in a form.
     * updatingContract can probably be received in the same way.
     * It also retrieves the stored ID if the user was redirected after creating a contract to then retrieve the contract object that was just made.
     */
    @PostConstruct
    public void Init() throws IOException {
        System.out.println("Edit contract");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            if (session.getAttribute("contract") != null) {
                selectedContract = (Contract) session.getAttribute("contract");
            } else {
                isAfterCreate = true;
                if (session.getAttribute("ID") != null) {
                    IDNewContract = Integer.parseInt((String) session.getAttribute("ID"));
                    session.removeAttribute("ID");
                    selectedContract = retrieveNewestContract();
                }
                if (session.getAttribute("quantity") != null) {
                    quantity = (int) session.getAttribute("quantity");
                    session.removeAttribute("quantity");
                }

            }
        }
        updatingContract = new Contract();
        try {
            BeanUtils.copyProperties(updatingContract, selectedContract);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ManageContractBean manageContractBean = new ManageContractBean();
        updatingContractDetails = manageContractBean.getContractDetails(updatingContract.getId(), true);
        counter = 0;
        for (ContractDetail detail : updatingContractDetails) {
            if (detail.getID() == 0) {
                counter--;
                detail.setID(counter);
                detail.setSource("CS");
                if (isAfterCreate) {
                    if (updatingContract.getStart_date() != null) {
                        detail.setPurchase_Date(updatingContract.getStart_date());
                    }
                    if (quantity != 0) {
                        detail.setAmount(quantity);
                    }
                    if (updatingContract.getIndex_start() != null) {
                        detail.setIndex_Start(updatingContract.getIndex_start());
                    }
                    if (updatingContract.getJgr() != 0) {
                        detail.setJgr(updatingContract.getJgr());
                    }
                    if (updatingContract.getInvoice_frequency() != null) {
                        detail.setRenewal(updatingContract.getInvoice_frequency());
                    }
                    detail.setWhatToDo("C");
                }
            }
        }
        updatingContractDetailsList = Arrays.asList(updatingContractDetails);
        moduleIds = retrieveModuleIds();
        sortBy = new ArrayList<>();
        clients = retrieveClients();
        cpis = retrieveIndex();

        updatingContract.setTotal_price(BigDecimal.valueOf(0));
        for (ContractDetail detail:updatingContractDetailsList){
            if (detail.getJgr_indexed() != null){
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_indexed()));
            }else if(detail.getJgr_not_indexed() != null){
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_not_indexed()));
            }
        }
        updateContract();
    }

    /**
     * Used after a new contract has been made, retrieves the newest contract stored, so it can be properly edited.
     *
     * @return the contract matching the ID passed to this class by CreateContractBean.
     */
    public Contract retrieveNewestContract() throws IOException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/" + IDNewContract);
        System.out.println("getContracts: " + response);

        byte[] jsonData = response.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Contract contract2;
        contract2 = mapper.readValue(jsonData, Contract.class);
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
     *
     * @return an array of all clients.
     */
    public Client[] retrieveClients() throws JsonProcessingException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/clients");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Client[].class);
    }

    public void updateAll() throws IOException {
        updateContract();
        UpdateContractDetails();
    }

    /**
     * Takes updatingContract and parses it into a json string
     * An api call is then made to CentralServer2023API with the json string, so it can update the original Contract.
     */
    public void updateContract() throws IOException {
        updatingContract.setTotal_price(BigDecimal.valueOf(0));
        for (ContractDetail detail:updatingContractDetailsList){
            if (detail.getJgr_indexed() != null){
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_indexed()));
            }else if(detail.getJgr_not_indexed() != null){
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_not_indexed()));
            }
        }

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContract);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract", jsonString);
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

        System.out.println("update: " + jsonString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/detail", jsonString);
        for (ContractDetail contractDetail : updatingContractDetailsList) {
            contractDetail.setWhatToDo("");
        }
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.removeAttribute("EditContractBean");
        session.setAttribute("contract", updatingContract);
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    /**
     * Sends a request for all stored software modules.
     *
     * @return an array of all modules
     */
    public ModuleId[] retrieveModuleIds() throws JsonProcessingException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/module");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ModuleId> tempModuleList = Arrays.asList(mapper.readValue(response, ModuleId[].class));
        /*ModuleId emptyModule=new ModuleId();
        emptyModule.setName("Choose a module");
        tempModuleList.add(emptyModule);*/
        ModuleId[] tempArray = tempModuleList.toArray(new ModuleId[0]);
        return tempArray;
    }

    public Index[] retrieveIndex() throws JsonProcessingException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_indexservice() + "/index");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Index[].class);
    }

    public void removeContractDetail(int id) throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/detail/" + id);
    }

    /**
     * Gets called every time a cell is edited.
     * Discerns what objects need to be updated and what objects need to be inserted into the database.
     * If the new value is a module id, a warning is given if that module is already in the list.
     * The values for the module and client dropdowns are also properly updated so that the correct values are displayed.
     * The new value is also printed onto the console.
     */
    public void onCellEdit(CellEditEvent event) {
        //newvalue and oldvalue are somehow an array now with 2 items if you edit the module field, so I need to get the first one in order for the rest of the code to work.
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue.getClass().equals(ArrayList.class)) {
            List<java.lang.Object> newValueList;
            newValueList = (List<Object>) newValue;
            newValue = newValueList.get(0);
        }
        warningModule = "";

        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        if (editedDetail != null) {
            for (ContractDetail detail : updatingContractDetailsList) {
                if (newValue.equals(detail.getModule_DBB_ID()) && editedDetail.getID() != detail.getID()) {
                    System.out.println("This contract already has this module.");
                    warningModule = " This contract already has this module.";
                }
            }
            for (ModuleId moduleId : moduleIds) {
                if (moduleId.getDbb_id().equals(newValue)) {
                    for (ContractDetail detail : updatingContractDetailsList) {
                        if (detail.getID() == editedDetail.getID()) {
                            detail.setModuleId(moduleId);
                            if (detail.getID() == 0) {
                                counter--;
                                detail.setID(counter);
                            }
                        }
                    }
                }
            }
            if (editedDetail.getID() > 0) {
                for (ContractDetail detail : updatingContractDetailsList) {
                    if (detail.getID() == editedDetail.getID()) {
                        detail.setWhatToDo("U");
                    }
                }
            } else if (editedDetail.getID() < 0) {
                for (ContractDetail detail : updatingContractDetailsList) {
                    if (detail.getID() == editedDetail.getID()) {
                        detail.setWhatToDo(detail.getWhatToDo() + "C");
                    }
                }
            }
        }

        Contract editedContract = context.getApplication().evaluateExpressionGet(context, "#{edit}", Contract.class);
        if (editedContract != null) {
            for (Client client : clients) {
                if (client.getDBB_ID().equals(newValue)) {
                    updatingContract.client = client;
                    updatingContract.client_id = client.getDBB_ID();
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
        detail.setContract_ID(updatingContract.id);
        detail.setWhatToDo("N" + counterWhatToDo);
        detail.setSource("M");
        counterWhatToDo++;
        List<ContractDetail> newList = new ArrayList<>(updatingContractDetailsList);
        newList.add(0, detail);
        updatingContractDetailsList = newList;
    }

    public void pressDelete() {
        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        if (editedDetail.getID() == 0) {
            removeRow();
        } else {
            for (ContractDetail detail : updatingContractDetailsList) {
                if (detail.getID() == editedDetail.getID()) {
                    if (detail.getWhatToDo() == null || detail.getWhatToDo().equals("")) {
                        detail.setWhatToDo("D");
                    } else {
                        detail.setWhatToDo("");
                    }
                    break;
                }
            }
        }
    }

    public void removeRow() {
        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        for (ContractDetail detail : updatingContractDetailsList) {
            if (detail.getWhatToDo().equals(editedDetail.getWhatToDo())) {
                updatingContractDetailsList.remove(detail);
                break;
            }
        }
    }

    public void updateCPILastInvoice() {
        if (updatingContract.getLast_invoice_date() != null && updatingContract.getBase_index_year() > 0) {
            updatingContract.setIndex_last_invoice(updateCPI(updatingContract.getLast_invoice_date(),updatingContract.getBase_index_year()));
        }
    }

    public void updateCPIContractDetail(int id) {
        for (ContractDetail detail: updatingContractDetailsList){
            if (detail.getID()==id){
                detail.setIndex_Start(updateCPI(detail.getPurchase_Date(),updatingContract.getBase_index_year()));
            }
        }
    }

    public BigDecimal updateCPI(Date startDate, int baseYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, -1); // subtract one month from the date
        Date updatedDate = calendar.getTime();

        DateFormat df = new SimpleDateFormat("MMMM yyyy");
        String month = df.format(updatedDate);

        for (Index index : cpis) {
            if (Objects.equals(index.getBase(), baseYear + " = 100") && index.getMonth().equalsIgnoreCase(month)) {
                return index.getCI();
            }
        }
        return null;
    }

    /**
     * sends a request with a contract id to delete the associated contract
     */
    public void deleteContract() throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/" + updatingContract.id);
    }

    public void useless() {

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