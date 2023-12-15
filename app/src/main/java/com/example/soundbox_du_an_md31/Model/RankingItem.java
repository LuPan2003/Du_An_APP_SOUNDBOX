package com.example.soundbox_du_an_md31.Model;

public class RankingItem {
    private String userName;
    private String userEmail;
    private int userPoints;
    public RankingItem(String userName, String userEmail, int userPoints) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPoints = userPoints;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getUserPoints() {
        return userPoints;
    }
}
