package com.ronsapir.getRexi.auth.data;

import androidx.lifecycle.MutableLiveData;

import com.ronsapir.getRexi.auth.data.model.LoggedInUser;
import com.ronsapir.getRexi.auth.ui.register.RegisterResult;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepository {

    private static volatile AuthRepository instance;

    private AuthDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private AuthRepository(AuthDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static AuthRepository getInstance(AuthDataSource dataSource) {
        if (instance == null) {
            instance = new AuthRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<LoggedInUser> login(String username, String password) {
        // handle login
        Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        return result;
    }

    public void register(String username, String password, String name, MutableLiveData<RegisterResult> registerResult) {
        // handle login
        dataSource.register(username, password,name, registerResult);
    }
}