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

     /**
      * retrieves and returns all History objects stored in the database.
      * @return a List of History objects.
      */
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

     /**
      * Retrieves and returns all History objects stored in the database where the action is 'DELETE'
      * @return a List of History objects.
      */
     @GET
     @Path("/deleted")
     @Produces("application/json")
     public List<History> getHistoryDeletedContracts(@QueryParam("datastoreKey") String datastoreKey) {
         JSONArray jsonHistory = new JSONArray();
         for (DatastoreObject ds : Datastores.getDatastores()) {
             if (datastoreKey.equals(ds.getKey())) {
                 jsonHistory = DBFirebird.executeSQL(ds, "SELECT * FROM HISTORY WHERE ATTRIBUTE = 'contract' AND H_ACTION = 'DELETE'");
             }
         }
         return JsonToHistory(jsonHistory);
     }

     /**
      * Retrieves and returns all History objects with the given attribute (contract and contractDetail)
      * @return a List of History objects.
      */
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

     /**
      * Retrieves all History objects corresponding to the given ID and attribute.
      * @param attribute contract or ContractDetail.
      * @param attribute_id the ID of the corresponding object.
      * @return a List of History objects.
      */
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

     /**
      * Inserts a given History object into the database.
      * @param history the object that needs to be inserted.
      */
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

     /**
      * Deletes all History objects in the database relating to the given ID.
      * @param datastoreKey the key for the used database.
      * @param id the attribute ID of all objects that need to be deleted.
      */
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

     /**
      * Goes over every object in jsonHistorys and maps its values onto History objects that are then returned in a List
      * @param jsonHistorys a JSONArray with all objects received from the database.
      * @return List<History>
      */
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