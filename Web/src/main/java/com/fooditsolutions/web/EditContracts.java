package com.fooditsolutions.web;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;
import com.fooditsolutions.web.model.Contract;
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

    @PostConstruct
    public void Init() {
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
    }

    public void updateContract() throws IOException {
        //selectedItem.start_date= new java.util.Date(selectedItem.start_date.getTime());
        System.out.println(updatingContract.start_date);
        long time = updatingContract.start_date.getTime();


        Gson gson = new Gson();
        String contractString = gson.toJson(updatingContract);
        //String detailString=gson.toJson(details);
        System.out.println("update: " + contractString);
        //System.out.println(detailString);

        HttpController.httpPut(PropertiesController.getProperty().getBase_url_centralserver2023api() + "/crudContract", contractString);
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
}