package com.fooditsolutions.datastoreservice.model.centralserver;

import com.fooditsolutions.datastoreservice.model.Sqlmodel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "BJR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bjr extends Sqlmodel {
    @Id
    private int id=0;
    private String name;
}
