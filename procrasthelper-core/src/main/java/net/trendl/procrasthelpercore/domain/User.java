package net.trendl.procrasthelpercore.domain;

import org.joda.time.DateTime;

import java.util.Map;

/**
 * Created by Tomas.Rendl on 7.8.2015.
 */
public class User {
    private String id;
    private String name;
    private String password;
    private String googleId;
    private DateTime passwordExpiration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DateTime getPasswordExpiration() {
        return passwordExpiration;
    }

    public void setPasswordExpiration(DateTime passwordExpiration) {
        this.passwordExpiration = passwordExpiration;
    }

    public static User convert(Map<String, Object > userMap) {
        User u = new User();
        u.setId((String)userMap.get("id"));
        u.setName((String)userMap.get("name"));
        u.setPassword((String) userMap.get("password"));
        u.setGoogleId((String)userMap.get("googleId"));
        // TODO: conversion for expiration date - it is too much work :(
        return u;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
