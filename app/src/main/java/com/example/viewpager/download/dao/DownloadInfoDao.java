package com.example.viewpager.download.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.viewpager.download.entity.DownloadInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DownloadInfoDao {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_PATH = "video_db";
    public static final String TABLE_NAME = "video_download_info";
    public static final String DOWNLOAD_URL = "url";
    public static final String DOWNLOAD_STATE = "state";
    public static final String DOWNLOAD_PROGRESS = "progress";
    public static final String DOWNLOAD_PATH = "path";

    private SQLiteDatabase mSQLiteDatabase;


    public DownloadInfoDao(Context context, String databasePath) {
        if (databasePath == null) {
            databasePath = DATABASE_PATH;
        }
        // 指定路径 只要DATABASE_PATH传入具体路径即可
        SQLiteHelper mHelper = new SQLiteHelper(context, databasePath, null, DATABASE_VERSION);
        mSQLiteDatabase = mHelper.getWritableDatabase();
    }

    public List<DownloadInfo> queryDownloadInfo(Set<String> urlList) {
        String sql = "select " + DOWNLOAD_URL + ", " + DOWNLOAD_STATE + ", " + DOWNLOAD_PROGRESS + ", " + DOWNLOAD_PATH + " from " + TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        List<DownloadInfo> list = new ArrayList<>();
        for (String url : urlList) {
            System.out.println("url " + url);
            DownloadInfo info = new DownloadInfo();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex(DOWNLOAD_URL));
                System.out.println("数据库里url " + u);
                if (u.equals(url)) { // url匹配 获取数据
                    info.setUrl(u);
                    info.setPath(cursor.getString(cursor.getColumnIndex(DOWNLOAD_PATH)));
                    info.setProgress(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_PROGRESS)));
                    info.setState(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_STATE)));
                    break;
                }
            }
            info.setUrl(url);
            list.add(info);
        }
        cursor.close();
        return list;
    }

    public void updateDownloadInfoByUrl(DownloadInfo info) {
        String sql = "delete from " + TABLE_NAME + " where " + DOWNLOAD_URL  + " = '" + info.getUrl() + "'";
        mSQLiteDatabase.execSQL(sql);
        sql = "insert into " + TABLE_NAME + " values('" + info.getUrl() + "', " + info.getState() + ", " + info.getProgress() + ", '" + info.getPath() + "')";
        mSQLiteDatabase.execSQL(sql);
    }

    public List<String> queryAllDownloadPath() {
        String sql = "select " + DOWNLOAD_PATH + " from " + TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex(DOWNLOAD_PATH));
            if (path != null) {
                list.add(path);
            }
        }
        cursor.close();
        return list;
    }

    public void deleteAll() {
        String sql = "delete from " + TABLE_NAME ;
        mSQLiteDatabase.execSQL(sql);
    }
}
