package com.fooditsolutions.authenticationservice.controller;

import com.fooditsolutions.authenticationservice.model.Session;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SessionController {
    private static List<Session> sessions;
    static {
        sessions=new ArrayList<Session>();
    }

    public static void AddSession(Session session){
        sessions.add(session);
        System.out.println("Sessions:" + sessions);
    }

    public static void DeleteSession(String sessionKey){
        List<Session> sessions2=new ArrayList<>();
        for(Session s:sessions){
            if (!s.getSessionKey().equals(sessionKey)){
                sessions2.add(s);
            }
        }
        sessions=sessions2;
    }

    public static List<Session> getSessions() {
        return sessions;
    }

    public static boolean Validate(String sessionKey){
        boolean result=false;
        List<Session> sessions2=new ArrayList<>();
        for(Session s:sessions){
            if (s.getSessionKey().equals(sessionKey)){
                s.setLastUsed(new Timestamp(System.currentTimeMillis()));
                result=true;
            }
            sessions2.add(s);
        }
        sessions=sessions2;
        return result;
    }
}
