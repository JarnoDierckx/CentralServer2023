package com.fooditsolutions.moduleservice;

import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
@ApplicationPath("/api")
public class ModuleApplication extends Application {
    public ModuleApplication(){
        try {
            PropertiesController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
