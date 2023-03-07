package com.ronsapir.getRexi.auth.data;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ronsapir.getRexi.auth.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class AuthDataSource {

    private FirebaseAuth mAuth;
    private String Tag = "Auth";
    public Result<LoggedInUser> register(String email, String password, String name) {
        mAuth = FirebaseAuth.getInstance();
        LoggedInUser User;
        try {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            Log.d("@@@@@@@@",mAuth.getCurrentUser().getUid());
                        }
                    });

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                updateUsername(user,name);
                User = new LoggedInUser(
                                user.getUid(),
                                name);
                return new Result.Success<>(User);
            }
            return new Result.Error(new IOException("Error logging in"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public Result<LoggedInUser> login(String email, String password) {
        mAuth = FirebaseAuth.getInstance();

        try {
            return new Result.Error(new IOException("Error logging in"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
    public void logout() {
        mAuth.signOut();
    }

    private void updateUsername(FirebaseUser user, String name) {
        try {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(Tag, "User profile updated.");
                            }
                        }
                    });

        } catch (Exception e) {
            Log.w(Tag, "updateProfile:failure", e);
        }
    }
}