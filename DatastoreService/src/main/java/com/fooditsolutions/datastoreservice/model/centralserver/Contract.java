package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.annotation.DatabaseTable;
import com.fooditsolutions.datastoreservice.annotation.Identifier;
import com.fooditsolutions.datastoreservice.controller.Util;
import com.fooditsolutions.datastoreservice.model.Sqlmodel;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
@Table(name = "CONTRACT")
public class Contract extends Sqlmodel {
    @Id
    private int ID;
    private String contract_number;
    private BigDecimal CLIENT_ID;
    private int BJR_ID;
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContract_number() {
        return contract_number;
    }

    public void setContract_number(String contract_number) {
        this.contract_number = contract_number;
    }

    public BigDecimal getCLIENT_ID() {
        return CLIENT_ID;
    }

    public void setCLIENT_ID(BigDecimal CLIENT_ID) {
        this.CLIENT_ID = CLIENT_ID;
    }

    public int getBJR_ID() {
        return BJR_ID;
    }

    public void setBJR_ID(int BJR_ID) {
        this.BJR_ID = BJR_ID;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getInvoice_frequency() {
        return invoice_frequency;
    }

    public void setInvoice_frequency(String invoice_frequency) {
        this.invoice_frequency = invoice_frequency;
    }

    public String getIndex_frequency() {
        return index_frequency;
    }

    public void setIndex_frequency(String index_frequency) {
        this.index_frequency = index_frequency;
    }

    public int getBase_index_year() {
        return base_index_year;
    }

    public void setBase_index_year(int base_index_year) {
        this.base_index_year = base_index_year;
    }

    public BigDecimal getIndex_start() {
        return index_start;
    }

    public void setIndex_start(BigDecimal index_start) {
        this.index_start = index_start;
    }

    public BigDecimal getIndex_last_invoice() {
        return index_last_invoice;
    }

    public void setIndex_last_invoice(BigDecimal index_last_invoice) {
        this.index_last_invoice = index_last_invoice;
    }

    public BigDecimal getAmount_last_invoice() {
        return amount_last_invoice;
    }

    public void setAmount_last_invoice(BigDecimal amount_last_invoice) {
        this.amount_last_invoice = amount_last_invoice;
    }

    public int getLast_invoice_number() {
        return last_invoice_number;
    }

    public void setLast_invoice_number(int last_invoice_number) {
        this.last_invoice_number = last_invoice_number;
    }

    public Date getLast_invoice_date() {
        return last_invoice_date;
    }

    public void setLast_invoice_date(Date last_invoice_date) {
        this.last_invoice_date = last_invoice_date;
    }

    public Date getLast_invoice_period_start() {
        return last_invoice_period_start;
    }

    public void setLast_invoice_period_start(Date last_invoice_period_start) {
        this.last_invoice_period_start = last_invoice_period_start;
    }

    public Date getLast_invoice_period_end() {
        return last_invoice_period_end;
    }

    public void setLast_invoice_period_end(Date last_invoice_period_end) {
        this.last_invoice_period_end = last_invoice_period_end;
    }

    public int getJgr() {
        return jgr;
    }

    public void setJgr(int jgr) {
        this.jgr = jgr;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }


}
