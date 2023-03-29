package com.fooditsolutions.indexservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Facts {
    @JsonProperty("facts")
    List<IndexTemp> indexTempList = new ArrayList<>();
}

