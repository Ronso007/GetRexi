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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.databinding.FragmentAddOrEditDogBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        getOptionsFromApi(dogBreed);

        return view;
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

    private void getOptionsFromApi(Spinner dogBreed ) {
        String url = "https://api.thedogapi.com/v1/breeds";
        String apiKey = "live_SrBT4v5Y02emFdlSrLKr7t12AQ8uomXOIx4poV3J4OJQZh9rKg17P5kuiCFCCena";
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
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    dogBreed.setAdapter(adapter);
                },
                error ->  Log.e("error", error.getMessage())
        ){
                // Override the getHeaders() method to add the API key header
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("x-api-key", apiKey);
                    return headers;
                }
            };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }

    private void setDogTemperament(Dog dogToSave) {
        String url = "https://api.thedogapi.com/v1/breeds/search?name=" + dogToSave.getBreed();
        String apiKey = "live_SrBT4v5Y02emFdlSrLKr7t12AQ8uomXOIx4poV3J4OJQZh9rKg17P5kuiCFCCena";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        String temperament = jsonObject.getString("temperament");
                        dogToSave.setTemperament(temperament);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("error", error.getMessage())
        ) {
            // Override the getHeaders() method to add the API key header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-api-key", apiKey);
                return headers;
            }
        };

        // Add the request to the request queue
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }

    private void setDogLifeSpan(Dog dogToSave) {
        String url = "https://api.thedogapi.com/v1/breeds/search?name=" + dogToSave.getBreed();
        String apiKey = "live_SrBT4v5Y02emFdlSrLKr7t12AQ8uomXOIx4poV3J4OJQZh9rKg17P5kuiCFCCena";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        String lifeSpan = jsonObject.getString("life_span");
                        dogToSave.setLifeSpan(lifeSpan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("error", error.getMessage())
        ) {
            // Override the getHeaders() method to add the API key header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("x-api-key", apiKey);
                return headers;
            }
        };

        // Add the request to the request queue
        Volley.newRequestQueue(getContext()).add(jsonArrayRequest);
    }

    private void setSaveButtonActionListener() {
        binding.saveBtn.setOnClickListener(view1 -> {
            String dogName = binding.dogName.getText().toString();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            int dogAge = parseInt(binding.dogAge.getText().toString());
            String dogBreed = binding.dogBreed.getSelectedItem().toString();
            String imageUrl = "";

            Dog dogToSave = new Dog(dogName, dogBreed, dogAge, imageUrl, "", userId, "");
            setDogTemperament(dogToSave);
            setDogLifeSpan(dogToSave);

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