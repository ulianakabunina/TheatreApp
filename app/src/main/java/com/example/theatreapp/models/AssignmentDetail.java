package com.example.theatreapp.models;

public class AssignmentDetail {
    private String roleName;
    private String playTitle;
    private String assignmentType;
    private String actorName; // Добавьте если нужно

    public AssignmentDetail() {
        // Пустой конструктор
    }

    public AssignmentDetail(String roleName, String playTitle, String assignmentType) {
        this.roleName = roleName;
        this.playTitle = playTitle;
        this.assignmentType = assignmentType;
    }

    // Геттеры и сеттеры
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getPlayTitle() { return playTitle; }
    public void setPlayTitle(String playTitle) { this.playTitle = playTitle; }

    public String getAssignmentType() { return assignmentType; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }
}