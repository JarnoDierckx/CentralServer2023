package com.fooditsolutions.contractservice.controller;

import com.fooditsolutions.contractservice.model.Property;
import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesController {
    @Getter
    private final static Property property = new Property();
    public static void init() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "application.properties";
            inputStream = PropertiesController.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            String dataStoreId = prop.getProperty("datastore");
            property.setDatastore(dataStoreId);
        }catch (Exception ex){
            System.out.println("Exception: " + ex);
        }finally {
            inputStream.close();
        }
    }
}
