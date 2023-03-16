package com.fooditsolutions.datastoreservice.model;


import com.fooditsolutions.datastoreservice.controller.Util;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Sqlmodel{
    public String getInsertStatement(){
        Table classAnnotation = this.getClass().getAnnotation(Table.class);

        Field[] fields = this.getClass().getDeclaredFields();

        String sqlFields = "";
        String sqlValues = "";
        try {
            for(Field f:fields){
                Annotation annotation = f.getAnnotation(Id.class);
                if(!(annotation instanceof Id)) {
                    Object o = runGetter(f, this);
                    if (o != null) {
                        if (sqlFields != "") {
                            sqlFields += ",";
                            sqlValues += ",";
                        }
                        sqlFields += f.getName();
                        sqlValues += Util.structureSQL(o);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "INSERT INTO "+classAnnotation.name()+" (" +
                sqlFields +
                ") VALUES ("+
                sqlValues +
                ");";
        return result ;
    }

    public String getDeleteStatement() {
        Table classAnnotation = this.getClass().getAnnotation(Table.class);

        Field[] fields = this.getClass().getDeclaredFields();


        String sqlWhere = "";
        try {
            for(Field f:fields){
                Annotation annotation = f.getAnnotation(Id.class);
                if((annotation instanceof Id)) {
                    Object o = runGetter(f, this);
                    if (o != null) {

                        sqlWhere += f.getName() + "=" + Util.structureSQL(o);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "DELETE FROM "+classAnnotation.name()+" WHERE "+
                sqlWhere +";";
        return result ;
    }

    public String getUpdateStatement() {
        Table classAnnotation = this.getClass().getAnnotation(Table.class);

        Field[] fields = this.getClass().getDeclaredFields();

        String sqlSet = "";
        String sqlWhere = "";
        try {
            for(Field f:fields){
                Annotation annotation = f.getAnnotation(Id.class);
                if((annotation instanceof Id)) {
                    Object o = runGetter(f, this);
                    if (o != null && !Objects.equals(o,0)) {
                        sqlWhere += f.getName() + "=" + Util.structureSQL(o);
                    }
                } else {
                    Object o = runGetter(f, this);
                    if (o != null && !Objects.equals(o,0) && !Objects.equals(o,0.00)) {
                        if (sqlSet != "") {
                            sqlSet += ",";

                        }
                        sqlSet += f.getName() +"="+ Util.structureSQL(o);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "UPDATE "+classAnnotation.name() +
                " SET " +  sqlSet +
                " WHERE "+ sqlWhere +";";
        return result ;
    }

    private static Object runGetter(Field field, Object o)
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
