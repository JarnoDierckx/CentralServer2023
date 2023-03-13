package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.model.Sqlmodel;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "CONTRACT_DETAIL")
public class ContractDetail extends Sqlmodel {
    @Id
    private int ID;
    private int contract_ID;
    private BigDecimal module_DBB_ID;
    private Date purchase_Date;
    private int amount;
    private BigDecimal purchase_price;
    private BigDecimal index_Start;
    private String renewal;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getContract_ID() {
        return contract_ID;
    }

    public void setContract_ID(int contract_ID) {
        this.contract_ID = contract_ID;
    }

    public BigDecimal getModule_DBB_ID() {
        return module_DBB_ID;
    }

    public void setModule_DBB_ID(BigDecimal module_DBB_ID) {
        this.module_DBB_ID = module_DBB_ID;
    }

    public Date getPurchase_Date() {
        return purchase_Date;
    }

    public void setPurchase_Date(Date purchase_Date) {
        this.purchase_Date = purchase_Date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public BigDecimal getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(BigDecimal purchase_price) {
        this.purchase_price = purchase_price;
    }

    public BigDecimal getIndex_Start() {
        return index_Start;
    }

    public void setIndex_Start(BigDecimal index_Start) {
        this.index_Start = index_Start;
    }

    public String getRenewal() {
        return renewal;
    }

    public void setRenewal(String renewal) {
        this.renewal = renewal;
    }
}
