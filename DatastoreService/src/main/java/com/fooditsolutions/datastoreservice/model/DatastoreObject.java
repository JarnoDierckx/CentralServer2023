package com.fooditsolutions.datastoreservice.model;


import com.fooditsolutions.datastoreservice.emum.DatamodelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import javax.json.bind.annotation.JsonbProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DatastoreObject {
    String key = "";
    @JsonbProperty("name")
    String name="";
    @JsonbProperty("connectionstring")
    String connectionString="";

    @JsonbProperty("username")
    String userName="";

    @JsonbProperty("password")
    String Password = "";

    @JsonbProperty("datamodeltype")
    DatamodelType datamodelType = DatamodelType.DATABASE;

    public void setName(String name){
        this.name = name;
        key = DigestUtils.md5Hex(name).toUpperCase();
    }

}
