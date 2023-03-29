package com.fooditsolutions.indexservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class IndexTemp {
    String Jaar;
    String Maand;
    String Basisjaar;
    BigDecimal Consumptieprijsindex;
    BigDecimal Inflatie;
    BigDecimal Gezondheidsindex;
    BigDecimal Afgevlakte ;
    /*BigDecimal Index_zonder_petroleum_producten;
    BigDecimal Index_zonder_energetische_producten;*/
}
