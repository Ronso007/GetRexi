package com.ronsapir.getRexi.auth.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.LoggedInUser;
import com.ronsapir.getRexi.auth.ui.login.LoggedInUserView;
import com.ronsapir.getRexi.auth.ui.register.RegisterResult;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class AuthDataSource {

    private FirebaseAuth mAuth;
    private String Tag = "Auth";
    public void register(String email, String password, String name, MutableLiveData<RegisterResult> registerResult) {
        mAuth = FirebaseAuth.getInstance();
        LoggedInUser User;
        try {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Tag, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUsername(user,name, registerResult);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Tag, "signInWithEmail:failure", task.getException());
                            registerResult.setValue(new RegisterResult(R.string.register_failed));
                        }
                    }
                });

        } catch (Exception e) {

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

    private void updateUsername(FirebaseUser user, String name, MutableLiveData<RegisterResult> registerResult) {
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
                                registerResult.setValue(new RegisterResult(new LoggedInUserView(name)));
                            } else {
                                registerResult.setValue(new RegisterResult(R.string.register_failed));
                            }
                        }
                    });

        } catch (Exception e) {
            Log.w(Tag, "updateProfile:failure", e);
        }
    }
}