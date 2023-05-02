package com.fooditsolutions.serverservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class Server {
    private BigDecimal DBB_ID = BigDecimal.valueOf(0);

    private String ID = "";
    private  BigDecimal CLIENT_DBB_ID = BigDecimal.valueOf(0);
}
