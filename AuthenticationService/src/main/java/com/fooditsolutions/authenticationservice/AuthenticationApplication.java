package com.fooditsolutions.authenticationservice;

import com.fooditsolutions.authenticationservice.controller.PropertiesController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;

@ApplicationPath("authapi")
public class AuthenticationApplication extends Application {
    public AuthenticationApplication(){
        try {
            PropertiesController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
