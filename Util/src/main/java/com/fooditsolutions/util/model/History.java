 package com.fooditsolutions.util.model;

 import com.fooditsolutions.util.enums.Action;
 import lombok.AllArgsConstructor;
 import lombok.Getter;
 import lombok.NoArgsConstructor;
 import lombok.Setter;

 import java.sql.Timestamp;
 import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class History {
    public int id;
    String attribute;
    long attribute_id;
    String ts;
    String actor;
    Action action;
    String description;
}