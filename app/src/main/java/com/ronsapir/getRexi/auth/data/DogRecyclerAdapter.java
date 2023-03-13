package com.ronsapir.getRexi.auth.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ronsapir.getRexi.R;
import com.ronsapir.getRexi.auth.data.model.Dog;

import java.util.List;

public class DogRecyclerAdapter extends RecyclerView.Adapter<DogViewHolder> {

    OnItemClickListener listener;
    LayoutInflater inflater;
    List<Dog> data;
    String sourceFrag;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public void setData(List<Dog> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public DogRecyclerAdapter(LayoutInflater inflater, List<Dog> data, String sourceFrag) {
        this.inflater = inflater;
        this.data = data;
        this.sourceFrag = sourceFrag;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.dog_list_row, parent,false);
        return new DogViewHolder(view, listener, data);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        Dog dog = data.get(position);
        holder.bind(dog, sourceFrag);
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }

        return data.size();
    }
}
