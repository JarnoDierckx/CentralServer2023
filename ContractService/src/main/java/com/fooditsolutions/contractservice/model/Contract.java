package com.fooditsolutions.contractservice.model;

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
public class Contract {
    private Client client;
    private int contractId;

    private int id;
    private String contract_number;
    private BigDecimal client_id;
    private int bjr_id;
    private Bjr bjr;
    private Date start_date;
    private Date end_date;
    private String source;
    private String invoice_frequency;
    private String index_frequency;
    private int base_index_year;
    private double index_start;
    private double index_last_invoice;
    private double amount_last_invoice;
    private int last_invoice_number;
    private Date last_invoice_date;
    private Date last_invoice_period_start;
    private Date last_invoice_period_end;
    private int jgr;
    private String comments;
    private Date created;
    private Date updated;

}
