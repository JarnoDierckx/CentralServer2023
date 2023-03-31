package com.fooditsolutions.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Server {
        private BigDecimal DBB_ID = BigDecimal.valueOf(0);
        private String ID = "";
        private  BigDecimal CLIENT_DBB_ID = BigDecimal.valueOf(0);

}

