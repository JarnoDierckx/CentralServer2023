package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.model.Sqlmodel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Table(name="MODUELE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Module extends Sqlmodel {
        @Id
        private BigDecimal DBB_ID = BigDecimal.valueOf(0);
        private BigDecimal SERVER_DBB_ID= BigDecimal.valueOf(0);
        private String Name = "";
        private Date Validuntil = new Date();
        private int Modules_Index = 0;
        private boolean isTrial;





}
