package com.ronsapir.getRexi.ui.settings;

import static com.ronsapir.getRexi.auth.ui.register.RegisterViewModel.isNameValid;
import static com.ronsapir.getRexi.auth.ui.register.RegisterViewModel.isPasswordValid;
import static com.ronsapir.getRexi.auth.ui.register.RegisterViewModel.isPhoneValid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ronsapir.getRexi.HomeActivity;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.FirebaseDataSource;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.auth.data.model.User;
import com.ronsapir.getRexi.auth.ui.register.RegisterFormState;
import com.ronsapir.getRexi.databinding.FragmentSettingsBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class SettingsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FragmentSettingsBinding binding;
    private FirebaseDataSource dataSource;
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private Boolean isImageSelected = false;
    private User currentUser;

    private MutableLiveData<RegisterFormState> updateAccountFormState = new MutableLiveData<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        dataSource = new FirebaseDataSource();
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                if (result != null) {
                    binding.userImg.setImageBitmap(result);
                    isImageSelected = true;
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    binding.userImg.setImageURI(result);
                    isImageSelected = true;
                }
            }
        });

    }

    private void registerDataChanged(String password, String name, String phone) {
        if (!isPasswordValid(password)) {
            updateAccountFormState.setValue(new RegisterFormState(null, R.string.invalid_password,null,null));
        } else if (!isNameValid(name)) {
            updateAccountFormState.setValue(new RegisterFormState(null,null,R.string.invalid_name,null));
        } else if (!isPhoneValid(phone)) {
            updateAccountFormState.setValue(new RegisterFormState(null,null,null,R.string.invalid_phone));
        } else {
            updateAccountFormState.setValue(new RegisterFormState(true));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        setFields();
        setSaveButtonActionListener();
        setCameraButtonActionListener();
        setGalleryButtonActionListener();


        final EditText passwordEditText = binding.password;
        final EditText nameEditText = binding.username;
        final EditText phoneEditText = binding.phone;
        final SwitchCompat verifiedSwitch = binding.VerifiedSwitch;
        final Button updateAccountButton = binding.updateAccountButton;
        final ProgressBar loadingProgressBar = binding.loading;

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerDataChanged(passwordEditText.getText().toString(),
                        nameEditText.getText().toString(),
                        phoneEditText.getText().toString());
            }
        };

        passwordEditText.addTextChangedListener(afterTextChangedListener);
        nameEditText.addTextChangedListener(afterTextChangedListener);
        phoneEditText.addTextChangedListener(afterTextChangedListener);

        updateAccountFormState.observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) {
                    return;
                }
                updateAccountButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getNameError() != null) {
                    nameEditText.setError(getString(registerFormState.getNameError()));
                }
                if (registerFormState.getPhoneError() != null) {
                    phoneEditText.setError(getString(registerFormState.getPhoneError()));
                }
            }
        });
        return view;
    }

    private void setGalleryButtonActionListener() {
        binding.galleryButton.setOnClickListener(view1->{
            galleryLauncher.launch("image/*");
        });
    }

    private void setCameraButtonActionListener() {
        binding.cameraButton.setOnClickListener(view1->{
            cameraLauncher.launch(null);
        });
    }

    private void setFields() {
        FirebaseDataSource firebaseModel = new FirebaseDataSource();
        firebaseModel.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid(), user -> {
            this.currentUser = user;
            binding.username.setText(mAuth.getCurrentUser().getDisplayName());
            binding.phone.setText(user.getPhoneNumber());
            binding.VerifiedSwitch.setChecked(user.isHasVerified());

            if (!Objects.equals(user.getImageUrl(), ""))
            {
                Picasso.get().load(user.getImageUrl()).into(binding.userImg);
            }
        });
    }

    private void setSaveButtonActionListener() {
        binding.updateAccountButton.setOnClickListener(view1 -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String username = binding.username.getText().toString();
            String password = binding.password.getText().toString();
            String phoneNumber = binding.phone.getText().toString();
            Boolean isVerified = binding.VerifiedSwitch.isChecked();

            mAuth.getCurrentUser().updatePassword(password);

            User user = new User(userId,phoneNumber, isVerified);

            if (isImageSelected) {
                binding.userImg.setDrawingCacheEnabled(true);
                binding.userImg.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) binding.userImg.getDrawable()).getBitmap();
                Model.instance().uploadImage(userId, bitmap, url->{
                    if (url != null){
                        user.setImageUrl(url);
                        dataSource.UpdateUsernameAndPhoto(mAuth.getCurrentUser(),username,url, data -> {
                            updateDrawer();
                            Model.instance().addUser(user, (unused) -> {
                                Navigation.findNavController(view1).popBackStack();
                            });
                        });
                    }
                });
            } else {
                Model.instance().addUser(user, (unused) -> {
                    dataSource.UpdateUsernameAndPhoto(mAuth.getCurrentUser(),username,null,unused2 -> {
                        updateDrawer();
                        Navigation.findNavController(view1).popBackStack();
                    });
                });
            }


        });
    }

    private void updateDrawer() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.navigation_drawer);

        View headerView = navigationView.getHeaderView(0);
        ((HomeActivity) getActivity()).populateHeaderView(headerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}