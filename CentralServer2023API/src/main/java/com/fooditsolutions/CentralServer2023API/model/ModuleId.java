package com.fooditsolutions.CentralServer2023API.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModuleId {

    BigDecimal dbb_id = new BigDecimal(0);
    String name="";
    String description="";

}
