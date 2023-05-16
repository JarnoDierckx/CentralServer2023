package com.fooditsolutions.historyservice.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooditsolutions.util.enums.Action;
import com.fooditsolutions.util.model.History;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class HistoryController {

    public static void addHistory(History history){

    }

    /**
     * turns a bunch of History objects in json format into a List of History objects.
     * @param jsonHistories History objects in json format, send as a String.
     * @return A List of history objects.
     * @throws IOException
     */
    public static List<History> createHistoryInformation(String jsonHistories) throws IOException {
        List<History> histories = new ArrayList<>();
        History[] histories2;

        byte[] jsonData = jsonHistories.getBytes();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        histories2 = mapper.readValue(jsonData, History[].class);
        histories = Arrays.asList(histories2);
        return histories;
    }

    /**
     * Goes through a List of History objects, any create or delete actions are returned by default but only the latest update action is returned with the list if 'full' isn't true.
     * @param full a boolean deciding whether all objects need to be returned of if only the newest of the update objects need to be included.
     * @param historyList The List that needs to potentially be sorted out
     * @return the filtered list of history objects.
     */
    public static List<History> getFull(boolean full, List<History> historyList) {
        if (!full) {
            List<History> histories = new ArrayList<>();
            History hUpdate = new History();
            for (History h : historyList) {
                if (h.getAction().equals(Action.CREATE) || h.getAction().equals(Action.DELETE)) {
                    histories.add(h);
                } else if (h.getAction().equals(Action.UPDATE)) {
                    if (hUpdate.getTs() == null) {
                        hUpdate = h;
                    } else {
                        Timestamp ts1 = Timestamp.valueOf(h.getTs());
                        Timestamp ts2 = Timestamp.valueOf(hUpdate.getTs());
                        if (ts1.after(ts2)) {
                            hUpdate = h;
                        }
                    }
                }
            }
            histories.add(hUpdate);
            historyList = histories;
        }

        return historyList;
    }
}