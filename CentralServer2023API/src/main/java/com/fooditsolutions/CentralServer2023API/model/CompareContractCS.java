package com.fooditsolutions.CentralServer2023API.model;

import com.fooditsolutions.CentralServer2023API.enums.ModuleCompare;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class CompareContractCS {
    BigDecimal moduleId_DBB_ID;
    ModuleCompare moduleSyncStatus;
    ModuleId moduleId;

}
