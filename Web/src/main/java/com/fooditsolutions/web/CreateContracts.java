package com.fooditsolutions.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Bjr;
import com.fooditsolutions.web.model.Client;
import com.fooditsolutions.web.model.Contract;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

@ManagedBean
@SessionScoped
public class CreateContracts implements Serializable {
    private Contract newContract;
    private Client client;
    private Client[] clients;
    private Bjr bjr;
    private Bjr[] bjrs;

    @PostConstruct
    public void init(){
        newContract=new Contract();
        client=new Client();
        bjr=new Bjr();
    }


    public String PrepareCreateContract() throws JsonProcessingException {
        bjrs =retrieveBjr();
        clients=retrieveClients();
        return "createContract.xhtml?faces-redirect=true";
    }
    public void createContract() throws IOException {
        newContract.bjr_id=bjr.getId();
        newContract.client_id=client.getDBB_ID();
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(newContract);

        System.out.println("Create: "+jsonString);
        //System.out.println(detailString);

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/detail", jsonString);
    }

    public Client[] retrieveClients() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/clients");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Client[].class);
    }

    public Bjr[] retrieveBjr() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/bjr");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Bjr[].class);
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
}
