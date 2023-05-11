 package com.fooditsolutions.datastoreservice.centralsserverresource;

 import com.fooditsolutions.datastoreservice.controller.DBFirebird;
 import com.fooditsolutions.datastoreservice.controller.Datastores;
 import com.fooditsolutions.datastoreservice.controller.Util;
 import com.fooditsolutions.datastoreservice.model.DatastoreObject;
 import com.fooditsolutions.datastoreservice.model.centralserver.Contract;
 import com.fooditsolutions.datastoreservice.model.centralserver.History;
 import org.json.JSONArray;

 import javax.ws.rs.*;
 import java.math.BigDecimal;
 import java.sql.Timestamp;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;

 @Path("/history")
public class HistoryResource {

     @GET
     @Produces("application/json")
     public List<History> getHistory(@QueryParam("datastoreKey") String datastoreKey) {
         JSONArray jsonHistory = new JSONArray();
         for (DatastoreObject ds : Datastores.getDatastores()) {
             if (datastoreKey.equals(ds.getKey())) {
                 jsonHistory = DBFirebird.executeSQL(ds, "SELECT * FROM HISTORY");
             }
         }
         return JsonToHistory(jsonHistory);
     }

     @GET
     @Produces("application/json")
     @Path("/{ATTRIBUTE}")
     public List<History> getHistory(@PathParam("ATTRIBUTE") String attribute,
             @QueryParam("datastoreKey") String datastoreKey) {
         JSONArray jsonHistory = new JSONArray();
         for (DatastoreObject ds : Datastores.getDatastores()) {
             if (datastoreKey.equals(ds.getKey())) {
                 String sql = "SELECT * FROM HISTORY WHERE ATTRIBUTE='" + attribute + "'";
                 jsonHistory = DBFirebird.executeSQL(ds,sql );
             }
         }
         return JsonToHistory(jsonHistory);
     }

     @GET
     @Produces("application/json")
     @Path("/{ATTRIBUTE}/{ATTRIBUTE_ID}")
     public List<History> getHistory(@PathParam("ATTRIBUTE") String attribute,
                                     @PathParam("ATTRIBUTE_ID") String attribute_id,
                                     @QueryParam("datastoreKey") String datastoreKey) {
         JSONArray jsonHistory = new JSONArray();
         for (DatastoreObject ds : Datastores.getDatastores()) {
             if (datastoreKey.equals(ds.getKey())) {
                 String sql = "SELECT * FROM HISTORY WHERE ATTRIBUTE='" + attribute + "' AND ATTRIBUTE_ID = " + attribute_id;
                 jsonHistory = DBFirebird.executeSQL(ds,sql );
             }
         }
         return JsonToHistory(jsonHistory);
     }
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public void createHistory(@QueryParam("datastoreKey") String datastoreKey, History history) {
        String sql = history.getInsertStatement();

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                DBFirebird.executeSQLInsert(ds, sql);
                System.out.println("Insert successful");
            }
        }
    }

    @DELETE
    @Path("/{id}")
    public void deleteAssociatedHistory(@QueryParam("datastoreKey") String datastoreKey,@PathParam("id") int id){
         String sql="DELETE FROM HISTORY WHERE ATTRIBUTE_ID="+id;

        for (DatastoreObject ds : Datastores.getDatastores()) {
            if (datastoreKey.equals(ds.getKey())) {
                //Insert also works for delete
                DBFirebird.executeSQLInsert(ds, sql);
                System.out.println("Delete successful");
            }
        }
    }

     public List<History> JsonToHistory(JSONArray jsonHistorys) {
         List<History> historys = new ArrayList<>();
         for (int i = 0; i < jsonHistorys.length(); i++) {
             History history = new History();

             history.setId((int) jsonHistorys.getJSONObject(i).opt("ID"));
             history.setATTRIBUTE((String) jsonHistorys.getJSONObject(i).opt("ATTRIBUTE"));
             history.setATTRIBUTE_ID(new BigDecimal((Long) jsonHistorys.getJSONObject(i).opt("ATTRIBUTE_ID")));
             history.setH_ACTION((String) jsonHistorys.getJSONObject(i).opt("H_ACTION"));
             history.setACTOR((String) jsonHistorys.getJSONObject(i).opt("ACTOR"));
             history.setDESCRIPTION((String) jsonHistorys.getJSONObject(i).opt("DESCRIPTION"));
             history.setTS(jsonHistorys.getJSONObject(i).opt("TS").toString());

             historys.add(history);
         }

         return historys;
     }
}