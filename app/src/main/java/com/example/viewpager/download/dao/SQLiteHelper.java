package com.example.viewpager.download.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.viewpager.download.dao.DownloadInfoDao.DOWNLOAD_PATH;
import static com.example.viewpager.download.dao.DownloadInfoDao.DOWNLOAD_STATE;
import static com.example.viewpager.download.dao.DownloadInfoDao.DOWNLOAD_URL;
import static com.example.viewpager.download.dao.DownloadInfoDao.TABLE_NAME;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(@androidx.annotation.Nullable Context context, @androidx.annotation.Nullable String name, @androidx.annotation.Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists " + TABLE_NAME + "(" +
                DOWNLOAD_URL + " varchar(200) primary key, " +
                DOWNLOAD_STATE + " int default 0, " +
                DOWNLOAD_PATH + " varchar(200))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
