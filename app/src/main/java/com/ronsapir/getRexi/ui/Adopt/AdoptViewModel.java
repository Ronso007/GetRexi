package com.ronsapir.getRexi.ui.Adopt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdoptViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdoptViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}