package com.example.files.nonActivity;

import androidx.annotation.NonNull;

public abstract class BaseAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup root, int __) {
        return new ViewHolder(android.view.LayoutInflater.from(root.getContext())
                .inflate(android.R.layout.simple_list_item_1, root, false));
    }
}
