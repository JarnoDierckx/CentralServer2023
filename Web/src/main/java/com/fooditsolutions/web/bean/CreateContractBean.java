package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.util.model.*;


import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
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
public class CreateContractBean implements Serializable {
    private Contract newContract;
    private Client client;
    private Client[] clients;
    private ModuleId[] modules;
    private Index[] cpis;
    private BigDecimal server_id;
    private Server[] servers;
    private Contract[] allContracts;
    private String contract_numberWarning="";
    private String server_IDWarning="";
    private boolean isDisabled=false;
    private String source;
    private int quantity;

    @PostConstruct
    public void init() throws JsonProcessingException {
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
        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        allContracts= (Contract[]) session.getAttribute("allContracts");

        newContract=new Contract();
        client=new Client();

        //use variables from generalContracts to fill fields
        PrepareCreateContract();
        if (session.getAttribute("serverID")!=null) {
            String serverID= (String) session.getAttribute("serverID");
            session.removeAttribute("serverID");
            newContract.setServer_ID(serverID);
            updateClient();
            newContract.setContract_number(newContract.getServer_ID()+"-IT");
        }
    }
    public void toLogin() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + "/index.xhtml?faces-redirect=true");
    }


    public void PrepareCreateContract() throws JsonProcessingException {
        clients=retrieveClients();
        cpis=retrieveIndex();
        servers=retrieveServers();
    }

    /**
     * Maps all values of newContract to a json object in a String and sends it to centralServerAPI.
     * quantity and ID stored in the session. ID is stored so the contract can be retrieved in the edit screen and quantity is stored to be used as a base value for the details.
     * The user is then redirected to the edit contract page
     */
    public String createContract() throws IOException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String name = "";

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().contains("LOGINCENTRALSERVER2023")) {
                    name=cookie.getName();
                    name=name.substring(22);
                    break;
                }
            }
        }
        //set source to manual if there is no server ID
        if (newContract.getServer_ID() == null){
            newContract.source="M";
        }

        newContract.is_active=true;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(newContract);

        System.out.println("Create: "+jsonString);
        String ID =HttpController.httpPost(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/"+name, jsonString);

        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("EditContractBean")!=null){
            session.removeAttribute("EditContractBean");
        }
        if (session.getAttribute("contract") != null) {
            session.removeAttribute("contract");
        }
        session.setAttribute("ID",ID);
        session.setAttribute("quantity", quantity);

        newContract=new Contract();
        server_id= BigDecimal.valueOf(0);
        quantity=0;

        return "editContract.xhtml?faces-redirect=true";
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
     * retrieves all consumer price index values
     */
    public Index[] retrieveIndex() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_indexservice()+"/index");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Index[].class);
    }

    /**
     * retrieves all stored server objects
     */
    public Server[] retrieveServers() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/server");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Server[].class);
    }

    /**
     * Checks if both the start date and the index base year are filled in so it can autofill the starting index value based on those values.
     */
    public void updateCPI(){
        if (newContract.start_date != null && newContract.base_index_year > 0){
            Date startDate = newContract.start_date;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.MONTH, -1); // subtract one month from the date
            Date updatedDate = calendar.getTime();

            DateFormat df = new SimpleDateFormat("MMMM yyyy");
            String month = df.format(updatedDate);

            for (Index index : cpis){
                if (Objects.equals(index.getBase(), newContract.getBase_index_year() + " = 100") && index.getMonth().equalsIgnoreCase(month)){
                    newContract.index_start = index.getCI();
                }
            }
        }
    }


    /**
     * Autofills the client based on the server id, and marks the contract as a central server contract.
     */
    public void updateClient(){
        newContract.setContract_number(newContract.getServer_ID()+"-IT");
        for (Server server:servers){
            if (server.getID().equals(newContract.getServer_ID())){
                newContract.client_id=server.getCLIENT_DBB_ID();
                for (Client client2:clients){
                    if (server.getCLIENT_DBB_ID().equals(client2.getDBB_ID())){
                        newContract.client=client2;
                    }
                }
            }
        }
        for (Contract contract: allContracts){
            if (contract.getServer_ID() !=null && contract.getServer_ID().equals(newContract.getServer_ID())){
                server_IDWarning="There is already a contract with this server";
                break;
            }else {
                server_IDWarning="";
            }
        }
    }

    /**
     * checks if the entered contract number is unique or not.
     */
    public void checkContract_NumberUnique(){
        for (Contract contract: allContracts){
            if (contract.getContract_number().equals(newContract.getContract_number())){
                contract_numberWarning="This contract number already exists";
                isDisabled=true;
                break;
            }else {
                contract_numberWarning="";
                isDisabled=false;
            }
        }
    }

    /**
     * mobile devices are monthly, the rest are yearly
     */
    public void changeSource(){
        if (newContract.source != null && newContract.source.equals("MOB")){
            newContract.setInvoice_frequency("M");
            newContract.setIndex_frequency("M");
        } else if(newContract.source != null && newContract.source.equals("CS")){
            newContract.setInvoice_frequency("J");
            newContract.setIndex_frequency("J");
        }
    }

    /**
     * fields without ajax lose their values when other fields are updated, so just giving them an empty function prevents that.
     */
    public void placebo(){}


    public Contract getNewContract() {
        return newContract;
    }

    public void setNewContract(Contract newContract) {
        this.newContract = newContract;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client[] getClients() {
        return clients;
    }

    public void setClients(Client[] clients) {
        this.clients = clients;
    }

    public ModuleId[] getModules() {
        return modules;
    }

    public void setModules(ModuleId[] modules) {
        this.modules = modules;
    }

    public BigDecimal getServer_id() {
        return server_id;
    }

    public void setServer_id(BigDecimal server_id) {
        this.server_id = server_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getContract_numberWarning() {
        return contract_numberWarning;
    }

    public String getServer_IDWarning() {
        return server_IDWarning;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
