package com.example.soundbox_du_an_md31.Model;

import java.io.Serializable;

public class Song implements Serializable {

    private int id;
    private String title;
    private String image;
    private String url;
    private String artist;
    private boolean latest;
    private boolean isVip;
    private String genre;
    private boolean featured;
    private int count;
    private boolean isPlaying;

    private boolean isCopyrighted;
    private boolean access;

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public boolean isCopyrighted() {
        return isCopyrighted;
    }

    public void setCopyrighted(boolean copyrighted) {
        isCopyrighted = copyrighted;
    }

    public Song() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGenre() {return genre;}

    public void setGenre(String genre) {this.genre = genre;}

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }
}
