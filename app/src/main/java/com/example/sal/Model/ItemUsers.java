package com.example.sal.Model;

public class ItemUsers {
    private String userfam;
    private String username;
    private String role;

    public ItemUsers() {
        // Пустой конструктор без аргументов
    }

    public ItemUsers(String userfam, String username, String role) {
        this.userfam = userfam;
        this.username = username;
        this.role = role;
    }

    public String getUserFam() {
        return userfam;
    }

    public void setUserFam(String userfam) {
        this.userfam = userfam;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}