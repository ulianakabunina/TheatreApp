package com.example.theatreapp.models;

public class Play { // Бывший Performance.java
    private String playId; // Ключ документа Firestore
    private String title;
    private String author;
    private String director;
    // Дополнительные поля для полноты FR-05
    private String genre;
    private String status;
    private String premiereDate;

    public Play() {
    }

    public Play(String playId, String title, String author, String director, String genre, String status, String premiereDate) {
        this.playId = playId;
        this.title = title;
        this.author = author;
        this.director = director;
        this.genre = genre;
        this.status = status;
        this.premiereDate = premiereDate;
    }

    // --- Getters и Setters ---
    public String getPlayId() { return playId; }
    public void setPlayId(String playId) { this.playId = playId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    // Getters/Setters для дополнительных полей
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPremiereDate() { return premiereDate; }
    public void setPremiereDate(String premiereDate) { this.premiereDate = premiereDate; }
}