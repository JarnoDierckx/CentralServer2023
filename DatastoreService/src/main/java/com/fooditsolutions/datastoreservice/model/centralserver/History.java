 package com.fooditsolutions.datastoreservice.model.centralserver;

 import com.fasterxml.jackson.annotation.JsonIgnore;
 import com.fasterxml.jackson.annotation.JsonProperty;
 import com.fooditsolutions.datastoreservice.model.Sqlmodel;
 import lombok.AllArgsConstructor;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;

 import javax.json.bind.annotation.JsonbProperty;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import java.math.BigDecimal;
 import java.sql.Timestamp;

 @Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "HISTORY")
public class History extends Sqlmodel {
    @Id
    @JsonProperty("id")
    public int id;
    @JsonProperty("attribute")
    public String ATTRIBUTE;
    @JsonProperty("attribute_id")
    public BigDecimal ATTRIBUTE_ID;
    @JsonProperty("actor")
    public String ACTOR;
    @JsonProperty("action")
    public String H_ACTION;
    @JsonProperty("description")
    public String DESCRIPTION;
     @JsonProperty("ts")
    public String TS;


}