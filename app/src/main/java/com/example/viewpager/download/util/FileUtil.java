package com.example.viewpager.download.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static String getExternalStoragePath(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
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

}
