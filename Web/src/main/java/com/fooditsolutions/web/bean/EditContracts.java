package com.fooditsolutions.web.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.*;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.CellEditEvent;


import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Entity;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ManagedBean
@SessionScoped
public class EditContracts implements Serializable {

    private Contract selectedContract;
    //This is the one that goes to the webpage
    private Contract updatingContract;
    private ContractDetail[] selectedContractDetails;
    //this is the one that goes to the webpage
    private ContractDetail[] updatingContractDetails;
    private List<ContractDetail> updatingContractDetailsList=new ArrayList<>();
    private ModuleId[] moduleIds;
    private int counter;

    /**
     *The function creates a session object, checks if one already exists and then retrieves the Contract object created before this class and the webpage it manages
     * where loaded. The Contract object is then put into updatingContract so that editContract.xhtml can put it into a form.
     * updatingContract's details are then also retrieved from the ManageContract class and put into updatingContractDetails before also being used in a form.
     * updatingContract can probably be received in the same way.
     */
    @PostConstruct
    public void Init() throws IOException {
        System.out.println("Edit contract");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            selectedContract = (Contract) session.getAttribute("contract");
        }
        updatingContract=new Contract();
        try {
            BeanUtils.copyProperties(updatingContract, selectedContract);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ManageContracts manageContracts=new ManageContracts();
        updatingContractDetails=manageContracts.getContractDetails(updatingContract.id);
        counter=0;
        for (ContractDetail detail:updatingContractDetails){
            if (detail.getID()==0){
                counter--;
                detail.setID(counter);
            }
        }
        updatingContractDetailsList= Arrays.asList(updatingContractDetails);
        moduleIds=retrieveModuleIds();
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
    }

    public ModuleId[] retrieveModuleIds() throws JsonProcessingException {
        String response=HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/module");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(response, ModuleId[].class);
    }

    /**
     * Gets called every time a cell is edited.
     * Discerns what objects need to be updated and what objects need to be inserted into the database.
     * The new value is also printed onto the console.
     */
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        FacesContext context = FacesContext.getCurrentInstance();
        ContractDetail editedDetail = context.getApplication().evaluateExpressionGet(context, "#{detail}", ContractDetail.class);
        if (editedDetail != null){
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
        System.out.println(newValue);
    }



    public void addRow() {
        ContractDetail detail = new ContractDetail();
        counter--;
        detail.setID(counter);
        detail.setContract_ID(updatingContract.id);
        List<ContractDetail> newList = new ArrayList<>(updatingContractDetailsList);
        newList.add(detail);
        updatingContractDetailsList = newList;
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
}