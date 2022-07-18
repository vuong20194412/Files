package com.example.files;

import static android.graphics.Color.RED;
import static com.example.files.R.id.textView_text;
import static com.example.files.R.layout.activity_text;
import static com.example.files.R.string.intent_pathname;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class TextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_text);
        TextView textView = findViewById(textView_text);
        File file = new File(getIntent().getStringExtra(getString(intent_pathname)));
        setTitle(file.getName());
        getContentTxtFile(textView, file);
    }

    private void getContentTxtFile(@NonNull TextView textView, File txtFile) {
        textView.setText("");
        try {
            FileInputStream fileInputStream = new FileInputStream(txtFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            AtomicInteger offset = new AtomicInteger();
            AtomicInteger number = new AtomicInteger();
            int SIZE = 1024;
            char[] chars = new char[SIZE];
            while (true) {
                number.set(inputStreamReader.read(chars, offset.get(), SIZE - offset.get()));
                if (number.get() == -1) {
                    break;
                }
                StringBuilder block = new StringBuilder();
                number.set(number.get() + offset.get());
                while (offset.get() < number.get()) {
                    block.append(chars[offset.getAndAdd(1)]);
                }
                textView.append(block);
                if (offset.get() == SIZE) {
                    offset.set(0);
                    number.set(0);
                }
            }

            fileInputStream.close();
        } catch (IOException e) {
            textView.setTextColor(RED);
            textView.setText(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Handler.canClick = true;
        super.onBackPressed();
    }
}