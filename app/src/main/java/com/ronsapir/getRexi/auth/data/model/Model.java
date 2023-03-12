package com.ronsapir.getRexi.auth.data.model;

import android.graphics.Bitmap;

import com.ronsapir.getRexi.auth.data.FirebaseDataSource;

public class Model {
    private static final Model _instance = new Model();

    private Model() {}

    public static Model instance() {
        return _instance;
    }
    private FirebaseDataSource firebaseModel = new FirebaseDataSource();

    public interface Listener<T> {
        void onComplete(T data);
    }

    public void uploadImage(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImage(name,bitmap,listener);
    }

    public void addUser(User user, Listener<Void> listener){
        firebaseModel.addUser(user, (Void)-> {
            listener.onComplete(null);
        });
    }
}
