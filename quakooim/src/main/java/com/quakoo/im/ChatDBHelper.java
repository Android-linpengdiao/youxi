package com.quakoo.im;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.base.BaseApplication;
import com.base.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ChatDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "ChatDBHelper";

    private final static String DB_NAME = "User.db";
    private final static int DB_VERSION = 4;
    private static volatile ChatDBHelper sInstance;

    private final Context mContext;

    public ChatDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public synchronized static ChatDBHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ChatDBHelper.class) {
                if (sInstance == null) {
                    sInstance = new ChatDBHelper(context);
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: ");
//        db.execSQL("CREATE TABLE im_message_center (_id INTEGER PRIMARY KEY AUTOINCREMENT,user VARCHAR,fromname VARCHAR,textContent VARCHAR)");
        createChatTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: "+oldVersion);
//        if (oldVersion == 1) {
//            createChatTable(db);
//        }
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE chat_history ADD msg_redopen_state VARCHAR");
            db.execSQL("ALTER TABLE chat_recent ADD msg_redopen_state VARCHAR");
        }else if(oldVersion == 2){
            db.execSQL("ALTER TABLE chat_history ADD msg_receive_state VARCHAR"); //在聊天记录里新增 "//红包领取情况 //已领取 //已被领完"
            db.execSQL("ALTER TABLE chat_recent ADD msg_receive_state VARCHAR"); //在聊天列表记录里新增 "//红包领取情况 //已领取 //已被领完"
            db.execSQL("ALTER TABLE chat_collect ADD msg_receive_state VARCHAR");//在聊天收藏里新增 "//红包领取情况 //已领取 //已被领完"

            ContentValues values = new ContentValues();
            values.put("msg_send_state", Constants.SEND_CHATMESSAGE_SUCCESS);
            db.update("chat_history", values, "msg_send_state=?", new String[]{"succeed"});
            db.update("chat_recent", values, "msg_send_state=?", new String[]{"succeed"});

            db.execSQL("CREATE TABLE chat_setting (_id INTEGER PRIMARY KEY AUTOINCREMENT," +   //新建聊天设置表
                    "username VARCHAR," + // 用户
                    "friend VARCHAR," + // 好友
                    "screenshot_notice VARCHAR," + // 截屏通知
                    "destroy_msg VARCHAR," + // 阅后即焚
                    "shield_msg VARCHAR," + // 消息免打扰
                    "chat_theme VARCHAR)");  // 聊天背景
        }else if (oldVersion == 3) {
            db.execSQL("ALTER TABLE chat_setting ADD chat_top VARCHAR");
        }


    }

    private void createChatTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE chat_history (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR," + // 用户
                "msg_from VARCHAR," + // 消息发送方
                "msg_fromnick VARCHAR," + // 消息发送方昵称
                "msg_fromremark VARCHAR," + // 消息发送方备注
                "msg_to VARCHAR," +   // 消息接收方
                "msg_tonick VARCHAR," +   // 消息接收方昵称
                "msg_toremark VARCHAR," +   // 消息接收方备注
                "msg_content VARCHAR," + // 消息内容
                "msg_type VARCHAR," +  // 消息类型 chat 或者 groupchat
                "msg_time VARCHAR," + // 消息时间戳
                "msg_serveId VARCHAR," + // 消息服务id
                "msg_clientId VARCHAR," + // 消息客户id
                "msg_avatar VARCHAR," + // 消息来源的头像
                "msg_nickname VARCHAR," + // 消息来源的昵称
                "msg_userremark VARCHAR," + // 消息来源的备注
                "msg_username VARCHAR," + // 消息来源的用户名
                "msg_usericon VARCHAR," + // 消息来源的用户icon
                "msg_media_url VARCHAR," + // 多媒体消息url
                "msg_content_type VARCHAR," + // 消息内容类型
                "msg_media_extra VARCHAR," + // 多媒体消息额外信息
                "msg_media_thumbnail VARCHAR," + // 多媒体消息缩略图
                "msg_audio_state VARCHAR," + // 语音消息状态read或unread
                "msg_hint_state VARCHAR," + // @好友消息状态read或unread
                "msg_progress VARCHAR," + // 消息发送中
                "msg_prompt VARCHAR," + // 消息发送失败标识
                "msg_destroy VARCHAR," + // 消息是否阅后即焚
                "msg_send_state VARCHAR," + // 消息发送状态
                "msg_redopen_state VARCHAR," + // 红包打开状态
                "msg_receive_state VARCHAR," + // //红包领取情况 //已领取 //已被领完
                "msg_state VARCHAR)");  // read或者是unread

        // 最近聊天记录
        db.execSQL("CREATE TABLE chat_recent (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR," + // 用户
                "msg_from VARCHAR," + // 消息发送方
                "msg_fromnick VARCHAR," + // 消息发送方昵称
                "msg_fromremark VARCHAR," + // 消息发送方备注
                "msg_to VARCHAR," +   // 消息接收方
                "msg_tonick VARCHAR," +   // 消息接收方昵称
                "msg_toremark VARCHAR," +   // 消息接收方备注
                "msg_content VARCHAR," + // 消息内容
                "msg_type VARCHAR," +  // 消息类型 chat 或者 groupchat
                "msg_time VARCHAR," + // 消息时间戳
                "msg_serveId VARCHAR," + // 消息服务id
                "msg_clientId VARCHAR," + // 消息客户id
                "msg_avatar VARCHAR," + // 消息来源的头像
                "msg_nickname VARCHAR," + // 消息来源的昵称
                "msg_userremark VARCHAR," + // 消息来源的备注
                "msg_username VARCHAR," + // 消息来源的用户名
                "msg_usericon VARCHAR," + // 消息来源的用户icon
                "msg_unread_count VARCHAR," + // 未读消息数量
                "msg_media_url VARCHAR," + // 多媒体消息url
                "msg_content_type VARCHAR," + // 消息内容类型
                "msg_media_extra VARCHAR," + // 多媒体消息额外信息
                "msg_media_thumbnail VARCHAR," + // 多媒体消息缩略图
                "msg_audio_state VARCHAR," + // 语音消息状态read或unread
                "msg_hint_state VARCHAR," + // @好友消息状态read或unread
                "msg_progress VARCHAR," + // 消息发送中
                "msg_prompt VARCHAR," + // 消息发送失败标识
                "msg_destroy VARCHAR," + // 消息是否阅后即焚
                "msg_send_state VARCHAR," + // 消息发送状态
                "msg_redopen_state VARCHAR," + // 红包打开状态
                "msg_receive_state VARCHAR," + // //红包领取情况 //已领取 //已被领完
                "msg_state VARCHAR)");  // read或者是unread

        // 聊天收藏
        db.execSQL("CREATE TABLE chat_collect (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR," + // 用户
                "msg_from VARCHAR," + // 消息发送方
                "msg_fromnick VARCHAR," + // 消息发送方昵称
                "msg_fromremark VARCHAR," + // 消息发送方备注
                "msg_to VARCHAR," +   // 消息接收方
                "msg_tonick VARCHAR," +   // 消息接收方昵称
                "msg_toremark VARCHAR," +   // 消息接收方备注
                "msg_content VARCHAR," + // 消息内容
                "msg_type VARCHAR," +  // 消息类型 chat 或者 groupchat
                "msg_time VARCHAR," + // 消息时间戳
                "msg_serveId VARCHAR," + // 消息服务id
                "msg_clientId VARCHAR," + // 消息客户id
                "msg_avatar VARCHAR," + // 消息来源的头像
                "msg_nickname VARCHAR," + // 消息来源的昵称
                "msg_userremark VARCHAR," + // 消息来源的备注
                "msg_username VARCHAR," + // 消息来源的用户名
                "msg_usericon VARCHAR," + // 消息来源的用户icon
                "msg_unread_count VARCHAR," + // 未读消息数量
                "msg_media_url VARCHAR," + // 多媒体消息url
                "msg_content_type VARCHAR," + // 消息内容类型
                "msg_media_extra VARCHAR," + // 多媒体消息额外信息
                "msg_media_thumbnail VARCHAR," + // 多媒体消息缩略图
                "msg_audio_state VARCHAR," + // 语音消息状态read或unread
                "msg_hint_state VARCHAR," + // @好友消息状态read或unread
                "msg_progress VARCHAR," + // 消息发送中
                "msg_prompt VARCHAR," + // 消息发送失败标识
                "msg_destroy VARCHAR," + // 消息是否阅后即焚
                "msg_send_state VARCHAR," + // 消息发送状态
                "msg_redopen_state VARCHAR," + // 红包打开状态
                "msg_receive_state VARCHAR," + // //红包领取情况 //已领取 //已被领完
                "msg_state VARCHAR)");  // read或者是unread

        // 聊天设置
        db.execSQL("CREATE TABLE chat_setting (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR," + // 用户
                "friend VARCHAR," + // 好友
                "screenshot_notice VARCHAR," + // 截屏通知
                "destroy_msg VARCHAR," + // 阅后即焚
                "shield_msg VARCHAR," + // 消息免打扰
                "chat_top VARCHAR," + // 消息置顶
                "chat_theme VARCHAR)");  // 聊天背景
    }

    private final static String EMOTION_DB_NAME = "emotion.db"; //表情数据库
    private final static String RABBIT_DB_NAME = "rabbit.db";

    public static SQLiteDatabase getEmotionDatabase() {
        try {
            String dbPath = BaseApplication.getInstance().getDatabasePath(EMOTION_DB_NAME).getParent();
            File dbPathDir = new File(dbPath);
            if (!dbPathDir.exists()) {
                dbPathDir.mkdirs();
            }

            File dest = new File(dbPathDir, EMOTION_DB_NAME);
            if (!dest.exists()) {
                dest.createNewFile();
                InputStream is = BaseApplication.getInstance().getAssets().open(EMOTION_DB_NAME);
                int size = is.available();
                byte buf[] = new byte[size];
                is.read(buf);
                is.close();
                FileOutputStream fos = new FileOutputStream(dest);
                fos.write(buf);
                fos.close();
            }

            return SQLiteDatabase.openOrCreateDatabase(BaseApplication.getInstance().getDatabasePath(EMOTION_DB_NAME), null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SQLiteDatabase getRabbitDatabase() {
        try {
            String dbPath = BaseApplication.getInstance().getDatabasePath(RABBIT_DB_NAME).getParent();
            File dbPathDir = new File(dbPath);
            if (!dbPathDir.exists()) {
                dbPathDir.mkdirs();
            }

            File dest = new File(dbPathDir, RABBIT_DB_NAME);
            if (!dest.exists()) {
                dest.createNewFile();
                InputStream is = BaseApplication.getInstance().getAssets().open(RABBIT_DB_NAME);
                int size = is.available();
                byte buf[] = new byte[size];
                is.read(buf);
                is.close();
                FileOutputStream fos = new FileOutputStream(dest);
                fos.write(buf);
                fos.close();
            }

            return SQLiteDatabase.openOrCreateDatabase(BaseApplication.getInstance().getDatabasePath(RABBIT_DB_NAME), null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}