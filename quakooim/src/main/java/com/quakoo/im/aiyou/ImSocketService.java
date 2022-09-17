package com.quakoo.im.aiyou;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.LogUtil;
import com.base.utils.MsgCache;
import com.base.utils.ToastUtils;
import com.google.gson.Gson;
import com.quakoo.im.ChatDBHelper;
import com.quakoo.im.R;
import com.quakoo.im.manager.ChatSettingManager;
import com.quakoo.im.manager.DBManager;
import com.quakoo.im.manager.IMChatManager;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.MainMessage;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.screen.User;
import com.quakoo.im.socket.ImSocketThread;
import com.quakoo.im.utils.ContentUtils;
import com.quakoo.im.utils.NetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;

/**
 * Created by Administrator on 2018/1/3.
 */

/**
 * 用于常规消息的(发送信息\发送信息成功,失败 | 回撤 | 删除 等等)Serivce.音视频类不在这接收(AVChatService) 适用聊天窗口
 */
public class ImSocketService extends Service {

    public static final String IM_SERVICE_PACKAGE = "com.quakoo.im.aiyou.ImSocketService";
    private static final String TAG = IM_SERVICE_PACKAGE;

    private static Object lock = new Object();

    private String master;

    private ImSocketThread socket;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    private Thread thread = null;
    private Boolean isContect = false;

    private Thread queryMagThread = null;
    private Boolean serviceContect = false;

    //主机IP地址
    private static String HOST = "120.55.88.212";//""192.168.101.25";
    ///端口号
    public static final String PORT = "11111";

    //心跳检测时间
    private static final long HEART_BEAT_RATE = 60 * 1000;

    private static final long CONNECT_SERVIE_RATE = 3 * 1000;

    //心跳广播
    public static final int CONNECT_SERVIE_ACTION = 100;
    //心跳广播
    public static final int HEART_BEAT_ACTION = 200;

    public ImSocketReceiver imReceiver;


    private ChatMessage chatMessage = new ChatMessage();

    private LocalBroadcastManager localBroadcastManager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT_SERVIE_ACTION:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!CommonUtil.isBlank(HOST)
                                    && !CommonUtil.isBlank(PORT)
                                    && !CommonUtil.isBlank(master)) {
                                connect();
                            }
                        }
                    }).start();
                    break;
                case HEART_BEAT_ACTION:

                    break;
            }
        }
    };

    public static void startImService(Context context) {
        LogUtil.i(TAG, "startImService: ");
        if (!CommonUtil.isWorked(context, IM_SERVICE_PACKAGE)) {
            UserInfo userinfo = (UserInfo) MsgCache.get(context).getAsObject(Constants.USER_INFO);
            if (!CommonUtil.isBlank(userinfo)) {
                Intent intent = new Intent(context, ImSocketService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                } else {
                    context.startService(intent);
                }
            }
        }
    }

