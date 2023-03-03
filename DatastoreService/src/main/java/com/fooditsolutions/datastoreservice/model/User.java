package com.fooditsolutions.datastoreservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User{
    int ID = 0;
    String Name="";
    String Password="";
}
