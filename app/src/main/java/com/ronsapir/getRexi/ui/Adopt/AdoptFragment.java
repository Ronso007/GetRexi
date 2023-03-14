package com.ronsapir.getRexi.ui.Adopt;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ronsapir.getRexi.auth.data.DogRecyclerAdapter;
import com.ronsapir.getRexi.auth.data.model.Dog;
import com.ronsapir.getRexi.auth.data.model.Model;
import com.ronsapir.getRexi.databinding.FragmentAdoptBinding;
import com.ronsapir.getRexi.ui.Adopt.AdoptFragmentDirections;

public class AdoptFragment extends Fragment {

    private FragmentAdoptBinding binding;
    DogRecyclerAdapter adapter;
    AdoptViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdoptBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DogRecyclerAdapter(getLayoutInflater(), viewModel.getData().getValue(), "DogsList");
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new DogRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                Log.d("TAG", "Row was clicked " + pos);
                Dog dog = viewModel.getData().getValue().get(pos);
                com.ronsapir.getRexi.ui.Adopt.AdoptFragmentDirections.ActionNavigationAdoptToDogDetailsFragment action =
                        AdoptFragmentDirections.actionNavigationAdoptToDogDetailsFragment(dog);
                Navigation.findNavController(view).navigate(action);
            }
        });

        binding.progressBar.setVisibility(View.GONE);

        viewModel.getData().observe(getViewLifecycleOwner(),list-> {
            adapter.setData(list);
        });

        Model.instance().EventDogsListLoadingState.observe(getViewLifecycleOwner(), status-> {
            binding.swipeRefresh.setRefreshing(status == Model.LoadingState.LOADING);
        });

        binding.swipeRefresh.setOnRefreshListener(()-> {
            reloadData();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel = new ViewModelProvider(this).get(AdoptViewModel.class);
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