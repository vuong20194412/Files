package com.example.files;

import static android.R.layout.simple_list_item_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup root, int __) {
        LayoutInflater inflater = LayoutInflater.from(root.getContext());
        View view = inflater.inflate(simple_list_item_1, root, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
