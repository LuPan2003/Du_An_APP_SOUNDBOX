package com.example.soundbox_du_an_md31.Model;
public class Comment {
    private String commentId;
    private String userId;
    private String userName;
    private String songId;
    private String commentText;
    private long timestamp;

    private boolean isDeleted;

    // Thêm constructor không tham số
    public Comment() {
        // Cần phải có constructor mặc định không tham số
    }

    public Comment(String commentId, String userId, String userName, String songId, String commentText, long timestamp) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.songId = songId;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
