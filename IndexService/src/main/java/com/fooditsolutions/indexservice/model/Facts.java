package com.fooditsolutions.indexservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Facts {
    List<IndexTemp> indexTempList = new ArrayList<>();
}