//    private UserDao sdao;
//    private UserDao ddao;

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        EventBus.getDefault().register(this);
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
        EventBus.getDefault().register(this);
        //    需要在创建CHANNEL时确定
        //    NotificationManager.IMPORTANCE_MIN: 静默;
        //    NotificationManager.IMPORTANCE_HIGH:随系统使用声音或振动

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(getPackageName() + ".service", getResources().getString(R.string.app_service),
                    NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(getApplicationContext(), getPackageName() + ".service").build();
            startForeground(Constants.IM_SERVICE_ID, notification);
        }

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        sdao = new ScreenDaoImpl(this);
//        ddao = new DestoryDaoImpl(this);

        imReceiver = new ImSocketReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.SEND_CHATMESSAGE);
        localBroadcastManager.registerReceiver(imReceiver, filter);

        serviceContect = true;
        queryMagThread = new Thread(queryDoThread);
        queryMagThread.start();


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand: ");

        UserInfo userinfo = (UserInfo) MsgCache.get(getApplication()).getAsObject(Constants.USER_INFO);
        if (!CommonUtil.isBlank(userinfo)) {
            master = userinfo.getId();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!CommonUtil.isBlank(HOST)
                        && !CommonUtil.isBlank(PORT)
                        && !CommonUtil.isBlank(master)) {
                    connect();
                }
            }
        }).start();

        return START_REDELIVER_INTENT;
    }

    private void connect() {
        try {
            mHandler.removeCallbacks(heartBeatRunnable);
            socket = ImSocketThread.getInstance();
            LogUtil.i(TAG, " connect: " + HOST + " " + PORT + " " + master);
            if (socket.SocketStart(HOST, PORT, master)) {
                if (socket.isConnected()) {
                    if (sendMsg(1, new ChatMessage())) {
                        thread = new Thread(null, doThread, "Message");
                        thread.start();
                        mHandler.removeCallbacks(heartBeatRunnable);
                        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
                        isContect = true;
                    }
                }
            } else {
                LogUtil.i(TAG, "connect: 重新连接服务器 ");
                handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //吐司
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    // 发送心跳包
    private Handler mHandler = new Handler();

    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            HeartBeatThread thread = new HeartBeatThread();
            thread.start();
        }
    };

    public class HeartBeatThread extends Thread {
        @Override
        public void run() {
            super.run();
            if (sendMsg(1, new ChatMessage())) {
                mHandler.removeCallbacks(heartBeatRunnable);
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
                LogUtil.i(TAG, "run: 发送心跳成功");
            } else {
                mHandler.removeCallbacks(heartBeatRunnable);
                handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
                LogUtil.i(TAG, "run: 发送心跳失败");
            }
        }
    }

    private boolean sendMsg(int n, ChatMessage cm) {
        if (socket == null) {
            return false;
        }
        try {
            if (socket.isConnected()) {
                dis = socket.getDIS();
                dos = socket.getDOS();
                if (n == 1) {
                    LogUtil.i(TAG, "sendMsg: 心跳");
                    try {
                        String currentStreamIndex = CommonUtil.chatMaxStreamIndex(this, master, Constants.GET_MSG_INDEX, null);
                        if (currentStreamIndex.equals("0")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
                            Date cur = new Date();
                            long diff = cur.getTime() - (5 * 1000 * 60 * 60 * 24);
                            String starTime = CommonUtil.getStringToDate(df.format(diff) + "00:00:00");
                            dos.write(ContentUtils.ImHeartbeat(master, parseDouble(starTime)));
                        } else {
                            dos.write(ContentUtils.ImHeartbeat(master, parseDouble(currentStreamIndex)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.i(TAG, "sendMsg: e " + e.getMessage());
                        return false;
                    }
                } else if (n == 2) {
                    LogUtil.i(TAG, "sendMsg: 发消息");
                    try {
                        dos.write(ContentUtils.sendMessage(cm));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.i(TAG, "sendMsg: e " + e.getMessage());
                        return false;
                    }
                } else if (n == 3) {
                    LogUtil.i(TAG, "sendMsg: 发消息");
                    try {
                        dos.write(ContentUtils.sendDeleteMessage(cm));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.i(TAG, "sendMsg: e " + e.getMessage());
                        return false;
                    }
                } else if (n == 6) {
                    LogUtil.i(TAG, "sendMsg: 发消息");
                    try {
                        dos.write(ContentUtils.sendRecallMessage(cm));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.i(TAG, "sendMsg: e " + e.getMessage());
                        return false;
                    }
                }
                isContect = true;
            } else {
                LogUtil.i(TAG, "sendMsg: ");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.i(TAG, "sendMsg: e " + e.getMessage());
            return false;
        }
        return true;
    }

    private Runnable doThread = new Runnable() {
        @Override
        public void run() {
            LogUtil.i(TAG, "run: running! ");
            ReceiveMsg();
        }
    };

    private void ReceiveMsg() {
        byte[] buffer = new byte[1024];
        int ping = 0;
        try {
            // 获取输入流，用来接收数据
            DataInputStream stream = new DataInputStream(socket.getDIS());
            while (true) {

                // 将要接收的数据的长度
                final int dataLen = stream.readInt();
                LogUtil.i(TAG, "ReceiveMsg: dataLen " + dataLen);
                if (dataLen > 5 * 1024 * 1024)
                    throw new IllegalStateException("oom");
                // 缓冲区尺寸验证
                if (buffer.length < dataLen) {
                    buffer = new byte[dataLen];
                }
                // 从第0字节开始接收
                int offset = 0;

                // 接收数据
                while (offset < dataLen) {
                    int len = stream.read(buffer, offset, dataLen - offset);
                    if (len == -1) {
                        break;
                    }
                    offset += len;
                }
                // 转化为json格式，
                String command = new String(buffer, 0, offset, "UTF-8");
                try {
                    LogUtil.i(TAG, "ReceiveMsg: command " + command);
                    JSONObject obj = new JSONObject(command);
                    int type = obj.getInt("type");
                    boolean success = obj.getBoolean("success");
                    if (type == 1) {
                        if (success) {
                            LogUtil.i(TAG, "ReceiveMsg: 心跳回执成功\n ");
                            ping = 0;
                        } else {
                            ping += ping;
                            LogUtil.i(TAG, "ReceiveMsg: 心跳回执失败 ： " + "\n ping " + ping);
                        }
                        if (ping < 3) {
//                            mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
                        } else {
                            handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
                        }
                    } else if (type == 2) {
                        if (success) {
                            LogUtil.i(TAG, "ReceiveMsg: 发送消息回执成功\n ");
                            String clientId = obj.optString("clientId");
                            IMChatManager.getInstance(getApplicationContext()).updateSendServiceSuccess(master, clientId);
                            sendBroadcast(chatMessage, Constants.SEND_CHATMESSAGE_SUCCESS);
                        } else {
                            LogUtil.i(TAG, "ReceiveMsg: 发送消息回执失败 ： ");
                            ToasthowShort(obj.optString("errMsg"));
                            sendBroadcast(chatMessage, Constants.SEND_CHATMESSAGE_FAIL);
                        }
                    } else if (type == 3) {
                        if (success) {
                            String mid = obj.optString("mid");
                            sendBroadcast(mid, Constants.SEND_DELETEMSG_SUCCESS);
                            LogUtil.i(TAG, "ReceiveMsg: 删除消息回执成功\n ");
                        } else {
                            ToasthowShort("删除消息失败");
                            LogUtil.i(TAG, "ReceiveMsg: 删除消息回执失败 ： ");
                        }
                    } else if (type == 5) {
                        if (success) {
                            LogUtil.i(TAG, "ReceiveMsg: 新消息接受成功\n ");
                            JSONObject result = obj.getJSONObject("result");
                            ConnectResponse(result);
                        } else {
                            LogUtil.i(TAG, "ReceiveMsg: 新消息接受失败 ： ");
                        }
                    } else if (type == 6) {
                        if (success) {
                            LogUtil.i(TAG, "ReceiveMsg: 撤回消息回执成功\n ");
//                            JSONObject result = obj.getJSONObject("result");
//                            ConnectResponse(result);
                        } else {
                            LogUtil.i(TAG, "ReceiveMsg: 撤回消息回执失败 ： ");
                        }
                    } else if (type == 7) {//账号被挤掉
                        LogUtil.i(TAG, "ReceiveMsg: 账号被挤掉\n ");
                        sendBroadcast(Constants.SOCKET_USER_LOGOUT);
                    } else if (type == 10) {
                        if (success) {
//                            String clientId = obj.optString("clientId");
//                            IMChatManager.getInstance(getApplicationContext()).updateSendServiceSuccess(master, clientId);
                        } else {
                            ToasthowShort(obj.optString("errMsg"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.i(TAG, "ReceiveMsg: e json " + e.getMessage());
                    handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            LogUtil.i(TAG, "ReceiveMsg: SocketException " + e.getMessage());
            handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.i(TAG, "ReceiveMsg: IOException " + e.getMessage());
            handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            LogUtil.i(TAG, "ReceiveMsg: OutOfMemoryError " + e.getMessage());
            handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
            throw new OutOfMemoryError("oom");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i(TAG, "ReceiveMsg: Exception " + e.getMessage());
            handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
        }
    }

    private void ConnectResponse(JSONObject result) {
        synchronized (lock) {
            String maxStreamIndex = result.optString("maxStreamIndex");
            String currentStreamIndex = CommonUtil.chatMaxStreamIndex(this, master, Constants.GET_MSG_INDEX, null);
            LogUtil.i(TAG, "ConnectResponse: " + maxStreamIndex + "===" + currentStreamIndex);
            if (!maxStreamIndex.equals(currentStreamIndex)) {
//                CommonUtil.chatMaxStreamIndex(this, master, Constants.SET_MSG_INDEX, "" + maxStreamIndex);
                List<ChatMessage> chatMessages = IMChatManager.getInstance(getApplicationContext()).getNewChatMsg10(master);
                try {
                    JSONArray array = result.optJSONArray("streams");
                    for (int i = 0; i < array.length(); i++) {
                        List<ChatMessage> allMsgList = new ArrayList<ChatMessage>();
                        List<ChatMessage> recallMsgList = new ArrayList<ChatMessage>();
//                        List<ChatMessage> revMsgList = new ArrayList<ChatMessage>();
//                        List<ChatMessage> sendMsgList = new ArrayList<ChatMessage>();
                        JSONObject obj = array.optJSONObject(i);
                        String uid = obj.optString("uid");
                        String type = obj.optString("type");
                        String thirdId = obj.optString("thirdId");
                        String thirdNick = obj.optString("thirdNick");
                        String thirdIcon = obj.optString("thirdIcon");
                        String maxIndex = obj.optString("maxIndex");
                        JSONArray data = obj.optJSONArray("data");
                        for (int j = 0; j < data.length(); j++) {
                            boolean screen = true;
                            JSONObject content = data.optJSONObject(j);
                            String id = content.optString("id");
                            String authorId = content.optString("authorId");
                            String authorNick = content.optString("authorNick");
                            String authorRemark = null;
                            if (!CommonUtil.wordIsBlank(content.optString("authorRemark"))) {
                                authorRemark = content.optString("authorRemark");
                            }
                            String authorIcon = content.optString("authorIcon");
                            String clientId = content.optString("clientId");
                            String msgType = content.optString("type");
                            String time = content.optString("time");
                            String index = content.optString("index");
                            JSONObject messageChat = content.optJSONObject("messageChat");
                            JSONObject messageNotice = content.optJSONObject("messageNotice");
                            ChatMessage cm = new ChatMessage();
                            cm.setMaster(uid);
                            cm.setTime(CommonUtil.getDateToString(time));
                            cm.setServeId(id);
                            cm.setClientId(clientId);
                            if (!CommonUtil.isBlank(messageChat)) {
                                String word = messageChat.optString("word");
                                String picture = messageChat.optString("picture");
                                String voice = messageChat.optString("voice");
                                String voiceDuration = messageChat.optString("voiceDuration");
                                String video = messageChat.optString("video");
                                String videoDuration = messageChat.optString("videoDuration");
                                String ext = messageChat.optString("ext");
//                                ChatMessage cm = new ChatMessage();
//                                cm.setMaster(uid);
//                                cm.setTime(CommonUtil.getDateToString(time));
//                                cm.setServeId(id);
//                                cm.setClientId(clientId);

                                if (!CommonUtil.wordIsBlank(word)) {
                                    cm.setContent(word);
                                    cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_TXT);
                                    cm.setExtra(ext);
                                } else if (!CommonUtil.isBlank(video)) {
                                    cm.setThumbnailUrl(picture);
                                    cm.setUrl(video);
                                    cm.setExtra(videoDuration);
                                    cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_VIDEO);
                                } else if (!CommonUtil.isBlank(picture)) {
                                    if (!CommonUtil.isPicture(picture)) {
                                        cm.setThumbnailUrl(picture);
                                    }
                                    cm.setUrl(picture);
                                    cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_PIC);
                                } else if (!CommonUtil.isBlank(voice)) {
                                    cm.setUrl(voice);
                                    cm.setExtra(voiceDuration);
                                    cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_AUDIO);
                                } else if (!CommonUtil.isBlank(ext)) {
                                    JSONObject json = new JSONObject(ext);
                                    String ext_type = json.optString("type");
                                    if (!CommonUtil.isBlank(json.optString("extra"))) {
                                        try {
                                            JSONObject extra = new JSONObject(json.optString("extra"));
                                            if (ext_type.equals("1")) {
                                                cm.setContent(extra.optString("title"));
                                                cm.setExtra(extra.optString("redId"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_RED);
                                            } else if (ext_type.equals("2")) { //修改成通知类型
                                                cm.setContent(extra.optString("content"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
                                                cm.setExtra(ext);
                                            } else if (ext_type.equals("3")) {//
                                                cm.setContent(extra.optString("content"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
                                                cm.setExtra(ext);
                                                //是否显示截屏通知
                                                if (ChatSettingManager.getInstance().query(master, thirdId).getScreenshot()) {
                                                    screen = true;
                                                } else {
                                                    screen = false;
                                                    continue;
                                                }
                                            } else if (ext_type.equals("4")) {//
                                                cm.setContent(extra.optString("content"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
                                                cm.setExtra(ext);
                                            } else if (ext_type.equals("5")) {
                                                cm.setContent(extra.optString("content"));
                                                cm.setUrl(extra.optString("picture"));
                                                cm.setExtra(json.optString("extra"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_LOCATION);
                                            } else if (ext_type.equals("6")) {
//                                            cm.setContent(extra.optString("content"));
//                                            cm.setUrl(extra.optString("icon"));
//                                            cm.setExtra(extra.optString("uid"));
                                                cm.setExtra(extra.toString());
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NAMECARD);
                                            } else if (ext_type.equals("100")) {//
                                                cm.setContent("撤回了一条消息");
                                                cm.setServeId(extra.optString("mid"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG);
                                            } else if (ext_type.equals("7")) {
//                                            cm.setUrl(extra.optString("url"));
//                                            cm.setContent(extra.optString("title"));
//                                            cm.setExtra(extra.optString("desc"));
//                                            cm.setThumbnailUrl(extra.optString("thumUrl"));
                                                cm.setExtra(extra.toString());
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_SHARE);
                                            } else if (ext_type.equals("8")) {//
                                                JSONObject jsonObject = new JSONObject(json.optString("extra"));
                                                if (!jsonObject.optString("uid").equals(master)) {
                                                    continue;
                                                }
                                                cm.setContent(extra.optString("title"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
                                                cm.setExtra(ext);
                                            } else if (ext_type.equals("9")) {
                                                cm.setContent(extra.optString("title"));
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
                                                cm.setExtra(ext);
                                            } else if (ext_type.equals("16")) {

                                                String OtherList = extra.optString("OtherList");
                                                String[] uids = OtherList.split(",");
                                                cm.setContent(extra.optString("name") + "想邀请" + uids.length + "位朋友加入了群");
                                                cm.setExtra(ext);
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
                                                if (extra.optString("uid").equals(master)) {
                                                    ToasthowShort("发送成功，等待群主审核");
                                                }
                                                //邀人进群 群主审核 审核人都不是当前登录人,就不需要接受消息了
                                                if (!extra.optString("GMid").equals(master)) {
                                                    continue;
                                                }

                                            } else if (ext_type.equals("10")) {
                                                cm.setContent(extra.optString("name"));
                                                cm.setExtra(extra.toString());
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_SHARE_PLUGIND);
                                            } else if (ext_type.equals("11")) {
                                                cm.setUrl(extra.optString("url"));
                                                cm.setExtra(extra.toString());
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_EMOTION);
                                            } else if (ext_type.equals("17")) {
                                                cm.setExtra(extra.toString());
                                                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_PRODUCT);
                                            }
                                        } catch (JSONException e) {
                                            cm.setContent("【消息" + id + "格式不兼容】");
                                            cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_TXT);
                                            cm.setExtra(ext);
                                        }
                                    } else {
                                        cm.setContent("【消息" + id + "格式不兼容】");
                                        cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_TXT);
                                        cm.setExtra(ext);
                                    }
                                } else {
                                    cm.setContent("【消息" + id + "格式不兼容】");
                                    cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_TXT);
                                    cm.setExtra(ext);
                                }

                            } else if (!CommonUtil.isBlank(messageNotice)) {
                                String title = messageNotice.optString("title");
                                String desc = messageNotice.optString("desc");
                                String cover = messageNotice.optString("cover");
                                String redirect = messageNotice.optString("redirect");
                                cm.setContent(title);
                                cm.setExtra(messageNotice.toString());
                            }

                            if (type.equals("1")) {
                                cm.setType(Constants.FRAGMENT_FRIEND);
                            } else if (type.equals("2")) {
                                cm.setType(Constants.FRAGMENT_GROUP);
                            } else if (type.equals("3")) {
                                cm.setType(Constants.FRAGMENT_NOTICE);
                            }
                            cm.setAvatar(authorIcon);
                            cm.setSendMsgState(Constants.SEND_CHATMESSAGE_SUCCESS);
                            ChatSettingEntity users = ChatSettingManager.getInstance().query(master, thirdId);
//                            List<User> users = DBManager.getInstance().getDestory().queryThreadAll(master + "");
                            List<User> blackUser = DBManager.getInstance().getBlacklist().queryThread(master, authorId);
                            if (!uid.equals(authorId)) {
                                cm.setFromuser(thirdId);
                                cm.setFromusernick(thirdNick);
                                cm.setTouser(uid);
                                cm.setTousernick(uid);
                                cm.setUsername(authorId);
                                cm.setNickname(authorNick);
                                cm.setUserremark(authorRemark);
                                cm.setUserIcon(thirdIcon);
                                cm.setMsgState("unread");
                                cm.setAudioState("unread");
                                //
//                                if (users.size() > 0) {
//                                    for (User info : users) {
//                                        if (thirdId.equals(info.userId)) {
//                                            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)
//                                                    || cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
//                                                cm.setDestroy("false");
//                                            } else {
//                                                cm.setDestroy("true");
//                                            }
//                                            break;
//                                        }
//                                    }
//                                }
                                if (users.getDestroy()) {
                                    if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)
                                            || cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
                                        cm.setDestroy("false");
                                    } else {
                                        cm.setDestroy("true");
                                    }
                                }
                                if (screen && blackUser.size() == 0) {
//                                revMsgList.add(cm);
                                    cm.setMsgFrom("2");
                                    if (chatMessages.size() > 0) {
                                        boolean same = false;
                                        for (ChatMessage msg : chatMessages) {
                                            if (!cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                                                if (msg.getServeId().equals(cm.getServeId())) {
                                                    same = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!same) {
                                            allMsgList.add(cm);
                                        }
                                    } else {
                                        allMsgList.add(cm);
                                    }
//                                    EventBus.getDefault().postSticky(cm);
                                }
                            } else {
                                cm.setFromuser(uid);
                                cm.setFromusernick(uid);
                                cm.setTouser(thirdId);
                                cm.setTousernick(thirdNick);
                                cm.setUsername(authorId);
                                cm.setNickname(authorNick);
                                cm.setUserremark(authorRemark);
                                cm.setUserIcon(thirdIcon);
                                cm.setMsgState("read");
                                cm.setPrompt("false");
                                cm.setSendMsgState(Constants.SEND_CHATMESSAGE_SUCCESS);
                                //
//                                if (users.size() > 0) {
//                                    for (User info : users) {
//                                        if (thirdId.equals(info.userId)) {
//                                            if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)
//                                                    || cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
//                                                cm.setDestroy("false");
//                                            } else {
//                                                cm.setDestroy("true");
//                                            }
//                                            break;
//                                        }
//                                    }
//                                }
                                if (users.getDestroy()) {
                                    if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)
                                            || cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
                                        cm.setDestroy("false");
                                    } else {
                                        cm.setDestroy("true");
                                    }
                                }
                                if (screen) {
//                                sendMsgList.add(cm);
                                    cm.setMsgFrom("1");
                                    if (chatMessages.size() > 0) {
                                        boolean same = false;
                                        for (ChatMessage msg : chatMessages) {
                                            if (!cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                                                if (msg.getServeId().equals(cm.getServeId())) {
                                                    same = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!same) {
                                            allMsgList.add(cm);
                                        }
                                    } else {
                                        allMsgList.add(cm);
                                    }
                                }
                            }
                        }

                        if (allMsgList.size() > 0) {
                            List<ChatMessage> revMsgList = new ArrayList<ChatMessage>();
//                            List<ChatMessage> sendMsgList = new ArrayList<ChatMessage>();
                            for (int k = allMsgList.size() - 1; k >= 0; k--) {
                                if (allMsgList.get(k).getMsgFrom().equals("2")) {//接收消息
                                    if (allMsgList.get(k).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                                        recallMsgList.add(allMsgList.get(k));

//                                        IMChatManager.getInstance(getApplicationContext()).updateRecallSuccess(allMsgList.get(k));
//                                        sendBroadcast(allMsgList.get(k), Constants.REACLL_CHATMESSAGE_SUCCESS);

                                    } else {
                                        if (allMsgList.get(k).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_OPEN_RED)) {
                                            try {
                                                /**
                                                 * 是否打开自己的红包通知
                                                 */
                                                JSONObject object = new JSONObject(allMsgList.get(k).getExtra());
                                                String redUid = object.optString("uid");
                                                String myuid = object.optString("myuid");
                                                if (redUid.equals(master) || myuid.equals(master)) {
                                                    revMsgList.add(allMsgList.get(k));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            revMsgList.add(allMsgList.get(k));
                                        }
//                                        revMsgList.add(allMsgList.get(k));
//                                        IMChatManager.getInstance(getApplicationContext()).onRevMsg(allMsgList.get(k), master);
                                    }
                                } else if (allMsgList.get(k).getMsgFrom().equals("1")) {//发送消息
                                    if (allMsgList.get(k).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                                        IMChatManager.getInstance(getApplicationContext()).updateRecallSuccess(allMsgList.get(k));
                                    } else if (allMsgList.get(k).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) {
                                        IMChatManager.getInstance(getApplicationContext()).save(allMsgList.get(k));
                                        IMChatManager.getInstance(getApplicationContext()).sendBroadCast(allMsgList.get(k));
                                    } else {
                                        if (currentStreamIndex.equals("0")) {
                                            if (chatMessages.size() > 0) {
                                                boolean same = false;
                                                for (ChatMessage msg : chatMessages) {
                                                    if (msg.getClientId().equals(allMsgList.get(k).getClientId())) {
                                                        same = true;
                                                        break;
                                                    }
                                                }
                                                if (!same) {
                                                    IMChatManager.getInstance(getApplicationContext()).save(allMsgList.get(k));
                                                } else {
                                                    IMChatManager.getInstance(getApplicationContext()).updateSendSuccess(allMsgList.get(k));
                                                }
                                            } else {
                                                IMChatManager.getInstance(getApplicationContext()).save(allMsgList.get(k));
                                            }

//                                            IMChatManager.getInstance(getApplicationContext()).save(allMsgList.get(k));
                                        } else {
                                            IMChatManager.getInstance(getApplicationContext()).updateSendSuccess(allMsgList.get(k));
                                        }
                                    }
                                    sendBroadcast(allMsgList.get(k), Constants.SEND_CHATMESSAGE_SUCCESS);
                                }
                            }
                            if (revMsgList.size() > 0) {
                                IMChatManager.getInstance(getApplicationContext()).onRevMsgs(revMsgList, master);
                            }

                            if (recallMsgList.size() > 0) {
                                for (int j = 0; j < recallMsgList.size(); j++) {
                                    IMChatManager.getInstance(getApplicationContext()).updateRecallSuccess(recallMsgList.get(j));
                                    sendBroadcast(recallMsgList.get(j), Constants.REACLL_CHATMESSAGE_SUCCESS);
                                }
                            }


                        }

                    }


                    CommonUtil.chatMaxStreamIndex(this, master, Constants.SET_MSG_INDEX, "" + maxStreamIndex);
                    Intent intent = new Intent("CHAT_RECENT_MESSAGE");
                    localBroadcastManager.sendBroadcast(intent);
                    mHandler.removeCallbacks(heartBeatRunnable);
                    mHandler.post(heartBeatRunnable);

                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("解析错误  ", e);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    private void sendBroadcast(String mid, String type) {
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(type);
        model.setValue(mid);
        EventBus.getDefault().postSticky(model);
    }

    private void sendBroadcast(ChatMessage cm, String type) { //回执消息判断
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(type);
        if (!type.equals(Constants.SEND_CHATMESSAGE_FAIL)) {
            cm.setSendMsgState(Constants.SEND_CHATMESSAGE_SUCCESS);
        }
        model.setValue(model.toJson(cm));
        if (cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE) || cm.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
            JSONObject json = null;
            try {
                json = new JSONObject(cm.getExtra());
                String ext_type = json.optString("type");
                ChatSettingEntity entity = ChatSettingManager.getInstance().query(cm.getMaster(), cm.getMaster().endsWith(cm.getFromuser()) ? cm.getTouser() : cm.getFromuser());
                if (ext_type.equals("3") && !entity.getScreenshot()) { // 等于截屏通知 外加 自己没有开启截屏通知 隐藏
                    return;
                } else if (ext_type.equals("8")) {
                    return;
                } else if (ext_type.equals("100")) {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        EventBus.getDefault().postSticky(model);
    }

    private void sendBroadcast(String type) {
        Intent intent = new Intent(type);
        localBroadcastManager.sendBroadcast(intent);
        MainMessage model = new MainMessage();
        model.setKey(Constants.SOCKET_USER_LOGOUT);
        model.setValue("");
        EventBus.getDefault().postSticky(model);
    }

    private class ImSocketReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                LogUtil.i(TAG, "onReceive: action " + action);
                if (action.equals(Constants.SEND_CHATMESSAGE)) {
                    Bundle bundle = intent.getExtras();
                    if (null != bundle) {
                        chatMessage = (ChatMessage) bundle.getSerializable("ChatMessage");
                        if (chatMessage != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_DELETEMSG)) {
                                        if (sendMsg(3, chatMessage)) {
                                            LogUtil.i(TAG, "run: 删除消息成功");
                                        } else {
                                            ToasthowShort("删除消息失败");
                                            LogUtil.i(TAG, "run: 删除消息失败");
                                        }
                                    } else if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                                        if (sendMsg(6, chatMessage)) {
                                            LogUtil.i(TAG, "run: 撤回消息成功");
                                        } else {
                                            ToasthowShort("撤回消息失败");
                                        }
                                    } else {
                                        LogUtil.i(TAG, "run: ClientId " + chatMessage.getClientId());
                                        if (sendMsg(2, chatMessage)) {
                                            LogUtil.i(TAG, "run: 发送消息成功");
                                        } else {
                                            chatMessage.setPrompt("true");
                                            chatMessage.setResend("false");
                                            chatMessage.setProgress("false");
                                            chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE_FAIL);
                                            IMChatManager.getInstance(getApplicationContext()).updateSendFail(chatMessage);
                                            sendBroadcast(chatMessage, Constants.SEND_CHATMESSAGE_FAIL);
                                            mHandler.removeCallbacks(heartBeatRunnable);
                                            handler.sendEmptyMessageDelayed(CONNECT_SERVIE_ACTION, CONNECT_SERVIE_RATE);
                                            LogUtil.i(TAG, "run: 发送消息失败");
                                        }
                                    }
                                }
                            }).start();
                        }
                    }
                }
            }
        }
    }

    private void ToasthowShort(final String content) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (content.equals("本群由于严重违规，被多人举报，已被禁用")) {
                    ToastUtils.showShortChatErrMsg(getApplicationContext(), content + "");
                } else {
                    ToastUtils.showShort(getApplicationContext(), content + "");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LogUtil.i(TAG, "onDestroy: ");
        if (null != imReceiver) {
            localBroadcastManager.unregisterReceiver(imReceiver);
        }

        serviceContect = false;
        queryMagThread = null;

        master = null;
        mHandler.removeCallbacks(heartBeatRunnable);
//        if (null != socket) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    socket.AllClose();
//                }
//            }).start();
//        }
    }

    private Runnable queryDoThread = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (!CommonUtil.isBlank(master) && serviceContect) {
                    try {
                        Thread.sleep(1000 * 5);
                        querySendUnSuccess(master);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    //更新单条发送回执未成功的聊天消息
    public void querySendUnSuccess(String master) {
        ChatDBHelper helper = new ChatDBHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM (SELECT * FROM chat_history WHERE username='" + master + "' ORDER BY _id DESC LIMIT 0,100)", null);
        List<ChatMessage> msgList = new ArrayList<>();
        while (cursor.moveToNext()) {
            msgList.add(IMChatManager.getInstance(BaseApplication.getInstance()).cursorToChatMessage(cursor));
        }
        if (msgList.size() > 0) {
            for (ChatMessage cm : msgList) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date pre = df.parse(df.format(new Date()));
                    Date cur = df.parse(cm.getTime());
                    long diff = pre.getTime() - cur.getTime();
                    if (diff / (1000 * 5) > 1) {
                        if (!cm.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_SUCCESS) && !cm.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_FAIL)) {
                            if (CommonUtil.isBlank(cm.getServeId())) {
                                LogUtil.i(TAG, "querySendUnSuccess: getClientId " + cm.getClientId());
                                cm.setSendMsgState(Constants.SEND_CHATMESSAGE_FAIL);
                                sendBroadcast(cm, Constants.SEND_CHATMESSAGE_FAIL);
                                IMChatManager.getInstance(getApplicationContext()).updateSendFail(cm);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();
        db.close();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true, priority = 98)
    public void sendUniteUpdateDataModel(UniteUpdateDataModel model) {
        if (NetUtil.getNetWork(getBaseContext())) { //检查网络状态
            if (model.getKey().equals(Constants.SEND_CHATMESSAGE)) {
                Gson gson = new Gson();
                chatMessage = gson.fromJson(model.getValue(), ChatMessage.class);
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                sendMsg(2, chatMessage);
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.SEND_RED_HONGBAO)) {
                Gson gson = new Gson();
                chatMessage = gson.fromJson(model.getValue(), ChatMessage.class);
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                sendMsg(2, chatMessage);
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.SEND_NOTICE)) {
                Gson gson = new Gson();
                chatMessage = gson.fromJson(model.getValue(), ChatMessage.class);
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                sendMsg(2, chatMessage);
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.SEND_DELETEMSG)) {
                Gson gson = new Gson();
                chatMessage = gson.fromJson(model.getValue(), ChatMessage.class);
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                sendMsg(3, chatMessage);
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.SEND_RECALLMSG)) {
                Gson gson = new Gson();
                chatMessage = gson.fromJson(model.getValue(), ChatMessage.class);
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                sendMsg(6, chatMessage);
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.IMSOCKETSERVICE_SENG)) {
                chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                sendMsg(2, chatMessage);
                EventBus.getDefault().removeStickyEvent(model);
            }
            UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
            if (stickyEvent != null) {
                Log.i("ImSocketService", "删除事件了:");
                EventBus.getDefault().removeStickyEvent(stickyEvent);
            }
        } else {


        }
    }

    //    关闭服务
    @Override
    public boolean stopService(Intent name) {
        EventBus.getDefault().unregister(this);
        return super.stopService(name);
    }

    //解绑服务
    @Override
    public void unbindService(ServiceConnection conn) {
        EventBus.getDefault().unregister(this);
        super.unbindService(conn);
    }
}
