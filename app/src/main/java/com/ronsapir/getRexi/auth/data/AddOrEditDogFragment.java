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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.databinding.FragmentAddOrEditDogBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddOrEditDogFragment} factory method to
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

        Spinner dogBreed = view.findViewById(R.id.dogBreed);
/*        DogApi.instance().getBreedOptions(optionsList -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, optionsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dogBreed.setAdapter(adapter);
        });*/
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        dogBreed.setOnItemSelectedListener(listener);
        getOptionsFromApi(dogBreed);
        return view;
    }

    private void getOptionsFromApi(Spinner dogBreed ) {
        String url = "https://api.thedogapi.com/v1/breeds";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> optionsList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String option = jsonObject.getString("name");
                            optionsList.add(option);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    // Populate the options in the dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, optionsList);




                    adapter.setDropDownViewResource(R.layout.spinner_style);
                    dogBreed.setAdapter(adapter);
                },
                error ->  Log.e("error", error.getMessage()));

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }


    private void setFields() {
        Bundle bundle = getArguments();

        if (bundle.get("Dog") != null) {
            Dog dog = (Dog) bundle.get("Dog");
            binding.dogName.setText(dog.getName());
            binding.dogAge.setText(Integer.toString(dog.getAge()));
            binding.dogBreed.setPrompt(dog.getBreed());

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
            String dogBreed = binding.dogBreed.getSelectedItem().toString();
            String imageUrl = "";

            Dog dogToSave = new Dog(dogName, dogBreed, dogAge, imageUrl, "", userId, "");

            getDogTemperament(dogBreed, temperemant -> {
                getLifeSpan(dogBreed, lifespan -> {
                    dogToSave.setTemperament(temperemant);
                    dogToSave.setLifeSpan(lifespan);
                    Bundle bundle = getArguments();
                    if (bundle.get("Dog") != null) {
                        Dog dog = (Dog) bundle.get("Dog");
                        dogToSave.setImageUrl(dog.getImageUrl());
                    }

                    if (isImageSelected) {
                        binding.dogImg.setDrawingCacheEnabled(true);
                        binding.dogImg.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) binding.dogImg.getDrawable()).getBitmap();
                        Model.instance().uploadImage(dogName, bitmap, url -> {
                            if (url != null) {
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
            });
        });
    }

    public void getDogTemperament(String dogBreed, Model.Listener<String> callback) {
        String url = "https://api.thedogapi.com/v1/breeds/search?name=" + dogBreed;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                        try {
                            JSONObject json = response.getJSONObject(0);
                            String temperament = json.getString("temperament");
                            callback.onComplete(temperament);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                },
                error ->  Log.e("error", error.getMessage()));

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }

    public void getLifeSpan(String dogBreed, Model.Listener<String> callback) {
        String url = "https://api.thedogapi.com/v1/breeds/search?name=" + dogBreed;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject json = response.getJSONObject(0);
                        String lifespan = json.getString("life_span");
                        callback.onComplete(lifespan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error ->  Log.e("error", error.getMessage()));

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }
}