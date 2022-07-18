package com.example.files;

import static com.example.files.R.id.cancel_button_dialog;
import static com.example.files.R.id.positive_button_dialog;
import static com.example.files.R.id.editText_dialog;
import static com.example.files.R.id.notification_dialog;
import static com.example.files.R.id.title_dialog;
import static com.example.files.R.layout.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Dialog extends AlertDialog {
    interface PosButtonListener {
        boolean onClickPositiveButton(String result, TextView notification);
    }

    private final PosButtonListener listener;
    private final Triad triad;

    protected Dialog(@NonNull Context context, Triad triad, PosButtonListener listener) {
        super(context);
        this.triad = triad;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dialog);

        TextView title = findViewById(title_dialog);
        TextView notification = findViewById(notification_dialog);
        EditText editText = findViewById(editText_dialog);
        Button posButton = findViewById(positive_button_dialog);
        Button cancelButton = findViewById(cancel_button_dialog);

        if (title != null && notification != null
                && editText != null && posButton != null && cancelButton != null) {
            title.setText(triad.title);
            editText.setHint(triad.hint);
            posButton.setText(triad.posButtonText);
            posButton.setOnClickListener(v -> {
                String result = editText.getText().toString();
                if (result.length() > 0)
                    if (Dialog.this.listener.onClickPositiveButton(result, notification))
                        dismiss();
            });
            cancelButton.setOnClickListener(v -> dismiss());
        }
    }

    static class Triad {
        String title;
        String hint;
        String posButtonText;

        Triad(String title, String hint, String posButtonText) {
            this.title = title;
            this.hint = hint;
            this.posButtonText = posButtonText;
        }
    }
}
