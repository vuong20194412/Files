package com.example.files.nonActivity;

import static com.example.files.R.id.cancel_button__dialog;
import static com.example.files.R.id.editText__dialog;
import static com.example.files.R.id.positive_button__dialog;
import static com.example.files.R.id.textView_notification__dialog;
import static com.example.files.R.id.textView_title__dialog;
import static com.example.files.R.layout.dialog;

import android.widget.Button;
import android.widget.TextView;

import com.example.files.R;

public class Dialog extends android.app.Dialog {

    public interface PositiveButtonListener {
        boolean onClick(String result, TextView notification);
    }

    private PositiveButtonListener listener;
    private String title = "Title";
    private String hint = "Hint";
    private String posButtonText = "OK";

    public void setListener(PositiveButtonListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setPosButtonText(String posButtonText) {
        this.posButtonText = posButtonText;
    }

    public Dialog(@androidx.annotation.NonNull android.content.Context context) {
        super(context);
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dialog);

        TextView textViewTitle = findViewById(textView_title__dialog);
        TextView textViewNotification = findViewById(textView_notification__dialog);
        android.widget.EditText editText = findViewById(editText__dialog);
        Button positiveButton = findViewById(positive_button__dialog);
        Button negativeButton = findViewById(cancel_button__dialog);

        editText.setHint(hint);
        textViewTitle.setText(title);
        textViewNotification.setText("");
        positiveButton.setText(posButtonText);
        negativeButton.setText(getContext().getString(R.string.cancel));

        positiveButton.setOnClickListener(v -> {
            String result = editText.getText().toString();
            if (result.length() > 0 && listener != null)
                if (listener.onClick(result, textViewNotification))
                    dismiss();
        });

        negativeButton.setOnClickListener(v -> dismiss());
    }
}
