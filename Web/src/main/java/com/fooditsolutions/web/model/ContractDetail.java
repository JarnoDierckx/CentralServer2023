package com.fooditsolutions.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContractDetail {
    private int ID;
    private int contract_ID;
    private BigDecimal module_DBB_ID;
    private ModuleId moduleId;
    private Date purchase_Date;
    private int amount;
    private BigDecimal purchase_price;
    private BigDecimal index_Start;
    private String renewal;
    private int jgr;
    private BigDecimal jgr_not_indexed;
    private BigDecimal jgr_indexed;
    private String whatToDo;
}
