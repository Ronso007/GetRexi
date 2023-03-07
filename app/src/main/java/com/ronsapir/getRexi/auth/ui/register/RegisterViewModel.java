package com.ronsapir.getRexi.auth.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.ronsapir.getRexi.auth.data.AuthRepository;
import com.ronsapir.getRexi.auth.data.Result;
import com.ronsapir.getRexi.auth.data.model.LoggedInUser;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.ui.login.LoggedInUserView;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private AuthRepository loginRepository;

    RegisterViewModel(AuthRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String email, String password, String name) {
        // can be launched in a separate asynchronous job
        Result<LoggedInUser> result = loginRepository.register(email, password,name);

        if (result instanceof Result.Success) {
            LoggedInUser data = ((Result.Success<LoggedInUser>) result).getData();
            registerResult.setValue(new RegisterResult(new LoggedInUserView(data.getDisplayName())));
        } else {
            registerResult.setValue(new RegisterResult(R.string.register_failed));
        }
    }

    public void registerDataChanged(String email, String password, String name) {
        if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_email, null,null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password,null));
        } else if (!isNameValid(name)) {
            registerFormState.setValue(new RegisterFormState(null,null,R.string.invalid_name));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isNameValid(String name) {
        return name != null && name.trim().length() > 3;
    }

    // A placeholder email validation check
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (email.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        return false;
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}