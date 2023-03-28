package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.*;
import lombok.Getter;
import lombok.Setter;


import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ContractBean implements Serializable {
    private Contract newContract;
    private Client client;
    private Client[] clients;
    private Bjr bjr;
    private Bjr[] bjrs;
    private ModuleId[] modules;
    private List<ContractDetail> contractDetailsList = new ArrayList<>();

    @PostConstruct
    public void init(){
        newContract=new Contract();
        client=new Client();
        bjr=new Bjr();
        ContractDetail detail=new ContractDetail();
        contractDetailsList.add(detail);
    }

    /**
     * Retrieves all clients and bjr objects for later use.
     * @return redirects user to the createContracts page
     */
    public String PrepareCreateContract() throws JsonProcessingException {
        bjrs =retrieveBjr();
        clients=retrieveClients();
        //modules=retrieveModules();
        return "createContract.xhtml?faces-redirect=true";
    }

    public void addContractDetail(){
        ContractDetail detail=new ContractDetail();
        contractDetailsList.add(detail);
    }

    /**
     * Maps all values of newContract to a json object in a String and sends it to centralServerAPI.
     */
    public void createContract() throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(newContract);

        System.out.println("Create: "+jsonString);
        HttpController.httpPost(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract", jsonString);
        newContract=new Contract();
        contractDetailsList=new ArrayList<>();
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
     * retrieves all bjr objects
     * @return maps all values from the api response into an array of Bjr objects
     */
    public Bjr[] retrieveBjr() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/bjr");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Bjr[].class);
    }

    public ModuleId[] retrieveModules() throws JsonProcessingException{
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/module");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, ModuleId[].class);
    }


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

    public Bjr getBjr() {
        return bjr;
    }

    public void setBjr(Bjr brj) {
        this.bjr = bjr;
    }

    public Bjr[] getBjrs() {
        return bjrs;
    }

    public void setBjrs(Bjr[] bjrs) {
        this.bjrs = bjrs;
    }

    public ModuleId[] getModules() {
        return modules;
    }

    public void setModules(ModuleId[] modules) {
        this.modules = modules;
    }

    public List<ContractDetail> getContractDetailsList() {
        return contractDetailsList;
    }

    public void setContractDetailsList(List<ContractDetail> contractDetailsList) {
        this.contractDetailsList = contractDetailsList;
    }
}
