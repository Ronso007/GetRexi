package com.ronsapir.getRexi.auth.data;

import android.widget.ImageView;

import androidx.lifecycle.MutableLiveData;

import com.ronsapir.getRexi.auth.data.model.LoggedInUser;
import com.ronsapir.getRexi.auth.ui.login.LoginResult;
import com.ronsapir.getRexi.auth.ui.register.RegisterResult;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepository {

    private static volatile AuthRepository instance;

    private FirebaseDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private AuthRepository(FirebaseDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static AuthRepository getInstance(FirebaseDataSource dataSource) {
        if (instance == null) {
            instance = new AuthRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return dataSource.isLoggedIn();
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public void login(String username, String password, MutableLiveData<LoginResult> loginResult) {
        // handle login
        dataSource.login(username, password, loginResult);
    }

    public void register(String username, String password, String name, String phone, boolean isVerified, ImageView image, MutableLiveData<RegisterResult> registerResult) {
        // handle login
        dataSource.register(username, password,name, phone,isVerified, image,registerResult);
    }


}