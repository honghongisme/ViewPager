package com.example.viewpager.download.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtil {

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

    public static String getAbsolutePath(String dirPath, String url) {
        File file = new File(dirPath + "/" + getFileName(url));
        //  创建目录
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 创建文件
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
}
