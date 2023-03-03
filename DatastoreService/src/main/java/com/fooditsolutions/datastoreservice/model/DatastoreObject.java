package com.fooditsolutions.datastoreservice.model;


import com.fooditsolutions.datastoreservice.emum.DatamodelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatastoreObject {
    String key = "";
    String name="";
    String connectionString="";
    String userName="";
    String Password = "";
    DatamodelType datamodelType = DatamodelType.DATABASE;

    public void setName(String name){
        this.name = name;
        key = DigestUtils.md5Hex(name).toUpperCase();
    }

}
