package com.example.theatreapp.models;

public class Actor {
    private String id; // Ключ документа Firestore
    private String name; // ФИО (используется name в Firestore)
    private String role; // "admin" или "actor"

    public Actor() {
    }

    public Actor(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    // --- Getters и Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; } // getName вместо getFio
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}