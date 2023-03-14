package com.ronsapir.getRexi.auth.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.databinding.DogListRowBinding;
import com.ronsapir.getRexi.ui.MyPets.MyPetsFragmentDirections;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class DogViewHolder extends RecyclerView.ViewHolder {

    DogListRowBinding binding;
    TextView breed;
    TextView dogName;
    TextView dogAge;
    ImageView dogImage;
    List<Dog> data;
    Button editButton;

    public DogViewHolder(@NonNull View itemView, DogRecyclerAdapter.OnItemClickListener listener, List<Dog> data) {
        super(itemView);
        this.binding = DogListRowBinding.bind(itemView);;
        this.data = data;
        breed = binding.breed;
        dogName = binding.dogName;
        dogAge = binding.dogAge;
        dogImage = binding.dogImage;
        editButton = binding.editButton;

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = getAdapterPosition();
                listener.onItemClick(pos);
            }
        });
    }

    public void bind(Dog dog, String sourceFrag) {
        breed.setText("Breed - " + dog.getBreed());
        dogName.setText(dog.getName());
        dogAge.setText("Age of - " + Integer.toString(dog.getAge()));

        if (dog.getPhoto() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(dog.getPhoto(), 0, dog.getPhoto().length);
            binding.dogImage.setImageBitmap(bitmap);
        } else if (!Objects.equals(dog.getImageUrl(), "")) {
            Picasso.get().load(dog.getImageUrl()).into(dogImage);
        }

        FirebaseDataSource firebaseModel = new FirebaseDataSource();
        firebaseModel.getUserById(FirebaseAuth.getInstance().getCurrentUser().getUid(), user -> {

            if(user.getId().equals(dog.getUserId())) {
                editButton.setVisibility(View.VISIBLE);

                if (Objects.equals(sourceFrag, "MyPosts")) {
                    NavDirections action1 = com.ronsapir.getRexi.ui.MyPets.MyPetsFragmentDirections.actionNavigationMyPetsToAddDogFragment(dog, "Edit Dog");
                    editButton.setOnClickListener(Navigation.createNavigateOnClickListener(action1));
                } /*else {
                    NavDirections action2 = BooksListFragmentDirections.actionBooksListFragmentToAddBookFragment(dog, "Edit Dog");
                    editButton.setOnClickListener(Navigation.createNavigateOnClickListener(action2));
                }*/
            }
        });
    }
}

