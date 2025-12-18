package com.example.theatreapp.models;

public class Assignment {
    // В Firestore ID документа используется как ключ,
    // но оставим поле для удобства.
    private String assignmentId;
    private String playId;
    private String actorId;
    private String roleName; // Используем roleName вместо roleId

    // Дополнительные поля для отображения (не хранятся в Firestore)
    // transient означает, что эти поля не будут сохраняться в Firestore
    private transient String actorName;
    private transient String playTitle;
    private transient String documentId; // ID документа в Firestore

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

    // Дополнительные геттеры и сеттеры
    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getPlayTitle() { return playTitle; }
    public void setPlayTitle(String playTitle) { this.playTitle = playTitle; }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    // Для обратной совместимости с кодом, который использует getId()
    public String getId() {
        return documentId != null ? documentId : assignmentId;
    }

    public void setId(String id) {
        this.documentId = id;
    }
}