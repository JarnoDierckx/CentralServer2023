package com.fooditsolutions.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class Contract {
    public int id;
    public String contract_number;
    @JsonbProperty("client_id")
    public BigDecimal client_id;
    public Client client;
    public int bjr_id;
    public Bjr bjr;
    public Date start_date;
    public Date end_date;
    public String source;
    public String invoice_frequency;
    public String index_frequency;
    public int base_index_year;
    public BigDecimal index_start;
    public BigDecimal index_last_invoice;
    public BigDecimal amount_last_invoice;
    public int last_invoice_number;
    public Date last_invoice_date;
    public Date last_invoice_period_start;
    public Date last_invoice_period_end;
    public int jgr;
    public String comments;
    public Date created;
    public Date updated;
    public boolean is_active;
    public String server_ID;

}
