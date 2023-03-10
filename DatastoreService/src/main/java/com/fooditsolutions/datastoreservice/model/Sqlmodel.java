package com.fooditsolutions.datastoreservice.model;

import com.fooditsolutions.datastoreservice.controller.Util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Sqlmodel{
    public String getInsertStatement(){
        Field[] fields = this.getClass().getDeclaredFields();

        String sqlFields = "";
        String sqlValues = "";
        try {
            for(Field f:fields){
                Object o = runGetter(f,this);
                if(o !=null) {
                    if (sqlFields != "") {
                        sqlFields += ",";
                        sqlValues += ",";
                    }
                    sqlFields += f.getName();
                    sqlValues += Util.structureSQL(o);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "INSERT INTO CONTRACT (" +
                sqlFields +
                ") VALUES ("+
                sqlValues +
                ");";
        return result ;
    }

    public static Object runGetter(Field field, Object o)
    {
        // MZ: Find the correct method
        for (Method method : o.getClass().getMethods())
        {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3)))
            {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase()))
                {
                    // MZ: Method found, run it
                    try
                    {
                        return method.invoke(o);
                    }
                    catch (IllegalAccessException e)
                    {
                        System.out.println("Could not determine method: " + method.getName());
                    }
                    catch (InvocationTargetException e)
                    {
                        System.out.println("Could not determine method: " + method.getName());
                    }

                }
            }
        }


        return null;
    }
}
