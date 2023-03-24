package com.fooditsolutions.datastoreservice.model;


import com.fooditsolutions.datastoreservice.controller.Util;
import com.fooditsolutions.datastoreservice.model.centralserver.Bjr;
import com.fooditsolutions.datastoreservice.model.centralserver.Client;
import com.fooditsolutions.datastoreservice.model.centralserver.ModuleId;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class Sqlmodel {
    /**
     * Gets called on objects that need to be inserted into the database.
     * It loops over the different properties in the object, taking their names and values and putting them into sqlInsertInto and sqlValues respectively.
     * Both variables are then used to create the INSERT INTO query.
     * @return The created query is then returned as a string.
     */
    public String getInsertStatement() {
        Table classAnnotation = this.getClass().getAnnotation(Table.class);

        Field[] fields = this.getClass().getDeclaredFields();

        StringBuilder sqlInserInto = new StringBuilder();
        StringBuilder sqlValues = new StringBuilder();
        try {
            for (Field f : fields) {
                Annotation annotation = f.getAnnotation(Id.class);
                if (!(annotation instanceof Id)) {
                    Object o = runGetter(f, this);
                    if (o != null && !Objects.equals(o, 0) && !Objects.equals(o, 0.00) && !(o instanceof Client) && !(o instanceof Bjr) && !(o instanceof ModuleId)) {
                        if (!sqlInserInto.toString().equals("")) {
                            sqlInserInto.append(",");
                            sqlValues.append(",");
                        }
                        sqlInserInto.append(f.getName());
                        sqlValues.append(Util.structureSQL(o));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "INSERT INTO " + classAnnotation.name() +
                " (" + sqlInserInto + ")" +
                " VALUES (" + sqlValues + ");";
        return result;
    }

    public String getDeleteStatement() {
        Table classAnnotation = this.getClass().getAnnotation(Table.class);

        Field[] fields = this.getClass().getDeclaredFields();


        String sqlWhere = "";
        try {
            for (Field f : fields) {
                Annotation annotation = f.getAnnotation(Id.class);
                if ((annotation instanceof Id)) {
                    Object o = runGetter(f, this);
                    if (o != null) {

                        sqlWhere += f.getName() + "=" + Util.structureSQL(o);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "DELETE FROM " + classAnnotation.name() + " WHERE " +
                sqlWhere + ";";
        return result;
    }

    /**
     * Gets called on objects that need to update existing values in the database.
     * It loops over the different properties in the object, taking their names and values and puts it into sqlSet.
     * Any values that are empty or are themselves an object are ignored.
     * any property that has the @ID annotation is looped over separately and put into sqlWhere.
     * Both sqlSet and sqlWhere are then used to create a sql update query.
     *
     * @return gives back a working update query.
     */
    public String getUpdateStatement() {
        Table classAnnotation = this.getClass().getAnnotation(Table.class);

        Field[] fields = this.getClass().getDeclaredFields();

        StringBuilder sqlSet = new StringBuilder();
        StringBuilder sqlWhere = new StringBuilder();
        try {
            for (Field f : fields) {
                Annotation annotation = f.getAnnotation(Id.class);
                if ((annotation instanceof Id)) {
                    Object o = runGetter(f, this);
                    if (o != null) {
                        sqlWhere.append(f.getName()).append("=").append(Util.structureSQL(o));
                    }
                } else {
                    Object o = runGetter(f, this);
                    if (o != null && !Objects.equals(o, 0) && !Objects.equals(o, 0.00) && !(o instanceof Client) && !(o instanceof Bjr) && !(o instanceof ModuleId)) {
                        if (!sqlSet.toString().equals("")) {
                            sqlSet.append(",");

                        }
                        sqlSet.append(f.getName()).append("=").append(Util.structureSQL(o));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String result = "UPDATE " + classAnnotation.name() +
                " SET " + sqlSet +
                " WHERE " + sqlWhere + ";";
        return result;
    }


    /**
     * is used to retrieve the method to get the value of an object
     *
     * @param field is the name of the object.
     * @param o     is the object that needs its value found
     * @return calls the getter of the object.
     */
    private static Object runGetter(Field field, Object o) {
        // MZ: Find the correct method
        for (Method method : o.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    // MZ: Method found, run it
                    try {
                        return method.invoke(o);
                    } catch (IllegalAccessException e) {
                        System.out.println("Could not determine method: " + method.getName());
                    } catch (InvocationTargetException e) {
                        System.out.println("Could not determine method: " + method.getName());
                    }

                }
            }
        }

        return null;
    }
}
