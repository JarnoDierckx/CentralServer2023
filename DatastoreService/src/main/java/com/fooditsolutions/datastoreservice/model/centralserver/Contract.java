package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.model.Sqlmodel;
import com.fooditsolutions.util.enums.ModuleSyncType;
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
    public int id;
    public String contract_number;
    @JsonbProperty("client_id")
    public BigDecimal client_id;

    public Client client;
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
    public Date next_invoice_date;
    public int jgr;
    public String comments;
    public boolean is_active;
    public String server_ID;
    public BigDecimal total_price;
    private boolean hasEmptyModule;
    private ModuleSyncType syncType;
}
