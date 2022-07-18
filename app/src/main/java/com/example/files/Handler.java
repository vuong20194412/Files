package com.example.files;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.files.R.string.cancel;
import static com.example.files.R.string.copy;
import static com.example.files.R.string.create;
import static com.example.files.R.string.create_directory;
import static com.example.files.R.string.create_txt_file;
import static com.example.files.R.string.delete;
import static com.example.files.R.string.existed_pathname;
import static com.example.files.R.string.failed_delete;
import static com.example.files.R.string.intent_pathname;
import static com.example.files.R.string.move;
import static com.example.files.R.string.non_valid_name;
import static com.example.files.R.string.non_valid_pathname;
import static com.example.files.R.string.rename;
import static com.example.files.R.string.type_file_non_open;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Handler {

    static boolean canClick = true;
    private final Activity activity;
    private TextView textView;
    private File root;
    private final List<File> list = new ArrayList<>();
    private class Adapter extends com.example.files.Adapter {

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            File file = list.get(position);
            View view = holder.itemView;
            if (view instanceof TextView) {

                activity.unregisterForContextMenu(view);
                ((TextView) view).setText(file.getName());
                view.setTag(file);
                activity.registerForContextMenu(view);

                view.setOnClickListener(v -> {

                    File f = (File) v.getTag();
                    if (f.isDirectory()) {
                        moveRoot(f.getAbsolutePath());
                    } else {
                        if (canClick) {
                            canClick = false;
                        String name = f.getName();
                        int beginIndex = name.length() - 4;
                        if (beginIndex >= 0) {
                            String pathname = f.getAbsolutePath();
                            Intent intent;
                            switch (name.substring(beginIndex)) {
                                case ".txt":
                                    intent = new Intent(activity, TextActivity.class);
                                    intent.putExtra(activity.getString(intent_pathname), pathname);
                                    intent.putExtra("idView", v.getId());
                                    activity.startActivity(intent);
                                    return;
                                case ".jpg":
                                case ".png":
                                    intent = new Intent(activity, ImageActivity.class);
                                    intent.putExtra(activity.getString(intent_pathname), pathname);
                                    activity.startActivity(intent);
                                    return;
                            }
                        }
                        Toast.makeText(activity,
                                activity.getString(type_file_non_open), LENGTH_SHORT).show();
                        canClick = true;
                    }
                    }
                });
            }
        }
    }
    private final Adapter adapter = new Adapter();

    public Handler(@NonNull Activity activity) {
        this.activity = activity;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    @Nullable
    public String getParentPathName() {
        if (root.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            return null;
        }
        return root.getParent();
    }

    // Directory
    void renameDirectory(@NonNull File directory) {
        new Dialog(activity,
                new Dialog.Triad("Rename directory", "New name", activity.getString(rename)),
                (newName, notification) -> {

                    File destFile = new File(root.getAbsolutePath(), newName);

                    if (destFile.exists()) {
                        notification.setText(activity.getString(existed_pathname));
                        return false;
                    }

                    if (directory.renameTo(destFile)) {
                        updateList();
                        return true;
                    } else {
                        notification.setText(activity.getString(non_valid_name));
                        return false;
                    }
                }).show();
    }

    void deleteDirectory(@NonNull File directory) {
        new AlertDialog.Builder(activity)
                .setTitle("Delete Directory")
                .setMessage("Would you like delete this directory?")
                .setNegativeButton(activity.getString(cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(activity.getString(delete), (dialog, which) -> {
                    if (directory.exists()) {
                        if (directory.delete())
                            updateList();
                        else
                            Toast.makeText(activity, activity.getString(failed_delete), LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    void createDirectory() {
        new Dialog(activity,
                new Dialog.Triad(activity.getString(create_directory), "Name", activity.getString(create)),
                (name, notification) -> {

                    File dir = new File(root.getAbsolutePath(), name);

                    if (dir.exists()) {
                        notification.setText(activity.getString(existed_pathname));
                        return false;
                    }

                    if (dir.mkdir()) {
                        updateList();
                        return true;
                    } else {
                        notification.setText(activity.getString(non_valid_name));
                        return false;
                    }

                }).show();
    }

    // File
    void renameFile(@NonNull File file) {
        new Dialog(activity,
                new Dialog.Triad("Rename file", "New name", activity.getString(rename)),
                (newName, notification) -> {

                    String name = file.getName();
                    int index = name.lastIndexOf(".");
                    String prefix = index == -1 ? "" : name.substring(index);
                    File destFile = new File(root.getAbsolutePath(), newName + prefix);

                    if (destFile.exists()) {
                        notification.setText(activity.getString(existed_pathname));
                        return false;
                    }

                    if (file.renameTo(destFile)) {
                        updateList();
                        return true;
                    } else {
                        notification.setText(activity.getString(non_valid_name));
                        return false;
                    }
                }).show();
    }

    void deleteFile(@NonNull File file) {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(delete))
                .setMessage("Would you like delete this file?")
                .setNegativeButton(activity.getString(cancel), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(activity.getString(delete), (dialog, which) -> {
                    if (file.exists()) {
                        if (file.delete())
                            updateList();
                        else
                            Toast.makeText(activity, activity.getString(failed_delete), LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }).show();
    }

    void copyFile(@NonNull File file) {
        new Dialog(activity,
                new Dialog.Triad("Copy file", "Pathname", activity.getString(copy)),
                (pathname, notification) -> {

                    File copyFile = new File(pathname);
                    if (copyFile.exists()) {
                        notification.setText(activity.getString(existed_pathname));
                        return false;
                    }

                    String name = file.getName();
                    int index = name.lastIndexOf(".");
                    String copyName = copyFile.getName();
                    int copyIndex = copyName.lastIndexOf(".");
                    String prefix = index == -1 ? "" : name.substring(index);
                    String copyPrefix = copyIndex == -1 ? "" : copyName.substring(copyIndex);
                    if (!prefix.equals(copyPrefix)) {
                        notification.setText(activity.getString(R.string.must_same_prefix));
                        return false;
                    }

                    try {
                        if (copyFile.createNewFile()) {
                            FileChannel srcChanel = new FileInputStream(file).getChannel();
                            FileChannel destChanel = new FileOutputStream(file).getChannel();
                            srcChanel.transferTo(0, srcChanel.size(), destChanel);
                            srcChanel.close();
                            destChanel.close();
                            if (root.getAbsolutePath().equals(copyFile.getParent()))
                                updateList();
                            return true;
                        } else {
                            notification.setText(activity.getString(non_valid_pathname));
                            return false;
                        }
                    } catch (IOException e) {
                        notification.setText(e.getMessage());
                        return false;
                    }
                }).show();
    }

    void moveFile(@NonNull File file) {
        new Dialog(activity,
                new Dialog.Triad("Move file", "Parent pathname", activity.getString(move)),
                (parentPathname, notification) -> {

                    File destFile = new File(parentPathname, file.getName());

                    if (destFile.exists()) {
                        notification.setText(activity.getString(existed_pathname));
                        return false;
                    }
                    if (file.renameTo(destFile)) {
                        updateList();
                        return true;
                    } else {
                        notification.setText(activity.getString(non_valid_pathname));
                        return false;
                    }
                }).show();
    }

    void createTxtFile() {
        new Dialog(activity,
                new Dialog.Triad(activity.getString(create_txt_file), "Name", activity.getString(create)),
                (name, notification) -> {

                    File file = new File(root.getAbsolutePath(), name + ".txt");

                    if (file.exists()) {
                        notification.setText(activity.getString(existed_pathname));
                        return false;
                    }

                    try {
                        if (file.createNewFile()) {
                            updateList();
                            return true;
                        } else {
                            notification.setText(activity.getString(non_valid_name));
                            return false;
                        }
                    } catch (IOException e) {
                        notification.setText(e.getMessage());
                        return false;
                    }
                }).show();
    }

    // All
    @SuppressLint("NotifyDataSetChanged")
    void updateList() {
        list.clear();
        File[] files = root.listFiles();
        if (files != null) {
            list.addAll(Arrays.asList(files));
            Collections.sort(list, (o1, o2) -> {
                if (o1.isDirectory()) {
                    if (o2.isFile()) {
                        return -1;
                    }
                } else {
                    if (o2.isDirectory()) {
                        return 1;
                    }
                }
                return o1.getName().compareTo(o2.getName());
            });
        }
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    void moveRoot(@NonNull String destPathname) {
        File dest = new File(destPathname);
        if (dest.exists()) {
            if (dest.isDirectory()) {
                root = dest;
                list.clear();
                File[] files = root.listFiles();
                if (files != null) {
                    list.addAll(Arrays.asList(files));
                    Collections.sort(list, (o1, o2) -> {
                        if (o1.isDirectory()) {
                            if (o2.isFile()) {
                                return -1;
                            }
                        } else {
                            if (o2.isDirectory()) {
                                return 1;
                            }
                        }
                        return o1.getName().compareTo(o2.getName());
                    });
                }
                adapter.notifyDataSetChanged();
                textView.setText(root.getAbsolutePath());
            }
        }
    }
}
