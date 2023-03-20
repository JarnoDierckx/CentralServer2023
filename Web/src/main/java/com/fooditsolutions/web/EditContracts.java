package com.fooditsolutions.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Contract;
import com.fooditsolutions.web.model.ContractDetail;
import com.google.gson.Gson;
import lombok.Setter;
import lombok.Getter;
import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.event.CellEditEvent;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class EditContracts implements Serializable {

    private Contract selectedContract;
    private Contract updatingContract;
    private ContractDetail[] selectedContractDetails;
    private ContractDetail[] updatingContractDetails;

    @PostConstruct
    public void Init() throws IOException {
        System.out.println("Edit contract");
        System.out.println(selectedContract);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            selectedContract = (Contract) session.getAttribute("contract");
            session.removeAttribute("contract");
        }
        updatingContract=new Contract();
        try {
            BeanUtils.copyProperties(updatingContract, selectedContract);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ManageContracts manageContracts=new ManageContracts();
        updatingContractDetails=manageContracts.getContractDetails(updatingContract.id);
    }

    public void updateContract() throws IOException {


        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContract);

        System.out.println("update: "+jsonString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract", jsonString);
    }

    public void UpdateContractDetails() throws IOException {
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //Converting the Object to JSONString
        String jsonString = mapper.writeValueAsString(updatingContractDetails);

        System.out.println("update: "+jsonString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crudContract/detail", jsonString);
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        System.out.println(newValue);
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
}