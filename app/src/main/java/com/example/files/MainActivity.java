package com.example.files;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import static com.example.files.BuildConfig.APPLICATION_ID;

import android.content.Intent;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.files.nonActivity.Handler;

import java.io.File;

public class MainActivity extends androidx.appcompat.app.AppCompatActivity {

    private final String storageDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final int REQUEST_CODE = 18072022;
    private final int REQUEST_CODE_HANDLER = 12082022;
    private Handler handler;
    private File file;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         handler = new Handler(this, storageDirPath, REQUEST_CODE_HANDLER);

        com.example.files.nonActivity.RecyclerView recyclerView = findViewById(R.id.recyclerView__activity_main);

        recyclerView.setAdapter(handler.getAdapter());
        handler.setTextView(findViewById(R.id.textView__activity_main));
        onRequestPermissions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.option_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.create_directory)
            handler.createDirectory();
        else if (id == R.id.create_txt_file)
            handler.createTxtFile();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, @NonNull android.view.View view, android.view.ContextMenu.ContextMenuInfo menuInfo) {
        file = (File) view.getTag();

        if (file.isFile())
            getMenuInflater().inflate(R.menu.file_context_menu, menu);
        else if (file.isDirectory())
            getMenuInflater().inflate(R.menu.directory_context_menu, menu);

        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (file.isDirectory()) {
            if (id == R.id.rename)
                handler.renameDirectory(file);
            else if (id == R.id.delete)
                handler.deleteDirectory(file);
        } else {
            if (id == R.id.rename)
                handler.renameFile(file);
            else if (id == R.id.delete)
                handler.deleteFile(file);
            else if (id == R.id.copy_file)
                handler.copyFile(file);
            else if (id == R.id.move_file)
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
    public void
    onRequestPermissionsResult(int code, @NonNull String[] permissions, @NonNull int[] results) {
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

        if (Environment.isExternalStorageManager())
            handler.moveRoot(storageDirPath);
        else
            registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Environment.isExternalStorageManager())
                       handler.moveRoot(storageDirPath);
                    else
                       exitNonGrantPermission();
                }
            )
            .launch(
                new Intent(
                    ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    android.net.Uri.parse("package:" + APPLICATION_ID)
                )
            );
    }

    private void exitNonGrantPermission() {

        new android.app.AlertDialog
            .Builder(this)
            .setTitle("Operation not permit")
            .setMessage("App need permission,so app shutdown same error.")
            .setNeutralButton(
                "Understood",
                (dialog, which) -> {
                    finishAndRemoveTask();
                    dialog.dismiss();
                }
            )
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_HANDLER) {
            handler.setCanClick(true);
        }
    }
}