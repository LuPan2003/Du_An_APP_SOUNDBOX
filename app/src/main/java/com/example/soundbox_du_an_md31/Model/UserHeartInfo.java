package com.example.soundbox_du_an_md31.Model;

public class UserHeartInfo {
    private String userHeartId;
    private String userId;
    private String commentId;
    private String songId;
    private String commentText;
    private boolean hearted; // Trường mới để kiểm tra đã thả tim hay chưa

    // Constructor mặc định (cần thiết cho Firebase)
    public UserHeartInfo() {
    }

    public UserHeartInfo(String userHeartId, String userId, String commentId, String songId, String commentText, boolean hearted) {
        this.userId = userId;
        this.commentId = commentId;
        this.songId = songId;
        this.commentText = commentText;
        this.hearted = hearted;
    }

    public String getUserHeartId() {
        return userHeartId;
    }

    public void setUserHeartId(String userHeartId) {
        this.userHeartId = userHeartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public boolean isHearted() {
        return hearted;
    }

    public void setHearted(boolean hearted) {
        this.hearted = hearted;
    }
}