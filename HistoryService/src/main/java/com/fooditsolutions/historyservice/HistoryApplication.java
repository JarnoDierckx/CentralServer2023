package com.fooditsolutions.historyservice;

import com.fooditsolutions.util.controller.PropertiesController;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;

@ApplicationPath("/api")
public class HistoryApplication  extends Application {
    public HistoryApplication(){
        try {
            PropertiesController.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}