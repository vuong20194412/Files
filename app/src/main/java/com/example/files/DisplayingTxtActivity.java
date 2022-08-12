package com.example.files;

import static android.graphics.Color.RED;

import static com.example.files.R.id.textView__activity_displaying_txt;
import static com.example.files.R.layout.activity_displaying_txt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

public class DisplayingTxtActivity extends androidx.appcompat.app.AppCompatActivity {

    android.widget.TextView textView;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_displaying_txt);

        textView = findViewById(textView__activity_displaying_txt);
        File file = new File(getIntent().getStringExtra(getString(R.string.intent_pathname)));

        setTitle(file.getName());
        getContentTxtFile(file);
    }

    private void getContentTxtFile(File txtFile) {

        textView.setText("");

        try {
            FileInputStream in = new FileInputStream(txtFile);
            InputStreamReader reader = new InputStreamReader(in);

            int LENGTH = 1024;
            char[] buffer = new char[LENGTH];

            AtomicInteger offset = new AtomicInteger();
            AtomicInteger number = new AtomicInteger();

            while (true) {
                number.set(reader.read(buffer, offset.get(), LENGTH - offset.get()));
                if (number.get() == -1) {
                    break;
                }

                textView.append(new StringBuilder().append(buffer, offset.get(), number.get()));

                if (offset.addAndGet(number.get()) == LENGTH) {
                    offset.set(0);
                }
            }

            in.close();
        }
        catch (java.io.IOException e) {
            textView.setTextColor(RED);
            textView.setText(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}