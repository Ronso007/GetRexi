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
    private static final String HAS_VERIFIED = "hasVerified";
    private static final String IMAGE_URL = "imageUrl";

    @PrimaryKey
    @NonNull
    private String id;
    private String phoneNumber;
    private boolean hasVerified;
    private String imageUrl;

    public User() {}
    public User(@NonNull String id, String phoneNumber, boolean hasVerified, String imageUrl) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.hasVerified = hasVerified;
        this.imageUrl = imageUrl;
    }

    public User(String id, String phoneNumber, boolean hasVerified) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.hasVerified = hasVerified;
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

    public boolean isHasVerified() {
        return hasVerified;
    }

    public void setHasVerified(boolean hasVerified) {
        this.hasVerified = hasVerified;
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
        boolean isVerified = (boolean)json.get(HAS_VERIFIED);

        User user = new User(id,phoneNumber, isVerified);

        return user;
    }

    public Map<String,Object> toJson() {
        Map<String, Object> json = new HashMap<>();

        json.put(ID, getId());
        json.put(PHONE_NUMBER, getPhoneNumber());
        json.put(HAS_VERIFIED, isHasVerified());
        json.put(IMAGE_URL, getImageUrl());

        return json;
    }
}
