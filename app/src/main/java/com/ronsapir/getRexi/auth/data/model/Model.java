package com.ronsapir.getRexi.auth.data.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ronsapir.getRexi.auth.data.FirebaseDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model {
    final public MutableLiveData<LoadingState> EventDogsListLoadingState = new MutableLiveData<LoadingState>(LoadingState.NOT_LOADING);
    private LiveData<List<Dog>> dogList;
    private LiveData<List<Dog>> userDogList;
    AppLocalDbRepository localDb = AppLocalDb.getAppDb();
    private Executor executor = Executors.newSingleThreadExecutor();
    private static final Model _instance = new Model();

    private Model() {}

    public static Model instance() {
        return _instance;
    }
    private FirebaseDataSource firebaseModel = new FirebaseDataSource();

    public interface Listener<T> {
        void onComplete(T data);
    }
    public enum LoadingState {
        LOADING,
        NOT_LOADING
    }
    public void uploadImage(String name, Bitmap bitmap, Listener<String> listener) {
        firebaseModel.uploadImage(name,bitmap,listener);
    }

    public void addUser(User user, Listener<Void> listener){
        firebaseModel.addUser(user, (Void)-> {
            listener.onComplete(null);
        });
    }

    public LiveData<List<Dog>> getAllDogs() {
        if (dogList == null) {
            dogList = (LiveData<List<Dog>>) localDb.dogDao().getAll();
            refreshAllDogs();
        }
        return dogList;
    }

    public LiveData<List<Dog>> getAllDogsByUserId(String userId) {
        userDogList = (LiveData<List<Dog>>) localDb.dogDao().getAllByUserId(userId);
        refreshAllDogs();

        return userDogList;
    }

    public void refreshAllDogs(){
        EventDogsListLoadingState.setValue(LoadingState.LOADING);
        Long localLastUpdate = Dog.getLocalLastUpdate();
        firebaseModel.getAllDogsSince(localLastUpdate,list->{
            executor.execute(()->{
                Log.d("TAG", " firebase return : " + list.size());
                Long time = localLastUpdate;
                for(Dog dog: list){
                    dog.setPhoto(urlToByteArr(dog.getImageUrl()));
                    // insert new records into ROOM
                    localDb.dogDao().insertAll(dog);
                    if (time < dog.getLastUpdated()){
                        time = dog.getLastUpdated();
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Dog.setLocalLastUpdate(time);
                EventDogsListLoadingState.postValue(LoadingState.NOT_LOADING);
            });
        });
    }

    public void addDog(Dog dog, Listener<Void> listener){
        firebaseModel.addDog(dog, (Void)->{
            refreshAllDogs();
            listener.onComplete(null);
        });
    }

    public byte[] urlToByteArr(String link) {
        byte[] imageBytes = null;
        try {
            URL url = new URL(link);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            imageBytes = outputStream.toByteArray();
            inputStream.close();
            outputStream.close();

        } catch(Exception e) {
            System.out.println(e);
        } finally {
            return imageBytes;
        }
    }
}
