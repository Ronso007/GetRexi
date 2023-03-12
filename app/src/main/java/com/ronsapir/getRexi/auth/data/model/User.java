package com.ronsapir.getRexi.auth.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

@Entity
public class User {
    public static final String COLLECTION = "users";
    private static final String ID = "id";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String IS_VERIFIED = "isVerified";
    private static final String IMAGE_URL = "imageUrl";

    @PrimaryKey
    @NonNull
    private String id;
    private String phoneNumber;
    private boolean isVerified;
    private String imageUrl;

    public User(@NonNull String id, String phoneNumber, boolean isVerified, String imageUrl) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.isVerified = isVerified;
        this.imageUrl = imageUrl;
    }

    public User(String id, String phoneNumber, boolean isVerified) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.isVerified = isVerified;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static User fromJson(Map<String, Object> json) {
        String id = (String)json.get(ID);
        String phoneNumber = (String)json.get(PHONE_NUMBER);
        boolean isVerified = (boolean)json.get(IS_VERIFIED);

        User user = new User(id,phoneNumber, isVerified);

        return user;
    }

    public Map<String,Object> toJson() {
        Map<String, Object> json = new HashMap<>();

        json.put(ID, getId());
        json.put(PHONE_NUMBER, getPhoneNumber());
        json.put(IS_VERIFIED, isVerified());
        json.put(IMAGE_URL, getImageUrl());

        return json;
    }
}
