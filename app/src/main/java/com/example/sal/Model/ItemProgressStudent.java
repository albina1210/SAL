package com.example.sal.Model;

public class ItemProgressStudent {
    private String userFam;
    private String userName;
    private String progress;

    public ItemProgressStudent() {
        this.userFam = "";
        this.userName = "";
        this.progress = "";
    }

    public ItemProgressStudent(String userFam, String userName, String progress) {
        this.userFam = userFam;
        this.userName = userName;
        this.progress = progress;
    }

    public String getUserFam() {
        return userFam;
    }

    public void setUserFam(String userFam) {
        this.userFam = userFam;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}