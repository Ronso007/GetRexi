package com.ronsapir.getRexi.ui.MyPets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;

import java.util.List;

public class MyPetsViewModel extends ViewModel {

    LiveData<List<Dog>> getData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return Model.instance().getAllDogsByUserId(user.getUid());
        }

        return null;
    }
}