package com.fooditsolutions.CentralServer2023API.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    private BigDecimal DBB_ID = BigDecimal.valueOf(0);

    private Server server = new Server();
    private BigDecimal SERVER_DBB_ID= BigDecimal.valueOf(0);
    private String Name = "";
    private Date Validuntil = new Date();
    private int Modules_Index = 0;
    private ModuleId moduleid = new ModuleId();
}
