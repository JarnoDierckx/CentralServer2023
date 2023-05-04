package com.fooditsolutions.web.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class LandingPageBean implements Serializable {

    public String redirectToGeneralContracts(){
        HttpSession session= (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        if (session.getAttribute("ManageContractBean")!=null){
            session.removeAttribute("ManageContractBean");
        }
        return "generalContracts.xhtml?faces-redirect=true";
    }
    public String redirectToLandingPage(){
        return "homePage.xhtml?faces-redirect=true";
    }
}
