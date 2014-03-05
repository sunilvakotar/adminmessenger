package com.ruby.admin.messanger.bean;

import java.util.Date;

/**
 * Created by Sunil Vakotar on 2/25/14.
 */
public class Message {
    private Integer id;
    private String message;
    private String date;
    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
