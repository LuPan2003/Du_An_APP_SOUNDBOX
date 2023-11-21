package com.example.soundbox_du_an_md31.Model;
public class ReplyComment {
    private String replyId;
    private String commentId;
    private String userId;
    private String userName;
    private String commentText;
    private String replyText;
    private long timestamp;
    // Constructor không có đối số (nếu bạn không thêm constructor này, Firebase sẽ gặp vấn đề)
    public ReplyComment() {
        // Cần phải có constructor không có đối số cho Firebase
    }

    public ReplyComment(String replyId, String commentId, String userId, String userName, String commentText, String replyText, long timestamp) {
        this.replyId = replyId;
        this.commentId = commentId;
        this.userId = userId;
        this.userName = userName;
        this.commentText = commentText;
        this.replyText = replyText;
        this.timestamp = timestamp;
    }


    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
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

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
