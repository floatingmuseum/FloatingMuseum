package com.floatingmuseum.androidtest.functions.download;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Floatingmuseum on 2017/3/14.
 */

public class DBUtil {

    private DBHelper dbHelper;

    public DBUtil(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void insertOrUpdate(ThreadInfo info) {
        String insertSql = "insert into thread_info(thread_id,url,start_position,end_position,current_position) values(?,?,?,?,?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(insertSql, new Object[]{info.getId(), info.getUrl(), info.getStartPosition(), info.getEndPosition(), info.getCurrentPosition()});
        db.close();
    }

    public void update(ThreadInfo info) {
        String updateSql = "update thread_info set current_position = ? where thread_id = ? and url=?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(updateSql, new Object[]{info.getCurrentPosition(), info.getId(), info.getUrl()});
        db.close();
    }

    public boolean isExists(String url, int thread_id) {
        String querySql = "select * from thread_info where thread_id=? and url=?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(querySql, new String[]{String.valueOf(thread_id), url});
        boolean isExists = cursor.moveToNext();
        cursor.close();
        db.close();
        return isExists;
    }

    public void delete(ThreadInfo info) {
        String deleteSql = "delete from thread_info where thread_id=? and url=?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(deleteSql, new Object[]{info.getId(), info.getUrl()});
        db.close();
    }

    public List<ThreadInfo> getAllThreadInfo(String url) {
        String queryAllSql = "select * from thread_info where url=?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryAllSql, new String[]{url});
        List<ThreadInfo> threadInfoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            ThreadInfo info = new ThreadInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            info.setStartPosition(cursor.getLong(cursor.getColumnIndex("start_position")));
            info.setEndPosition(cursor.getLong(cursor.getColumnIndex("end_position")));
            info.setCurrentPosition(cursor.getLong(cursor.getColumnIndex("current_position")));
            threadInfoList.add(info);
        }
        cursor.close();
        db.close();
        return threadInfoList;
    }
}
