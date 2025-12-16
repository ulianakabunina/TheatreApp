package com.example.theatreapp.models;

public class Assignment {
    // В Firestore ID документа используется как ключ,
    // но оставим поле для удобства.
    private String assignmentId;
    private String playId;
    private String actorId;
    private String roleName; // Используем roleName вместо roleId

    public Assignment() {
    }

    public Assignment(String assignmentId, String playId, String actorId, String roleName) {
        this.assignmentId = assignmentId;
        this.playId = playId;
        this.actorId = actorId;
        this.roleName = roleName;
    }

    // --- Getters и Setters ---
    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }
    public String getPlayId() { return playId; }
    public void setPlayId(String playId) { this.playId = playId; }
    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}