package com.example.theatreapp.models;

public class AssignmentDetail {
    private String roleName;
    private String playTitle;
    private String assignmentType; // Если это будет добавлено (Основной/Дублёр)

    public AssignmentDetail(String roleName, String playTitle, String assignmentType) {
        this.roleName = roleName;
        this.playTitle = playTitle;
        this.assignmentType = assignmentType;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getPlayTitle() {
        return playTitle;
    }

    public String getAssignmentType() {
        return assignmentType;
    }

    // Метод для удобного отображения в списке
    @Override
    public String toString() {
        // Пример вывода: "Чацкий (Горе от ума) [Основной состав]"
        return roleName + " (" + playTitle + ")";
    }
}