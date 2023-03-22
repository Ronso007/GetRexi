package com.ronsapir.getRexi.auth.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.databinding.FragmentDogDetailsBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DogDetailsFragment extends Fragment {

    FragmentDogDetailsBinding binding;
    public DogDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDogDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setFields();

        return view;
    }

    private void setFields() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            Dog dog = (Dog) bundle.get("Dog");
            binding.name.setText(dog.getName());
            binding.dogAge.setText(Integer.toString(dog.getAge()));
            binding.dogBreed.setText(dog.getBreed());
            binding.LifeSpan.setText(dog.getLifeSpan());
            binding.temperament.setText(dog.getTemperament());

            if (dog.getPhoto() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(dog.getPhoto(), 0, dog.getPhoto().length);
                binding.dogImage.setImageBitmap(bitmap);
            }
            else if (!Objects.equals(dog.getImageUrl(), ""))
            {
                Picasso.get().load(dog.getImageUrl()).into(binding.dogImage);
            }

            FirebaseDataSource firebaseModel = new FirebaseDataSource();
            firebaseModel.getUserById(dog.getUserId(), user -> {
                binding.sellerPhoneNumber.setText("Phone Number:  " + user.getPhoneNumber());
            });
        }
    }
}