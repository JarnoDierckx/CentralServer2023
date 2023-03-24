package com.fooditsolutions.util.controller;

import com.fooditsolutions.util.model.Property;
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

            property.setDatastore(prop.getProperty("datastore"));
            property.setBase_url_datastoreservice(prop.getProperty("BASE_URL_DATASTORESERVICE"));
            property.setBase_url_contractservice(prop.getProperty("BASE_URL_CONTRACTSERVICE"));
            property.setBase_url_centralserver2023api(prop.getProperty("BASE_URL_CENTRALSERVER2023API"));
            property.setAse_url_moduleservice(prop.getProperty("BASE_URL_MODULESERVICE"));

        }catch (Exception ex){
            System.out.println("Exception: " + ex);
        }finally {
            inputStream.close();
        }
    }
}
