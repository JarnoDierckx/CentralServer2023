package com.fooditsolutions.datastoreservice.controller;

import java.text.SimpleDateFormat;

public class Util {

    /**
     * Generates a structured SQL representation of the given object.
     * @param o the object to be converted to SQL structure.
     * @return a string representing the structured SQL value of the object.
     */
    public static String structureSQL(Object o){
        String result = "";
        String className = o.getClass().getName();
        switch (className){
            case"java.lang.Integer":
                result = o.toString();
                break;
            case "java.lang.String":
                result = "'" + o.toString() + "'";
                break;
            case "java.math.BigDecimal":
                result = o.toString();
                break;
            case"java.sql.Date":
            case"java.util.Date":
                //date needs to be structured
                SimpleDateFormat sdf = new SimpleDateFormat(
                        "dd.MM.yyyy");
                result = "'" + sdf.format(o) + "'";
                break;
        }

        return result;
    }


}
