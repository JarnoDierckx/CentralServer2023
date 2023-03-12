package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fooditsolutions.datastoreservice.model.Sqlmodel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Table(name = "CLIENT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Client extends Sqlmodel {
    @Id
    @JsonbProperty("DBB_ID")
    BigDecimal DBB_ID = BigDecimal.valueOf(0);
    String Phone="";
    String MobilePhone="";
    String EmergancyPhone="";
    String Fax = "";
    String Email="";
    String Address = "";
    String Name = "";
    String URL="";
    String Description="";
    Timestamp DBB_CREATION_DATE;

}
