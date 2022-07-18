package com.example.files;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

public class RecyclerView extends androidx.recyclerview.widget.RecyclerView {

    public RecyclerView(@NonNull Context context) {
        super(context);
        setHasFixedSize(true);
        setLongClickable(true);
        setLayoutManager(new LinearLayoutManager(context));
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setHasFixedSize(true);
        setLongClickable(true);
        setLayoutManager(new LinearLayoutManager(context));
    }

    public RecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHasFixedSize(true);
        setLongClickable(true);
        setLayoutManager(new LinearLayoutManager(context));
    }
}
