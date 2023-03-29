package com.fooditsolutions.indexservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class IndexTemp {
    @JsonProperty("Jaar")
    String Jaar;

    @JsonProperty("Maand")
    String Maand;
    @JsonProperty("Basisjaar")
    String Basisjaar;

    @JsonProperty("Consumptieprijsindex")
    BigDecimal Consumptieprijsindex;
}
