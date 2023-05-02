package com.fooditsolutions.web.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class LandingPageBean implements Serializable {

    public String redirectToGeneralContracts(){
        return "generalContracts.xhtml?faces-redirect=true";
    }
}
