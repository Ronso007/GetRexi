package com.ronsapir.getRexi.ui.MyPets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.DogRecyclerAdapter;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.databinding.FragmentMyPetsBinding;

public class MyPetsFragment extends Fragment {

    private FragmentMyPetsBinding binding;
    DogRecyclerAdapter adapter;
    MyPetsViewModel myPetsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyPetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.myPostsRecyclerView.setHasFixedSize(true);
        binding.myPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DogRecyclerAdapter(getLayoutInflater(), myPetsViewModel.getData().getValue(), "MyPosts");
        binding.myPostsRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DogRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
            }
        });

        View addButton = root.findViewById(R.id.addBtn);
        NavDirections action =  MyPetsFragmentDirections.actionNavigationMyPetsToAddDogFragment(null, "Add Dog");
        addButton.setOnClickListener(Navigation.createNavigateOnClickListener(action));

        binding.myPostsProgressBar.setVisibility(View.GONE);

        myPetsViewModel.getData().observe(getViewLifecycleOwner(),list->{
            adapter.setData(list);
        });

        Model.instance().EventDogsListLoadingState.observe(getViewLifecycleOwner(), status->{
            binding.myPostsSwipeRefresh.setRefreshing(status == Model.LoadingState.LOADING);
        });

        binding.myPostsSwipeRefresh.setOnRefreshListener(()->{
            reloadData();
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myPetsViewModel = new ViewModelProvider(this).get(MyPetsViewModel.class);
    }

    void reloadData() {
        Model.instance().refreshAllDogs();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}