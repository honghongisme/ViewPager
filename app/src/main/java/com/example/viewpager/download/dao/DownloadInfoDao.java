package com.example.viewpager.download.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.viewpager.download.entity.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

public class DownloadInfoDao {


    private SQLiteDatabase mSQLiteDatabase;
    private static final String TABLE_NAME = "video_download_info";

    public DownloadInfoDao(Context context) {
        SQLiteHelper mHelper = new SQLiteHelper(context, "video_db", null, 1);
        mSQLiteDatabase = mHelper.getWritableDatabase();
    }

    public List<DownloadInfo> queryDownloadInfo(List<String> urlList) {
        String sql = "select url, state, progress, path from " + TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        List<DownloadInfo> list = new ArrayList<>();
        for (String url : urlList) {
            System.out.println("url " + url);
            DownloadInfo info = new DownloadInfo();
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex("url"));
                System.out.println("数据库里url " + u);
                if (u.equals(url)) { // url匹配 获取数据
                    info.setUrl(u);
                    info.setPath(cursor.getString(cursor.getColumnIndex("path")));
                    info.setProgress(cursor.getLong(cursor.getColumnIndex("progress")));
                    info.setState(cursor.getInt(cursor.getColumnIndex("state")));
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
        String sql = "delete from " + TABLE_NAME + " where url = '" + info.getUrl() + "'";
        mSQLiteDatabase.execSQL(sql);
        sql = "insert into " + TABLE_NAME + " values('" + info.getUrl() + "', " + info.getState() + ", " + info.getProgress() + ", '" + info.getPath() + "')";
        mSQLiteDatabase.execSQL(sql);
    }

    public List<String> queryAllDownloadPath() {
        String sql = "select path from " + TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex("path"));
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
