package com.example.soundbox_du_an_md31.Model;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

public class Comment {
    private String commentId;
    private String userId;
    private String userName;
    private String songId;
    private String commentText;
    private long timestamp;
    private int heartCount; // Thêm trường này để lưu số lượng tim
    // Các trường khác
    private List<String> heartedBy;
    public int getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }

    public List<String> getHeartedBy() {
        return heartedBy;
    }

    public void setHeartedBy(List<String> heartedBy) {
        this.heartedBy = heartedBy;
    }
    public Comment(String commentId, String userId, String userName, String songId, String commentText, long timestamp, int heartCount, boolean isDeleted, List<ReplyComment> replies ,List<String> heartedBy) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.songId = songId;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.heartCount = heartCount;
        this.isDeleted = isDeleted;
        this.replies = replies;
        this.heartedBy = heartedBy;
    }
    @PropertyName("deleted")
    private boolean isDeleted;
    // Thêm trường để lưu trữ danh sách bình luận con
    private List<ReplyComment> replies;

    public Comment() {
        this.replies = new ArrayList<>();
    }

    // Getter and Setter for replies
    public List<ReplyComment> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyComment> replies) {
        if (replies != null) {
            this.replies = replies;
        } else {
            this.replies = new ArrayList<>();
        }
    }
    public Comment(String commentId, String userId, String userName, String songId, String commentText, long timestamp , int heartCount ,List<String> heartedBy) {
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.songId = songId;
        this.commentText = commentText;
        this.timestamp = timestamp;
        this.heartCount = heartCount;
        this.heartedBy = heartedBy;
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
