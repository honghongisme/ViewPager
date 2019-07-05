package com.example.viewpager.download.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtil {

    public static String getExternalStoragePath(Context context, String fileName) {
        File file = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/" + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private static void deleteFile(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void deleteFiles(List<String> paths) {
        for (String path : paths) {
            deleteFile(path);
        }
    }
}
