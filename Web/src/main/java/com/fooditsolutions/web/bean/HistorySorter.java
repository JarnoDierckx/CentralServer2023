package com.fooditsolutions.web.bean;

import com.fooditsolutions.util.model.History;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistorySorter {
    public static void sortByTimestamp(List<History> historyList) {
        Collections.sort(historyList, new Comparator<History>() {
            @Override
            public int compare(History h1, History h2) {
                return h1.getTs().compareTo(h2.getTs());
            }
        });
    }
}
