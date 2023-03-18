package com.ronsapir.getRexi.auth.data;

import static java.lang.Integer.parseInt;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.databinding.FragmentAddOrEditDogBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddOrEditDogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddOrEditDogFragment extends Fragment {

    private FragmentAddOrEditDogBinding binding;
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private Boolean isImageSelected = false;
    public AddOrEditDogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity parentActivity = getActivity();

        parentActivity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.addDogFragment);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        },this, Lifecycle.State.RESUMED);

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), new ActivityResultCallback<Bitmap>() {
            @Override
            public void onActivityResult(Bitmap result) {
                if (result != null) {
                    binding.dogImg.setImageBitmap(result);
                    isImageSelected = true;
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null){
                    binding.dogImg.setImageURI(result);
                    isImageSelected = true;
                }
            }
        });
    }

    private void setCancelButtonActionListener() {
        binding.cancelBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack(R.id.navigation_my_pets,false));
        binding.cancelBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack(R.id.navigation_adopt,false));
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddOrEditDogBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        setFields();
        setSaveButtonActionListener();
        setCancelButtonActionListener();
        setCameraButtonActionListener();
        setGalleryButtonActionListener();

        return view;
    }

    private void setFields() {
        Bundle bundle = getArguments();

        if (bundle.get("Dog") != null) {
            Dog dog = (Dog) bundle.get("Dog");
            binding.dogName.setText(dog.getName());
            binding.dogAge.setText(Integer.toString(dog.getAge()));
            binding.dogBreed.setText(dog.getBreed());

            if (!Objects.equals(dog.getImageUrl(), ""))
            {
                Picasso.get().load(dog.getImageUrl()).into(binding.dogImg);
            }
        }
    }

    private void setSaveButtonActionListener() {
        binding.saveBtn.setOnClickListener(view1 -> {
            String dogName = binding.dogName.getText().toString();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            int dogAge = parseInt(binding.dogAge.getText().toString());
            String dogBreed = binding.dogBreed.getText().toString();
            String imageUrl = "";

            Dog dogToSave = new Dog(dogName, dogBreed, dogAge, imageUrl, "", userId);

            // TODO: Add API Call, remove thread.sleep
            /*DogBreedApi.instance().getDogBreedWikiPageByName(name, wikiPage -> {
                dogToSave.setWikipediaUrl(wikiPage);
            });*/

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Bundle bundle = getArguments();
            if (bundle.get("Dog") != null) {
                Dog dog = (Dog) bundle.get("Dog");
                dogToSave.setImageUrl(dog.getImageUrl());
            }

            if (isImageSelected) {
                binding.dogImg.setDrawingCacheEnabled(true);
                binding.dogImg.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) binding.dogImg.getDrawable()).getBitmap();
                Model.instance().uploadImage(dogName, bitmap, url->{
                    if (url != null){
                        dogToSave.setImageUrl(url);
                    }
                    Model.instance().addDog(dogToSave, (unused) -> {
                        Navigation.findNavController(view1).popBackStack();
                    });
                });
            } else {
                Model.instance().addDog(dogToSave, (unused) -> {
                    Navigation.findNavController(view1).popBackStack();
                });
            }
        });
    }
}