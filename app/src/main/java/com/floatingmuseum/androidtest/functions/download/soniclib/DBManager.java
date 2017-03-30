package com.floatingmuseum.androidtest.functions.download.soniclib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.floatingmuseum.androidtest.functions.download.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Floatingmuseum on 2017/3/30.
 */

public class DBManager {

    private DBHelper dbHelper;

    public DBManager(Context context) {
        dbHelper = DBHelper.getInstance(context);
    }

    public void insertThreadInfo(ThreadInfo info) {
        String insertSql = "insert into thread_info(thread_id,url,start_position,end_position,current_position,file_size) values(?,?,?,?,?,?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(insertSql, new Object[]{info.getId(), info.getUrl(), info.getStartPosition(), info.getEndPosition(), info.getCurrentPosition(), info.getFileSize()});
        db.close();
    }

    public void insertTaskInfo(DownloadTask task) {
        String insertSql = "insert into task_info(url,tag,dir_path,file_path,name,current_size,total_size,state) values(?,?,?,?,?,?,?,?)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(insertSql, new Object[]{task.getDownloadUrl(), task.getTag(), task.getDirPath(), task.getFilePath(), task.getName(), task.getCurrentSize(), task.getTotalSize(), task.getState()});
        db.close();
    }

    public synchronized void updateThreadInfo(ThreadInfo info) {
        String updateSql = "update thread_info set current_position = ? where thread_id = ? and url=?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(updateSql, new Object[]{info.getCurrentPosition(), info.getId(), info.getUrl()});
        db.close();
    }

    public synchronized void updateTaskInfo(DownloadTask task) {
        String updateSql = "update task_info set current_size = ?,state = ? where tag = ?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(updateSql, new Object[]{task.getCurrentSize(), task.getState(), task.getTag()});
        db.close();
    }

    public synchronized void delete(String tableName, String fieldName) {
        String deleteSql = "delete from +" + tableName + " where url=?";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(deleteSql, new Object[]{fieldName});
        db.close();
    }

    public synchronized ThreadInfo queryThreadInfo(int id, String url) {
        String querySql = "select * from thread_info where thread_id=? and url=?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(querySql, new String[]{String.valueOf(id), url});
        if (cursor.moveToNext()) {
            ThreadInfo info = new ThreadInfo();
            info.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            info.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            info.setStartPosition(cursor.getLong(cursor.getColumnIndex("start_position")));
            info.setEndPosition(cursor.getLong(cursor.getColumnIndex("end_position")));
            info.setCurrentPosition(cursor.getLong(cursor.getColumnIndex("current_position")));
            info.setFileSize(cursor.getLong(cursor.getColumnIndex("file_size")));
            cursor.close();
            db.close();
            return info;
        }
        cursor.close();
        db.close();
        return null;
    }

    public synchronized DownloadTask queryDownloadTask(String tag) {
        String querySql = "select * from task_info where tag=?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(querySql, new String[]{tag});
        if (cursor.moveToNext()) {
            DownloadTask task = new DownloadTask();
            task.setDownloadUrl(cursor.getString(cursor.getColumnIndex("url")));
            task.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            task.setName(cursor.getString(cursor.getColumnIndex("name")));
            task.setDirPath(cursor.getString(cursor.getColumnIndex("dir_path")));
            task.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
            task.setCurrentSize(cursor.getLong(cursor.getColumnIndex("current_size")));
            task.setTotalSize(cursor.getLong(cursor.getColumnIndex("total_size")));
            task.setState(cursor.getInt(cursor.getColumnIndex("state")));
            cursor.close();
            db.close();
            return task;
        }
        cursor.close();
        db.close();
        return null;
    }

    public synchronized List<ThreadInfo> getAllThreadInfo(String url) {
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
            info.setFileSize(cursor.getLong(cursor.getColumnIndex("file_size")));
            threadInfoList.add(info);
        }
        cursor.close();
        db.close();
        return threadInfoList;
    }

    public synchronized List<DownloadTask> getAllDownloadTask() {
        String queryAllSql = "select * from task_info";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryAllSql, new String[]{});
        List<DownloadTask> tasks = new ArrayList<>();
        while (cursor.moveToNext()) {
            DownloadTask task = new DownloadTask();
            task.setDownloadUrl(cursor.getString(cursor.getColumnIndex("url")));
            task.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            task.setName(cursor.getString(cursor.getColumnIndex("name")));
            task.setDirPath(cursor.getString(cursor.getColumnIndex("dir_path")));
            task.setFilePath(cursor.getString(cursor.getColumnIndex("file_path")));
            task.setCurrentSize(cursor.getLong(cursor.getColumnIndex("current_size")));
            task.setTotalSize(cursor.getLong(cursor.getColumnIndex("total_size")));
            task.setState(cursor.getInt(cursor.getColumnIndex("state")));
        }
        cursor.close();
        db.close();
        return tasks;
    }

    public synchronized boolean isExists(String url, int thread_id) {
        String querySql = "select * from thread_info where thread_id=? and url=?";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(querySql, new String[]{String.valueOf(thread_id), url});
        boolean isExists = cursor.moveToNext();
        cursor.close();
        db.close();
        return isExists;
    }
}
