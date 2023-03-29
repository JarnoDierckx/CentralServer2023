package com.fooditsolutions.indexservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Index {
    int year;
    String Month;
    String Base;
    BigDecimal CI;
}
