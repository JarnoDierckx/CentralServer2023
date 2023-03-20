package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.model.Sqlmodel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name="MODUELEID")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleId extends Sqlmodel {
    @Id
    BigDecimal dbb_id = new BigDecimal(0);
    String Name="";
    String Description="";

}
