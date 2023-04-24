package com.fooditsolutions.util.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Table(name="SERVER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    @Id
    private BigDecimal DBB_ID = BigDecimal.valueOf(0);
    private String ID = "";
    private  BigDecimal CLIENT_DBB_ID = BigDecimal.valueOf(0);

}
