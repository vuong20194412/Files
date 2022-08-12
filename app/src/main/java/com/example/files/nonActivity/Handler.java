package com.example.files.nonActivity;

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
import static com.example.files.R.string.must_same_prefix;
import static com.example.files.R.string.non_valid_name;
import static com.example.files.R.string.non_valid_pathname;
import static com.example.files.R.string.rename;
import static com.example.files.R.string.type_file_non_open;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class Handler {

    private final AtomicBoolean canClick = new AtomicBoolean(true);
    private final Activity activity;
    private final String storageDirPath;
    private final int REQUEST_CODE_HANDLER;
    private TextView textView;
    private File root;
    private final java.util.List<File> list = new java.util.ArrayList<>();
    private final Dialog dialog;

    private class Adapter extends BaseAdapter {

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
                        if (canClick.get()) {
                            canClick.set(false);
                        String name = f.getName();
                        int beginIndex = name.length() - 4;
                        if (beginIndex >= 0) {
                            String pathname = f.getAbsolutePath();
                            Intent intent;
                            switch (name.substring(beginIndex)) {
                                case ".txt":
                                    intent = new Intent(activity, com.example.files.DisplayingTxtActivity.class);
                                    intent.putExtra(activity.getString(intent_pathname), pathname);
                                    intent.putExtra("idView", v.getId());
                                    activity.startActivityForResult(intent, REQUEST_CODE_HANDLER);
                                    return;
                                case ".jpg":
                                case ".png":
                                    intent = new Intent(activity, com.example.files.DisplayingPngAndJpgActivity.class);
                                    intent.putExtra(activity.getString(intent_pathname), pathname);
                                    activity.startActivityForResult(intent, REQUEST_CODE_HANDLER);
                                    return;
                            }
                        }
                        Toast.makeText(activity,
                                activity.getString(type_file_non_open), LENGTH_SHORT).show();
                        canClick.set(true);
                    }
                    }
                });
            }
        }
    }

    private final Adapter adapter = new Adapter();

    public Handler(@NonNull Activity activity, String storageDirPath, int REQUEST_CODE_HANDLER) {
        this.activity = activity;
        this.storageDirPath = storageDirPath;
        this.REQUEST_CODE_HANDLER = REQUEST_CODE_HANDLER;
        this.dialog = new Dialog(this.activity);
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public void setCanClick(boolean canClick) {
        this.canClick.set(canClick);
    }

    public Adapter getAdapter() {
        return adapter;
    }

    @androidx.annotation.Nullable
    public String getParentPathName() {
        if (root.getAbsolutePath().equals(storageDirPath)) {
            return null;
        }
        return root.getParent();
    }

    // Directory
    public void renameDirectory(@NonNull File directory) {
        dialog.setTitle("Rename directory");
        dialog.setHint("New name");
        dialog.setPosButtonText(activity.getString(rename));
        dialog.setListener((newName, notification) -> {

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
        });

        dialog.show();
    }

    public void deleteDirectory(@NonNull File directory) {
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

    public void createDirectory() {
        dialog.setTitle(activity.getString(create_directory));
        dialog.setHint("Name");
        dialog.setPosButtonText(activity.getString(create));
        dialog.setListener((name, notification) -> {

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

        });

        dialog.show();
    }

    // File
    public void renameFile(@NonNull File file) {
        dialog.setTitle("Rename file");
        dialog.setHint("New name");
        dialog.setPosButtonText(activity.getString(rename));
        dialog.setListener((newName, notification) -> {

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
        });

        dialog.show();
    }

    public void deleteFile(@NonNull File file) {
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

    public void copyFile(@NonNull File file) {
        dialog.setTitle("Copy file");
        dialog.setHint("Pathname");
        dialog.setPosButtonText(activity.getString(copy));
        dialog.setListener((pathname, notification) -> {

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
                notification.setText(activity.getString(must_same_prefix));
                return false;
            }

            try {
                if (copyFile.createNewFile()) {
                    FileChannel srcChanel = new java.io.FileInputStream(file).getChannel();
                    FileChannel destChanel = new java.io.FileOutputStream(file).getChannel();
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
        });

        dialog.show();
    }

    public void moveFile(@NonNull File file) {
        dialog.setTitle("Move file");
        dialog.setHint("Parent pathname");
        dialog.setPosButtonText(activity.getString(move));
        dialog.setListener((parentPathname, notification) -> {

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
        });

        dialog.show();
    }

    public void createTxtFile() {
        dialog.setTitle(activity.getString(create_txt_file));
        dialog.setHint("Name");
        dialog.setPosButtonText(activity.getString(create));
        dialog.setListener((name, notification) -> {
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
        });

        dialog.show();
    }

    public void moveRoot(@NonNull String destPathname) {
        File dest = new File(destPathname);

        if (!dest.exists()) {
            return;
        }

        if (!dest.isDirectory()) {
            return;
        }

        root = dest;

        updateList();

        textView.setText(root.getAbsolutePath());
    }

    @android.annotation.SuppressLint("NotifyDataSetChanged")
    void updateList() {
        list.clear();

        File[] files = root.listFiles();
        if (files != null) {
            list.addAll(java.util.Arrays.asList(files));
            java.util.Collections.sort(list, (o1, o2) -> {
                if (o1.isDirectory()) {
                    if (o2.isFile()) {
                        return -1;
                    }
                } else if (o2.isDirectory()) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());
            });
        }

        adapter.notifyDataSetChanged();
    }
}
