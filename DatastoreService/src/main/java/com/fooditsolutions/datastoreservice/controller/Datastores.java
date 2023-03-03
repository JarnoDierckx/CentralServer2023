package com.fooditsolutions.datastoreservice.controller;

import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
public class Datastores {
    private static List<DatastoreObject> datastoreObjects = new ArrayList<>();

    public static void addDatastore(DatastoreObject datastoreObject){
        datastoreObjects.add(datastoreObject);
    }

    public static List<DatastoreObject> getDatastores(){
        return datastoreObjects;
    }

    public static void addDatastores(List<DatastoreObject> newDatastoreObjects){
        datastoreObjects.addAll(newDatastoreObjects);
    }

    public static void deleteDatastores(List<DatastoreObject> delDatastoreObjects){
        for(DatastoreObject ds : delDatastoreObjects){
            for(DatastoreObject ds2 : datastoreObjects){
                if(ds.getKey().equals(ds2.getKey())){
                    datastoreObjects.remove(ds2);
                }
            }
        }
    }

    public static void deleteDatastore(String datastoreKey){
        List<DatastoreObject> newDatastoreObjects = new ArrayList<>();
        for(DatastoreObject ds : datastoreObjects){
            if(!ds.getKey().equals(datastoreKey)){
                newDatastoreObjects.add(ds);
            }
        }
        datastoreObjects = newDatastoreObjects;
    }
}
