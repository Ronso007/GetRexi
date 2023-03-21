package com.ronsapir.getRexi.auth.data.model;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.ronsapir.getRexi.AppContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
@Entity
public class Dog implements Serializable {
    private static final String NAME = "name";
    private static final String BREED = "breed";
    private static final String AGE = "age";
    private static final String IMAGE_URL = "imageUrl";
    private static final String TEMPERAMENT = "temperament";
    private static final String USER_ID = "userId";
    public static final String COLLECTION = "dogs";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String LOCAL_LAST_UPDATED = "dogs_local_last_update";

    @PrimaryKey
    @NonNull
    private String name;
    private String breed;
    private int age;
    private String imageUrl;
    private String temperament;
    private String userId;
    public Long lastUpdated;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] photo;

    public Dog() {}

    public Dog(@NonNull String name, String breed, int age, String imageUrl, String temperament, String userId) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.imageUrl = imageUrl;
        this.temperament = temperament;
        this.userId = userId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public static Long getLocalLastUpdate() {
        SharedPreferences sharedPref = AppContext.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        return sharedPref.getLong(LOCAL_LAST_UPDATED, 0);
    }

    public static void setLocalLastUpdate(Long time) {
        SharedPreferences sharedPref = AppContext.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LOCAL_LAST_UPDATED,time);
        editor.commit();
    }

    public static Dog fromJson(Map<String, Object> json) {
        String name = (String)json.get(NAME);
        String userId = (String)json.get(USER_ID);
        String breed = (String)json.get(BREED);
        int age = parseInt(json.get(AGE).toString());
        String imageUrl = (String)json.get(IMAGE_URL);
        String temperament = (String)json.get(TEMPERAMENT);

        Dog dog = new Dog(name, breed, age, imageUrl,temperament,userId);

        try {
            Timestamp time = (Timestamp) json.get(LAST_UPDATED);
            dog.setLastUpdated(time.getSeconds());
        } catch(Exception e) {}

        return dog;
    }

    public Map<String,Object> toJson() {
        Map<String, Object> json = new HashMap<>();

        json.put(NAME, getName());
        json.put(USER_ID, getUserId());
        json.put(BREED, getBreed());
        json.put(AGE, getAge());
        json.put(IMAGE_URL, getImageUrl());
        json.put(TEMPERAMENT, getTemperament());
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());

        return json;
    }
}
