package com.ronsapir.getRexi.auth.data;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.LoggedInUser;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.auth.data.model.User;
import com.ronsapir.getRexi.auth.ui.login.LoggedInUserView;
import com.ronsapir.getRexi.auth.ui.login.LoginResult;
import com.ronsapir.getRexi.auth.ui.register.RegisterResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class FirebaseDataSource {

    FirebaseFirestore db;
    FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private String Tag = "Auth";

    public FirebaseDataSource(){
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        storage = FirebaseStorage.getInstance();
    }

    public void register(String email, String password, String name, String phone, boolean isVerified, ImageView image, MutableLiveData<RegisterResult> registerResult) {
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
                            updateUsername(user,name, phone, isVerified, image, registerResult);

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

    public Result<LoggedInUser> login(String email, String password, MutableLiveData<LoginResult> loginResult) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(Tag, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginResult.setValue(new LoginResult(new LoggedInUserView(user.getDisplayName())));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(Tag, "signInWithEmail:failure", task.getException());
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                        }
                    }
                });
        try {
            return new Result.Error(new IOException("Error logging in"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
    public void logout() {
        mAuth.signOut();
    }

    private void updateUsername(FirebaseUser user, String name, String phone, boolean isVerified, ImageView image, MutableLiveData<RegisterResult> registerResult) {
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
                                User userDB = new User(user.getUid(),phone,isVerified,"");
                                if (image != null) {
                                    image.setDrawingCacheEnabled(true);
                                    image.buildDrawingCache();
                                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                                    Model.instance().uploadImage(user.getUid(), bitmap, url->{
                                        if (url != null){
                                            userDB.setImageUrl(url);
                                            updateUserPhoto(user,url);
                                        }
                                        Model.instance().addUser(userDB, data -> {
                                            registerResult.setValue(new RegisterResult(new LoggedInUserView(name)));
                                        });
                                    });
                                } else {
                                    Model.instance().addUser(userDB, data -> {
                                        registerResult.setValue(new RegisterResult(new LoggedInUserView(name)));
                                    });
                                }
                            } else {
                                registerResult.setValue(new RegisterResult(R.string.register_failed));
                            }
                        }
                    });

        } catch (Exception e) {
            Log.w(Tag, "updateProfile:failure", e);
        }
    }

    private void updateUserPhoto(FirebaseUser user, String url) {
        try{
            Uri photoUri = Uri.parse(url);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(photoUri)
                    .build();

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Tag, "User profile updated.");
                    } else {
                        Log.d(Tag, "User profile NOT updated.");
                    }
                }
            });
        } catch (Exception e) {
            Log.w(Tag, "updateProfile:failure", e);
        }
    }

    public void UpdateUsernameAndPhoto(FirebaseUser user,String username, String url, Model.Listener<Void> listener) {
        try{
            UserProfileChangeRequest profileUpdates;
            if (url != null) {
                Uri photoUri = Uri.parse(url);
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(photoUri).
                        setDisplayName(username)
                        .build();
            } else {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build();
            }

            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(Tag, "User profile updated.");
                        listener.onComplete(null);
                    } else {
                        Log.d(Tag, "User profile NOT updated.");
                    }
                }
            });
        } catch (Exception e) {
            Log.w(Tag, "updateProfile:failure", e);
        }
    }
    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public void uploadImage(String name, Bitmap bitmap, Model.Listener<String> listener) {
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + name + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        listener.onComplete(uri.toString());
                    }
                });
            }
        });
    }

    public void addUser(User user, Model.Listener<Void> listener) {
        db.collection(User.COLLECTION).document(user.getId()).set(user.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(null);
                    }
                });
    }
    public void getUserById(String userId, Model.Listener<User> callback) {
        db.collection("users").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onComplete(user);
                    }
                });
    }

    public void getAllDogsSince(Long since, Model.Listener<List<Dog>> callback){
        db.collection(Dog.COLLECTION)
                .whereGreaterThanOrEqualTo(Dog.LAST_UPDATED, new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Dog> list = new LinkedList<>();
                        if (task.isSuccessful()){
                            QuerySnapshot jsonsList = task.getResult();
                            for (DocumentSnapshot json: jsonsList){
                                Dog dog = Dog.fromJson(json.getData());
                                list.add(dog);
                            }
                        }
                        callback.onComplete(list);
                    }
                });
    }

    public void addDog(Dog dog, Model.Listener<Void> listener) {
        db.collection(Dog.COLLECTION).document(dog.getName()).set(dog.toJson())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onComplete(null);
                    }
                });
    }
}