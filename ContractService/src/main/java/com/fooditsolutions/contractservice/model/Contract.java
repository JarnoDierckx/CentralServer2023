package com.fooditsolutions.contractservice.model;

import java.util.Date;

public class Contract {
    private Client client;
    private int contractId;

    private int ID;
    private String contract_number;
    private int CLIENT_ID;
    private int BJR_ID;
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
