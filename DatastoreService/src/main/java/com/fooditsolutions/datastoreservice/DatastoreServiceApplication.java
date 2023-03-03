package com.fooditsolutions.datastoreservice;

import com.fooditsolutions.datastoreservice.controller.Datastores;
import com.fooditsolutions.datastoreservice.emum.DatamodelType;
import com.fooditsolutions.datastoreservice.model.DatastoreObject;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
public class DatastoreServiceApplication  extends Application {
    public DatastoreServiceApplication(){
        System.out.println("INIT DATASTORE SERVICE");
        DatastoreObject datastoreObject = new DatastoreObject();
        datastoreObject.setName("Central Server Thunderbird");
        datastoreObject.setDatamodelType(DatamodelType.DATABASE);
        datastoreObject.setUserName("SYSDBA");
        datastoreObject.setConnectionString("jdbc:firebirdsql:localhost:/data/CENTRALSERVER.FDB");
        datastoreObject.setPassword("masterkey");
        Datastores.addDatastore(datastoreObject);
    }
}
