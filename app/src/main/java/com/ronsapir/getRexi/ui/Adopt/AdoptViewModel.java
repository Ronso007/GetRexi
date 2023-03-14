package com.ronsapir.getRexi.ui.Adopt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;

import java.util.List;

public class AdoptViewModel extends ViewModel {

    private LiveData<List<Dog>> data = Model.instance().getAllDogs();

    LiveData<List<Dog>> getData() {
        return data;
    }
}