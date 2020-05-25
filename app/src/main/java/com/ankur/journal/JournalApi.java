package com.ankur.journal;

import android.app.Application;

public class JournalApi extends Application {
    private String userId;
    private String userName;
    public static JournalApi instance;

    public JournalApi() {
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

    public static JournalApi getInstance() {
        if(instance == null)
            instance = new JournalApi();

        return instance;
    }
}
