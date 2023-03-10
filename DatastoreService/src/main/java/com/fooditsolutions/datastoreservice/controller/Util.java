package com.fooditsolutions.datastoreservice.controller;

public class Util {
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
                //date needs to be structured
                result = "'" + o.toString() + "'";
                break;
        }

        return result;
    }
}
