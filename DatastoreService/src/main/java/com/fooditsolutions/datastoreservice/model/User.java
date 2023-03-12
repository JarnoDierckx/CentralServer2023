package com.fooditsolutions.datastoreservice.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CS_USER")
public class User extends Sqlmodel{


    @JsonbProperty("id")
    @Id
    int ID = 0;
    @JsonbProperty("name")
    String Name="";
    @JsonbProperty("password")
    String Password="";
    @JsonbProperty("email")
    String email="";
}
