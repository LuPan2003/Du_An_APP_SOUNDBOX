package com.example.soundbox_du_an_md31.Model;

public class Feedback {

    private String name;
    private String phone;
    private String email;
    private String comment;
    private long timestamp; // Thêm trường thời gian
    private boolean reply; // Trường mới
    public Feedback() {
        // Cần có constructor mặc định cho Firebase
    }


    public Feedback(String name, String phone, String email, String comment, long timestamp, boolean reply) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.comment = comment;
        this.timestamp = timestamp;
        this.reply = reply;
    }

    public boolean isReply() {
        return reply;
    }

    public void setReply(boolean reply) {
        this.reply = reply;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
