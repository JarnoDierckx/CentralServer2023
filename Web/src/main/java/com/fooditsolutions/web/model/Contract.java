package com.fooditsolutions.web.model;

import lombok.Getter;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class Contract {
    private int id;
    private String contract_number;
    @JsonbProperty("client_id")
    private BigDecimal client_id;
    private Client client;
    private int bjr_id;
    private Bjr bjr;
    private Date start_date;
    private Date end_date;
    private String source;
    private String invoice_frequency;
    private String index_frequency;
    private int base_index_year;
    private BigDecimal index_start;
    private BigDecimal index_last_invoice;
    private BigDecimal amount_last_invoice;
    private int last_invoice_number;
    private Date last_invoice_date;
    private Date last_invoice_period_start;
    private Date last_invoice_period_end;
    private int jgr;
    private String comments;
    private Date created;
    private Date updated;


}
