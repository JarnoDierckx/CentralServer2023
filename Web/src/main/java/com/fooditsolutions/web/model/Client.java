package com.fooditsolutions.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @JsonbProperty("DBB_ID")
    BigDecimal DBB_ID = BigDecimal.valueOf(0);
    String name="";
}
