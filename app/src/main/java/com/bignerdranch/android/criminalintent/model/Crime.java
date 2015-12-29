package com.bignerdranch.android.criminalintent.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "CRIME".
 */
public class Crime {

    private Long id;
    private String title;
    private java.util.Date date;
    private Boolean solved;
    private String suspect;

    public Crime() {
    }

    public Crime(Long id) {
        this.id = id;
    }

    public Crime(Long id, String title, java.util.Date date, Boolean solved, String suspect) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.solved = solved;
        this.suspect = suspect;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

}
