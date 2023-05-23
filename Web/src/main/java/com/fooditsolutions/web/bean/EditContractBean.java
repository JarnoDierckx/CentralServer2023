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
import javax.servlet.http.Cookie;
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
    private ContractDetail[] updatingContractDetails;
    private List<ContractDetail> updatingContractDetailsList = new ArrayList<>();
    private List<ContractDetail> filteredDetails = new ArrayList<>();
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
    private boolean inActiveFilter = false;
    private String userName;
    private String delete;


    /**
     * The function creates a session object, checks if one already exists and then retrieves the Contract object created before this class and the webpage it manages
     * where loaded. The Contract object is then put into updatingContract so that editContract.xhtml can put it into a form.
     * updatingContract's details are then also retrieved from the ManageContract class and put into updatingContractDetails before also being used in a form.
     * It also retrieves the stored ID if the user was redirected after creating a contract to then retrieve the contract object that was just made.
     */
    @PostConstruct
    public void Init() throws IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Cookie[] cookies = request.getCookies();
        boolean loggedin=false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().contains("LOGINCENTRALSERVER2023")) {
                    String sessionkey=cookie.getValue();
                    String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/"+sessionkey);
                    if (Boolean.getBoolean(response)){
                        loggedin=true;
                    }
                }
            }
            if (!loggedin){
                try {
                    toLogin();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Edit contract");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        //checks for existing session and retrieves contract
        //if there is no contract stored then it takes the ID and quantity and removes them from the session.
        if (session != null) {
            if (session.getAttribute("contract") != null) {
                selectedContract = (Contract) session.getAttribute("contract");
                session.removeAttribute("contract");
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
        //get the details for this contract
        updatingContractDetails = manageContractBean.getContractDetails(updatingContract.getId(), true);
        counter = 0;
        //looks for modules from CS, and gives them a negative ID.
        //if the user was redirected to the edit page after creating a contract, the modules are given basic values from when the contract was created.
        for (ContractDetail detail : updatingContractDetails) {
            if (detail.getID() == 0) {
                counter--;
                detail.setID(counter);
                detail.setSource("CS");
                if (isAfterCreate) {
                    if (updatingContract.getStart_date() != null) {
                        detail.setPurchase_Date(updatingContract.getStart_date());
                    }
                    if (updatingContract.getSource().equals("CS")){
                        if (quantity != 0) {
                            detail.setAmount(quantity);
                        }
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
            if (detail.is_active()) {
                filteredDetails.add(detail);
            }
        }
        //retrieve all necessary data
        updatingContractDetailsList = Arrays.asList(updatingContractDetails);
        moduleIds = retrieveModuleIds();
        sortBy = new ArrayList<>();
        clients = retrieveClients();
        cpis = retrieveIndex();

        updatingContract.setTotal_price(BigDecimal.valueOf(0));
        for (ContractDetail detail : updatingContractDetailsList) {
            if (detail.getJgr_indexed() != null && detail.is_active()) {
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_indexed()));
            } else if (detail.getJgr_not_indexed() != null && detail.is_active()) {
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_not_indexed()));
            }
        }

        request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        userName = "";

        cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().contains("LOGINCENTRALSERVER2023")) {
                    userName = cookie.getName();
                    userName = userName.substring(22);
                    break;
                }
            }
        }
        updateContract(userName);
        if (isAfterCreate) {
            updateAll();
        }
    }

    public void toLogin() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + "/index.xhtml?faces-redirect=true");
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
        if (contract2.next_invoice_date != null) {
            contract2.next_invoice_date = new java.sql.Date(contract2.next_invoice_date.getTime());
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
        updateContract(userName);
        UpdateContractDetails(userName);
    }

    /**
     * Takes updatingContract and parses it into a json string after updating the total price.
     * An api call is then made to CentralServer2023API with the json string, so it can update the original Contract.
     */
    public void updateContract(String name) throws IOException {
        updatingContract.setTotal_price(BigDecimal.valueOf(0));
        for (ContractDetail detail : updatingContractDetailsList) {
            if (detail.getJgr_indexed() != null) {
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_indexed()));
            } else if (detail.getJgr_not_indexed() != null) {
                updatingContract.setTotal_price(updatingContract.getTotal_price().add(detail.getJgr_not_indexed()));
            }
        }

        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContract);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/" + name, jsonString);
    }

    /**
     * Takes updatingContractDetails and parses it into a json string
     * An api call is then made to CentralServer2023API with the json string, so it can update the original contract details.
     * All "whatToDo" values are reset, so it doesn't interfere with any sequential updates.
     */
    public void UpdateContractDetails(String name) throws IOException {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContractDetailsList);

        System.out.println("update: " + jsonString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/detail/" + name, jsonString);
        for (ContractDetail contractDetail : updatingContractDetailsList) {
            contractDetail.setWhatToDo("");
        }
        ManageContractBean manageContractBean = new ManageContractBean();
        //get the details for this contract
        updatingContractDetails = manageContractBean.getContractDetails(updatingContract.getId(), true);
        updatingContractDetailsList = Arrays.asList(updatingContractDetails);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.removeAttribute("EditContractBean");
        session.setAttribute("contract", updatingContract);

        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
    }

    /**
     * Sends a request for all stored software modules.
     *
     * @return an array of all modules.
     */
    public ModuleId[] retrieveModuleIds() throws JsonProcessingException {
        String response = HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/module");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<ModuleId> tempModuleList = Arrays.asList(mapper.readValue(response, ModuleId[].class));
        ModuleId[] tempArray = tempModuleList.toArray(new ModuleId[0]);
        return tempArray;
    }

    /**
     * retrieves all index values.
     * @return an array of index values.
     */
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
        //newvalue and oldvalue are somehow an array now with 2/3 items, so i need to get whichever has an actual value in it.
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        if (newValue.getClass().equals(ArrayList.class)) {
            List<java.lang.Object> newValueList;
            newValueList = (List<Object>) newValue;
            for (Object o : newValueList) {
                if (o != null) {
                    newValue = o;
                    break;
                }
            }
        }
        warningModule = "";

        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        if (editedDetail != null) {
            if (!editedDetail.isHasFreeLine()){
                for (ContractDetail detail : updatingContractDetailsList) {
                    if (newValue.equals(detail.getModule_DBB_ID()) && editedDetail.getID() != detail.getID()) {
                        //if a module is already in use, add a warning.
                        System.out.println("This contract already has this module.");
                        warningModule = " This contract already has this module.";
                    }
                }
                for (ModuleId moduleId : moduleIds) {
                    if (moduleId.getDbb_id().equals(newValue)) {
                        for (ContractDetail detail : updatingContractDetailsList) {
                            if (detail.getID() == editedDetail.getID()) {
                                detail.setModuleId(moduleId);
                                detail.setModule_DBB_ID(editedDetail.getModule_DBB_ID());
                                if (detail.getID() == 0) {
                                    //give a negative id to details not already in the database when they are edited
                                    counter--;
                                    detail.setID(counter);
                                }
                            }
                        }
                    }
                }
            }else {
                for (ContractDetail detail : updatingContractDetailsList) {
                    if (detail.getID() == editedDetail.getID()) {
                        if (detail.getID() == 0) {
                            //give a negative id to details not already in the database when they are edited
                            counter--;
                            detail.setID(counter);
                        }
                    }
                }
            }

            if (editedDetail.getID() > 0) {
                for (ContractDetail detail : updatingContractDetailsList) {
                    if (detail.getID() == editedDetail.getID()) {
                        if (detail.getWhatToDo() != null && detail.getWhatToDo().equals("D")){
                            break;
                        }else {
                            // 'U' means it has to be updated when passed to the contractservice
                            detail.setWhatToDo("U");
                        }
                    }
                }
            } else if (editedDetail.getID() < 0) {
                for (ContractDetail detail : updatingContractDetailsList) {
                    if (detail.getID() == editedDetail.getID() && (detail.getWhatToDo()==null || !detail.getWhatToDo().contains("C"))) {
                        // 'C' means it has to be inserted into the DB when passed to the contractservice
                        detail.setWhatToDo(detail.getWhatToDo() + "C");
                    }
                }
            }
        }

        Contract editedContract = context.getApplication().evaluateExpressionGet(context, "#{edit}", Contract.class);
        if (editedContract != null) {
            for (Client client : clients) {
                if (client.getDBB_ID().equals(newValue)) {
                    //When a module is chosen for a new detail line, add the correct client and client ID
                    updatingContract.client = client;
                    updatingContract.client_id = client.getDBB_ID();
                }
            }
        }
        updateActiveFilterDetails();
        System.out.println(newValue);
    }

    /**
     * filter between active and inactive details
     */
    public void updateActiveFilterDetails() {
        if (!inActiveFilter) {
            filteredDetails = new ArrayList<>();
            for (ContractDetail contractDetail : updatingContractDetailsList) {
                if (contractDetail.is_active()) {
                    filteredDetails.add(contractDetail);
                }

            }
        } else {
            filteredDetails = new ArrayList<>();
            for (ContractDetail contractDetail : updatingContractDetailsList) {
                if (!contractDetail.is_active()) {
                    filteredDetails.add(contractDetail);

                }
            }
        }
    }

    /**
     * Adds a new detail object wth a module to the list used in the datatable.
     */
    public void addRow() {
        ContractDetail detail = new ContractDetail();
        detail.setContract_ID(updatingContract.id);
        detail.setWhatToDo("N" + counterWhatToDo);
        detail.setSource("M");
        detail.set_active(true);
        counterWhatToDo++;
        List<ContractDetail> newList = new ArrayList<>(updatingContractDetailsList);
        newList.add(0, detail);
        updatingContractDetailsList = newList;
        updateActiveFilterDetails();
    }

    /**
     * Adds a new detail object wth a free line to the list used in the datatable.
     */
    public void addRowFreeLine() {
        ContractDetail detail = new ContractDetail();
        detail.setHasFreeLine(true);
        detail.setContract_ID(updatingContract.id);
        detail.setWhatToDo("N" + counterWhatToDo);
        detail.setSource("M");
        detail.set_active(true);
        counterWhatToDo++;
        List<ContractDetail> newList = new ArrayList<>(updatingContractDetailsList);
        newList.add(0, detail);
        updatingContractDetailsList = newList;
        updateActiveFilterDetails();
    }


    /**
     * if a user presses on the delete button next to a module and it is an empty detail, the row is removed.
     * otherwise the user has the chance to undo their decision. Rows don't get removed until after the contract has been saved.
     */
    public void pressDelete() {
        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        if (editedDetail.getID() == 0) {
            removeRow();
        } else {
            for (ContractDetail detail : updatingContractDetailsList) {
                if (detail.getID() == editedDetail.getID()) {
                    if (detail.getWhatToDo() == null || detail.getWhatToDo().equals("")) {
                        // 'D' means it has to be deleted when passed to the contractService
                        detail.setWhatToDo("D");
                    } else {
                        detail.setWhatToDo("");
                    }
                    break;
                }
            }
        }
    }

    /**
     * removes the detail from the list of details
     */
    public void removeRow() {
        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        for (ContractDetail detail : updatingContractDetailsList) {
            if (detail.getWhatToDo()!=null && detail.getWhatToDo().equals(editedDetail.getWhatToDo())) {
                updatingContractDetailsList.remove(detail);
                break;
            }
        }
    }

    /**
     * updates the value of 'Index last invoice' based on the last invoice date and base year index.
     */
    public void updateCPILastInvoice() {
        if (updatingContract.getLast_invoice_date() != null && updatingContract.getBase_index_year() > 0) {
            updatingContract.setIndex_last_invoice(updateCPI(updatingContract.getLast_invoice_date(), updatingContract.getBase_index_year()));
        }
    }

    /**
     * updates the starting index value of a specific detail object.
     */
    public void updateCPIContractDetail(int id) {
        for (ContractDetail detail : updatingContractDetailsList) {
            if (detail.getID() == id) {
                detail.setIndex_Start(updateCPI(detail.getPurchase_Date(), updatingContract.getBase_index_year()));
            }
        }
    }

    /**
     * gives a cpi based on the values given to the function.
     */
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
    public String deleteContract() throws IOException {
        HttpController.httpDelete(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract/" + userName + "/" + updatingContract.id);
        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("ManageContractBean")!=null){
            session.removeAttribute("ManageContractBean");
        }
        return "generalContracts.xhtml?faces-redirect=true";
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

    public boolean isInActiveFilter() {
        return inActiveFilter;
    }

    public void setInActiveFilter(boolean inActiveFilter) {
        this.inActiveFilter = inActiveFilter;
    }

    public List<ContractDetail> getFilteredDetails() {
        return filteredDetails;
    }

    public void setFilteredDetails(List<ContractDetail> filteredDetails) {
        this.filteredDetails = filteredDetails;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public Index[] getCpis() {
        return cpis;
    }

    public void setCpis(Index[] cpis) {
        this.cpis = cpis;
    }
}