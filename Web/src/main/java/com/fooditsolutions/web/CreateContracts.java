package com.fooditsolutions.web;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Bjr;
import com.fooditsolutions.web.model.Client;
import com.fooditsolutions.web.model.Contract;

import java.io.IOException;

public class CreateContracts {
    private Contract newContract;
    private Client client;
    private Client[] clients;
    private Bjr brj;
    private Bjr[] bjrs;


    public String PrepareCreateContract(){

        return "createContract.xhtml?faces-redirect=true";
    }
    public void createContract() throws IOException {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(newContract);

        System.out.println("update: "+jsonString);
        //System.out.println(detailString);

        HttpController.httpPost(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/detail", jsonString);
    }

    public void retrieveClients(){


    }

    public void retrieveBjr(){

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

    public Bjr getBrj() {
        return brj;
    }

    public void setBrj(Bjr brj) {
        this.brj = brj;
    }

    public Bjr[] getBjrs() {
        return bjrs;
    }

    public void setBjrs(Bjr[] bjrs) {
        this.bjrs = bjrs;
    }
}
