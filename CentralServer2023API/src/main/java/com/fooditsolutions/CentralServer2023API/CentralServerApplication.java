package com.fooditsolutions.CentralServer2023API;

import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;

@ApplicationPath("api")
public class CentralServerApplication extends Application {
    public CentralServerApplication(){
        try {
            PropertiesController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
