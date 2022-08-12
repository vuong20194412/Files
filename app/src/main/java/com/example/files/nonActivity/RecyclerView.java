package com.example.files.nonActivity;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {

    public RecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, androidx.recyclerview.R.attr.recyclerViewStyle);
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHasFixedSize(true);
        setLongClickable(true);
        setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(context));
    }
}
