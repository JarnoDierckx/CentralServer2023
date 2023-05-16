package com.fooditsolutions.datastoreservice.controller;

import com.fooditsolutions.datastoreservice.model.DatastoreObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DBFirebird {

    /**
     * Function to execute update queries.
     * @param ds is the object containing the info needed to connect to the database.
     * @param query is the query that will be executed by stmt.executeUpdate().
     * @return a JSONArray with the results of the query
     */
    public static JSONArray executeSQL(DatastoreObject ds, String query){
        JSONArray jsonResult = new JSONArray();
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");

            Connection connection = null;

            connection = DriverManager.getConnection(
                    ds.getConnectionString(),
                    ds.getUserName(), ds.getPassword());


            Statement stmt = connection.createStatement();

            try {
                ResultSet rs = stmt.executeQuery(
                        query);
                ResultSetMetaData md = rs.getMetaData();
                int numCols = md.getColumnCount();
                List<String> colNames = IntStream.range(0, numCols)
                        .mapToObj(i -> {
                            try {
                                return md.getColumnName(i + 1);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return "?";
                            }
                        })
                        .collect(Collectors.toList());


                while (rs.next()) {
                    JSONObject row = new JSONObject();
                    colNames.forEach(cn -> {
                        try {
                            row.put(cn, rs.getObject(cn));
                        } catch (JSONException | SQLException e) {
                            e.printStackTrace();
                        }
                    });
                    jsonResult.put(row);
                }

           /* rs.next();
            String firstName = rs.getString(1);
            String lastName = rs.getString(2);*/

              } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return jsonResult;
    }

    /**
     * Function to execute update queries.
     * @param ds is the object containing the info needed to connect to the database.
     * @param query is the query that will be executed by stmt.executeUpdate().
     */
    public static void executeSQLUpdate(DatastoreObject ds, String query){
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");

            Connection connection = null;

            connection = DriverManager.getConnection(
                    ds.getConnectionString(),
                    ds.getUserName(), ds.getPassword());


            Statement stmt = connection.createStatement();

            try {
                stmt.executeUpdate(query);

            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Function to execute INSERT sql queries.
     * @param ds is the object containing the info needed to connect to the database.
     * @param query is the query that will be executed by stmt.execute().
     */
    public static void executeSQLInsert(DatastoreObject ds,String query){
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");

            Connection connection = null;

            connection = DriverManager.getConnection(
                    ds.getConnectionString(),
                    ds.getUserName(), ds.getPassword());


            Statement stmt = connection.createStatement();

            try {
                stmt.execute(query);

            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
