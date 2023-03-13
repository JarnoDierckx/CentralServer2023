package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.model.Sqlmodel;
import lombok.Getter;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "CONTRACT")
@Getter
@Setter
public class Contract extends Sqlmodel {
    @Id
    @JsonbProperty("id")
    private int id;
    private String contract_number;
    @JsonbProperty("client_id")
    private BigDecimal client_id;

    //@JsonbProperty("bjr_id")

    private int bjr_id;
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
