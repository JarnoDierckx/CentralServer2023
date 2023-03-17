package com.fooditsolutions.CentralServer2023API.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    BigDecimal id = BigDecimal.valueOf(0);
    String name="";
}
