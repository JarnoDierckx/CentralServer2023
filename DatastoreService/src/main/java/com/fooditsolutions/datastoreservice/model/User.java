package com.fooditsolutions.datastoreservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends Sqlmodel{

    @JsonbProperty("id")
    int ID = 0;
    @JsonbProperty("name")
    String Name="";
    @JsonbProperty("password")
    String Password="";
    @JsonbProperty("email")
    String email="";
}
