package com.fooditsolutions.datastoreservice.controller;

import com.fooditsolutions.datastoreservice.model.DatastoreObject;

import java.util.ArrayList;
import java.util.List;


public class Datastores {
    private final static List<DatastoreObject> datastoreObjects = new ArrayList<>();

    public static void addDatastore(DatastoreObject datastoreObject){
        datastoreObjects.add(datastoreObject);
    }

    public static List<DatastoreObject> getDatastores(){
        return datastoreObjects;
    }
}
