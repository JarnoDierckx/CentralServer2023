package com.fooditsolutions.authenticationservice.model;

import javax.json.bind.annotation.JsonbProperty;
import java.sql.Timestamp;

public class Session {

    @JsonbProperty("sessionkey")
    private String sessionKey;
    @JsonbProperty("lastused")
    private Timestamp lastUsed;
    @JsonbProperty("userid")
    private int userID;

    public Session(String sessionKey, Timestamp lastUsed, int userID) {
        this.sessionKey = sessionKey;
        this.lastUsed = lastUsed;
        this.userID = userID;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public Timestamp getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(Timestamp lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
