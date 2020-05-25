package com.ankur.journal;
import java.io.Serializable;
import java.util.Date;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

@SuppressWarnings("serial")
public class Journal implements Serializable{

    private String docId;
    private String userId;
    private String userName;

    private String jTitle;
    private String jText;

    private String imageURL;

    private String date;
    private String day;
    private String month;
    private String year;
    private String time;
    private Timestamp timestamp;

    public Journal() {
    }

    public Journal(String docId, String userId, String userName, String jTitle, String jText, String imageURL,
                   String date, String day, String month, String year, String time) {
        this.docId = docId;
        this.userId = userId;
        this.userName = userName;
        this.jTitle = jTitle;
        this.jText = jText;
        this.imageURL = imageURL;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.time = time;
    }

    public Journal(String docId, String userId, String userName, String jTitle, String jText, String date, String day, String month, String year, String time) {
        this.docId = docId;
        this.userId = userId;
        this.userName = userName;
        this.jTitle = jTitle;
        this.jText = jText;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.time = time;
    }

    public Journal(String docId) {
        this.docId = docId;
    }

    public Journal(String docId, String userId, String userName, String jTitle, String jText,
                   String date, String day, String month, String year, String time, Timestamp timestamp) {

        this.docId = docId;
        this.userId = userId;
        this.userName = userName;
        this.jTitle = jTitle;
        this.jText = jText;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.time = time;
        this.timestamp = timestamp;
    }


    public Journal(String docId, String userId, String userName, String jTitle, String jText, String imageURL,
                   String date, String day, String month, String year, String time, Timestamp timestamp) {

        this.docId = docId;
        this.jTitle = jTitle;
        this.jText = jText;
        this.userId = userId;
        this.userName = userName;
        this.date = date;
        this.imageURL = imageURL;
        this.day = day;
        this.month = month;
        this.year = year;
        this.time = time;
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    public String getjTitle() {
        return jTitle;
    }

    public void setjTitle(String jTitle) {
        this.jTitle = jTitle;
    }

    public String getjText() {
        return jText;
    }

    public void setjText(String jText) {
        this.jText = jText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
