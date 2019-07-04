package com.example.viewpager.download.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.viewpager.download.enties.DownloadInfo;

import java.util.ArrayList;
import java.util.List;

public class MainDAO {

    private SQLiteHelper mHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private static final String TABLE_NAME = "video_download_info";

    public MainDAO(Context context) {
        mHelper = new SQLiteHelper(context, "video_db", null, 1);
        mSQLiteDatabase = mHelper.getWritableDatabase();
    }

    public List<DownloadInfo> queryDownloadInfo(List<String> urlList) {
        String sql = "select url, state, progress, path from " + TABLE_NAME;
        Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
        List<DownloadInfo> list = new ArrayList<>();
        for (String url : urlList) {
            DownloadInfo info = new DownloadInfo();
            cursor.moveToPosition(0);
            while (cursor.moveToNext()) {
                String u = cursor.getString(cursor.getColumnIndex("url"));
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
        return list;
    }

    public void updateDownloadInfoByUrl(DownloadInfo info) {
        String sql = "update " + TABLE_NAME + " set state = " + info.getState() + ", progress = " + info.getProgress() + ", path = '" + info.getPath() + "'";
        mSQLiteDatabase.execSQL(sql);
    }
}
