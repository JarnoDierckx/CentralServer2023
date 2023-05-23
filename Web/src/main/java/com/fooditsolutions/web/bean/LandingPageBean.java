package com.fooditsolutions.web.bean;

import com.fooditsolutions.util.controller.HttpController;
import com.fooditsolutions.util.controller.PropertiesController;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class LandingPageBean implements Serializable {

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Cookie[] cookies = request.getCookies();
        boolean loggedin=false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().contains("LOGINCENTRALSERVER2023")) {
                    String sessionkey=cookie.getValue();
                    String response= HttpController.httpGet(PropertiesController.getProperty().getBase_url_centralserver2023api()+"/crud/"+sessionkey);
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
    }

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
    public void toLogin() throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(externalContext.getRequestContextPath() + "/index.xhtml?faces-redirect=true");
    }
}
