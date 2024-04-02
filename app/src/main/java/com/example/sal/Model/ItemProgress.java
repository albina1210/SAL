package com.example.sal.Model;

public class ItemProgress {
    private String userId;
    private String userfam;
    private String username;
    private int progress;

    public ItemProgress(String userId, String userfam, String username, int progress) {
        this.userId = userId;
        this.userfam = userfam;
        this.username = username;
        this.progress = progress;
    }

    public ItemProgress() {
        // Пустой конструктор без аргументов
    }

    public String getUserId() {
        return userId;
    }

    public String getUserfam() {
        return userfam;
    }

    public String getUsername() {
        return username;
    }

    public int getProgress() {
        return progress;
    }
}