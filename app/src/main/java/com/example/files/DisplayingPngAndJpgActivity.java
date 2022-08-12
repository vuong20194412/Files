package com.example.files;

import static com.example.files.R.id.touchZoomImageView__activity_displaying_png_jpg;
import static com.example.files.R.layout.activity_displaying_png_jpg;
import static com.example.files.R.string.intent_pathname;

public class DisplayingPngAndJpgActivity extends androidx.appcompat.app.AppCompatActivity {

    com.example.files.nonActivity.TouchZoomImageView imageView;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_displaying_png_jpg);

        String pathname = getIntent().getStringExtra(getString(intent_pathname));
        imageView = findViewById(touchZoomImageView__activity_displaying_png_jpg);

        setTitle(pathname.substring(pathname.lastIndexOf("/") + 1));
        imageView.setImage(android.graphics.drawable.BitmapDrawable.createFromPath(pathname));
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}