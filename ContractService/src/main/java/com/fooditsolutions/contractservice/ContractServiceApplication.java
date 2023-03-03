package com.fooditsolutions.contractservice;

import com.fooditsolutions.contractservice.controller.PropertiesController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;

@ApplicationPath("/api")
public class ContractServiceApplication extends Application {
    public ContractServiceApplication(){
        try {
            PropertiesController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
