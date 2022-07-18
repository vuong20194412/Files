package com.example.files;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static com.example.files.BuildConfig.APPLICATION_ID;
import static com.example.files.R.id.copy_file;
import static com.example.files.R.id.create_directory;
import static com.example.files.R.id.create_txt_file;
import static com.example.files.R.id.delete;
import static com.example.files.R.id.move_file;
import static com.example.files.R.id.recyclerView_files;
import static com.example.files.R.id.rename;
import static com.example.files.R.id.textView_path;
import static com.example.files.R.layout.activity_main;
import static com.example.files.R.menu.directory_context_menu;
import static com.example.files.R.menu.file_context_menu;
import static com.example.files.R.menu.option_menu;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private final String storageDirPath = getExternalStorageDirectory().getAbsolutePath();
    private final int REQUEST_CODE = 18072022;
    private final Handler handler = new Handler(this);
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        com.example.files.RecyclerView recyclerView = findViewById(recyclerView_files);

        recyclerView.setAdapter(handler.getAdapter());
        handler.setTextView(findViewById(textView_path));
        onRequestPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == create_directory)
            handler.createDirectory();
        else if (id == create_txt_file)
            handler.createTxtFile();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        if (view instanceof TextView) {
            file = (File) view.getTag();
            if (file.isFile())
                getMenuInflater().inflate(file_context_menu, menu);
            else if (file.isDirectory())
                getMenuInflater().inflate(directory_context_menu, menu);

            super.onCreateContextMenu(menu, view, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (file.isDirectory()) {
            if (id == rename)
                handler.renameDirectory(file);
            if (id == delete)
                handler.deleteDirectory(file);
        } else {
            if (id == rename)
                handler.renameFile(file);
            if (id == delete)
                handler.deleteFile(file);
            if (id == copy_file)
                handler.copyFile(file);
            if (id == move_file)
                handler.moveFile(file);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        String parentPathname = handler.getParentPathName();
        if (parentPathname != null)
            handler.moveRoot(parentPathname);
        else
            super.onBackPressed();
    }

    private void onRequestPermissions() {
        if (SDK_INT < 23)
            handler.moveRoot(storageDirPath);
        else if (SDK_INT < 30)
            onRequestPermissionsApi23();
        else
            onRequestPermissionsApi30();
    }

    @RequiresApi(value = 23)
    private void onRequestPermissionsApi23() {
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        else
            handler.moveRoot(storageDirPath);
    }

    @Override
    @RequiresApi(value = 23)
    public void onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(code, permissions, results);
        if (code == REQUEST_CODE && results.length > 0) {
            if (results[0] == PERMISSION_GRANTED)
                handler.moveRoot(storageDirPath);
            else
                exitNonGrantPermission();
        }
    }

    @RequiresApi(value = 30)
    private void onRequestPermissionsApi30() {
        if (!Environment.isExternalStorageManager()) {
            Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + APPLICATION_ID));
            ActivityResultLauncher<Intent> launcher = registerForActivityResult(new StartActivityForResult(), result -> {
                if (Environment.isExternalStorageManager()) {
                    handler.moveRoot(storageDirPath);
                } else {
                    exitNonGrantPermission();
                }
            });
            launcher.launch(intent);
        } else {
            handler.moveRoot(storageDirPath);
        }
    }

    private void exitNonGrantPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Operation not permit")
                .setMessage("App need permission,so app shutdown same error.")
                .setNeutralButton("Understood", (dialog, which) -> {
                    finishAndRemoveTask();
                    dialog.dismiss();
                });
        alertDialog.show();
    }
}