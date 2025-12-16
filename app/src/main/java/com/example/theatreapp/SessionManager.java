package com.example.theatreapp;

import com.example.theatreapp.models.Actor;

public class SessionManager {
    private static SessionManager instance;
    private String currentActorId;
    private String currentActorName;

    private SessionManager() {
        // Приватный конструктор
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }


    public void createSession(Actor actor) {
        this.currentActorId = actor.getId();
        this.currentActorName = actor.getName();
    }

    public void clearSession() {
        this.currentActorId = null;
        this.currentActorName = null;
    }

    public String getCurrentActorId() {
        return currentActorId;
    }

    public String getCurrentActorName() {
        return currentActorName;
    }
}