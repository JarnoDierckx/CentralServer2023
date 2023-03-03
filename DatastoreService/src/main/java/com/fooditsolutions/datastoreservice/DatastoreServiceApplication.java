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
        DatastoreObject datastoreObject3 = new DatastoreObject();
        datastoreObject3.setName("MATT: Central Server Thunderbird");
        datastoreObject3.setDatamodelType(DatamodelType.DATABASE);
        datastoreObject3.setUserName("SYSDBA");
        datastoreObject3.setConnectionString("jdbc:firebirdsql://localhost:3050/c:/data/CENTRALSERVER.fdb");
        datastoreObject3.setPassword("Test1234");
        Datastores.addDatastore(datastoreObject3);
    }
}
