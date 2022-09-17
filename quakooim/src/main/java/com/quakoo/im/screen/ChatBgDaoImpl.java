package com.quakoo.im.screen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */

public class ChatBgDaoImpl implements UserDao {

    UserDBHelper dbHelper;

    public ChatBgDaoImpl(Context context) {
        dbHelper = new UserDBHelper(context);
    }

    @Override
    public synchronized void insertThread(User screenInfo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("master", screenInfo.master);
        values.put("user_id", screenInfo.userId);
        values.put("chat_bg", screenInfo.chatBg);
        db.insert("chatbg_user", null, values);
        db.close();
    }

    @Override
    public synchronized void deleteThread(String master, String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int count = db.delete("chatbg_user", "master = ? and user_id = ?", new String[]{master, user_id});
        db.close();
    }

    @Override
    public synchronized void updateThread(String master, String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        db.update("chatbg_user", values, "master = ? and user_id = ? ", new String[]{master, user_id});
    }

    @Override
    public synchronized void updateThread(String master, String user_id, String chat_bg) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("master", master);
        values.put("user_id", user_id);
        values.put("chat_bg", chat_bg);
        Cursor cursor = db.query("chatbg_user", null, "master = ?  and user_id = ?",
                new String[]{master, user_id}, null, null, null);
        if (cursor.moveToNext()) {
            db.update("chatbg_user", values, "id=?", new String[]{cursor.getInt(0) + ""});
        } else {
            db.insert("chatbg_user", null, values);
        }
        cursor.close();
        db.close();
    }

    @Override
    public synchronized List<User> queryThread(String master, String user_id) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("chatbg_user", null, "master = ?  and user_id = ?",
                new String[]{master, user_id}, null, null, null);
        while (cursor.moveToNext()) {
            User threadInfo = new User();
            threadInfo.master = cursor.getString(cursor.getColumnIndex("master"));
            threadInfo.userId = cursor.getString(cursor.getColumnIndex("user_id"));
            threadInfo.chatBg = cursor.getString(cursor.getColumnIndex("chat_bg"));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public synchronized List<User> queryThreadAll(String master) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("chatbg_user", null, "master = ?",
                new String[]{master}, null, null, null);
        while (cursor.moveToNext()) {
            User threadInfo = new User();
            threadInfo.master = cursor.getString(cursor.getColumnIndex("master"));
            threadInfo.userId = cursor.getString(cursor.getColumnIndex("user_id"));
            threadInfo.chatBg = cursor.getString(cursor.getColumnIndex("chat_bg"));
            list.add(threadInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    public synchronized boolean isExists(String master, String user_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("chatbg_user", null, "master = ? and user_id = ?",
                new String[]{master, user_id}, null, null, null);
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }
}