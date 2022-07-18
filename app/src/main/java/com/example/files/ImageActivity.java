package com.example.files;

import static com.example.files.R.id.image_view_image;
import static com.example.files.R.id.zoom_in;
import static com.example.files.R.id.zoom_out;
import static com.example.files.R.layout.activity_image;
import static com.example.files.R.string.intent_pathname;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_image);
        imageView = findViewById(image_view_image);
        String pathname = getIntent().getStringExtra(getString(intent_pathname));
        this.setTitle(pathname.substring(pathname.lastIndexOf("/") + 1));
        imageView.setImageURI(Uri.parse(pathname));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zoom, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == zoom_in) {
            imageView.setScaleX(2f);
            imageView.setScaleY(2f);
        }
        else if (id == zoom_out) {
            imageView.setScaleX(1f);
            imageView.setScaleY(1f);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Handler.canClick = true;
        super.onBackPressed();
    }
}