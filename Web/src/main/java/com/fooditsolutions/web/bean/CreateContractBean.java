package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.*;
import org.primefaces.event.CellEditEvent;


import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@SessionScoped
public class CreateContractBean implements Serializable {
    private Contract newContract;
    private Client client;
    private Client[] clients;
    private Bjr bjr;
    private Bjr[] bjrs;
    private ModuleId[] modules;
    private Index[] cpis;
    private BigDecimal server_id;
    private Server[] servers;

    @PostConstruct
    public void init() throws JsonProcessingException {
        newContract=new Contract();
        client=new Client();
        bjr=new Bjr();
        PrepareCreateContract();
    }

    /**
     * Retrieves all clients and bjr objects for later use.
     * @return redirects user to the createContracts page
     */
    public String PrepareCreateContract() throws JsonProcessingException {
        bjrs =retrieveBjr();
        clients=retrieveClients();
        cpis=retrieveIndex();
        servers=retrieveServers();
        return "createContract.xhtml?faces-redirect=true";
    }

    /**
     * Maps all values of newContract to a json object in a String and sends it to centralServerAPI.
     */
    public void createContract() throws IOException, ServletException {
        if (!newContract.source.equals("CS")){
            newContract.source="M";
        }
        newContract.is_active=true;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(newContract);

        System.out.println("Create: "+jsonString);
        HttpController.httpPost(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract", jsonString);
        newContract=new Contract();
        server_id= BigDecimal.valueOf(0);
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

    public Index[] retrieveIndex() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_indexservice()+"/index");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Index[].class);
    }

    public Server[] retrieveServers() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/server");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, Server[].class);
    }

    public void updateCPI() throws IOException, ScriptException, NoSuchMethodException {
        if (newContract.start_date != null && newContract.base_index_year > 0){
            Date startDate = newContract.start_date;
            DateFormat df = new SimpleDateFormat("MMMM yyyy");
            String month =df.format(startDate);
            for (Index index:cpis){
                if (Objects.equals(index.getBase(), newContract.getBase_index_year() + " = 100") && index.getMonth().equals(month)){
                    newContract.index_start=index.getCI();
                    ScriptEngineManager manager=new ScriptEngineManager();
                    ScriptEngine engine=manager.getEngineByName("javascript");
                    engine.eval(Files.newBufferedReader(Paths.get("C:\\Users\\reports\\IdeaProjects\\CentralServer2023\\Web\\src\\main\\java\\com\\fooditsolutions\\web\\scripts\\Jsfunctions.js"), StandardCharsets.UTF_8));
                    Invocable inv = (Invocable) engine;
                    inv.invokeFunction("updateIndexStart", newContract.index_start);
                }
            }
        }
    }

    public void updateClient(){
        for (Server server:servers){
            if (server.getDBB_ID().equals(server_id)){
                newContract.client_id=server.getCLIENT_DBB_ID();
                newContract.source="CS";
                for (Client client2:clients){
                    if (server.getCLIENT_DBB_ID().equals(client2.getDBB_ID())){
                        newContract.client=client2;
                    }
                }
            }
        }
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

    public BigDecimal getServer_id() {
        return server_id;
    }

    public void setServer_id(BigDecimal server_id) {
        this.server_id = server_id;
    }
}
