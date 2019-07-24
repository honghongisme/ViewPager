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

    public static String createFile(String dirPath, String url) {
        String path = dirPath + "/" + getFileName(url);
        return createFile(path);
    }

    public static String createFile(String path) {
        File file = new File(path);
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

    public static long getFileLength(String path) {
        File file = new File(path);
        if (file.exists()) {
            System.out.println("文件长度 = " + file.length());
            return file.length();
        }
        return 0;
    }

    public static boolean isExistFile(String path) {
        File file = new File(path);
        return file.exists();
    }
}
