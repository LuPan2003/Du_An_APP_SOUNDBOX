package com.example.soundbox_du_an_md31.Model;

public class Feedback {

    private String name;
    private String phone;
    private String email;
    private String comment;
    private long timestamp; // Thêm trường thời gian
    private boolean reply; // Trường mới
    private int month; // Thêm trường tháng
    private int year;  // Thêm trường năm
    public Feedback() {
        // Cần có constructor mặc định cho Firebase
    }

    public Feedback(String name, String phone, String email, String comment, long timestamp, boolean reply, int month, int year) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.comment = comment;
        this.timestamp = timestamp;
        this.reply = reply;
        this.month = month;
        this.year = year;
    }


    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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
