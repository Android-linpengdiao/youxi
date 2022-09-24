package com.yuoxi.android.app;

import android.content.Intent;
import android.util.Log;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.utils.LogUtil;
import com.base.utils.MsgCache;
import com.okhttp.utils.HttpsUtils;
import com.okhttp.utils.OkHttpUtils;
import com.quakoo.im.IMSharedPreferences.ImSharedPreferences;
import com.quakoo.im.aiyou.ImSocketService;
import com.quakoo.im.manager.ChatSettingManager;
import com.quakoo.im.manager.IMChatManager;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.MainMessage;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.utils.NetUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainApplication extends BaseApplication {

    private static final String TAG = "MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        initOkHttp();
        initChatConfig();
    }

    private void initOkHttp() {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

                    @Override
                    public void log(String message) {
                        LogUtil.i("message", message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .hostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void initChatConfig() {
        EventBus.getDefault().register(this);//用于处理消息
        ImSharedPreferences.init(getInstance());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true, priority = 100)
    //在子线程对数据库进行更新,并设置优先级 数值也大,优先级越高.默认为0
    //7-24 修改IMChatActivity 主线程 101 优先级,先显示 后存库.
    public void getMessage(UniteUpdateDataModel model) {
        if (!NetUtil.getNetWork(getBaseContext()) && !model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //检查网络状态 无网络的情况
            UniteUpdateDataModel model1 = new UniteUpdateDataModel();
            model1.setKey(Constants.SEND_CHATMESSAGE_FAIL);
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE_FAIL);
            model1.setValue(model1.toJson(chatMessage));
            IMChatManager.getInstance(getInstance()).save(chatMessage);
            EventBus.getDefault().removeStickyEvent(model); //删除原来的消息
            UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
            if(stickyEvent != null) {
                Log.i(TAG,"删除事件了:");
                EventBus.getDefault().removeStickyEvent(stickyEvent);
            }
            EventBus.getDefault().postSticky(model1);//发送新的消息体
            return;
        }
        Log.e("EventBus", "测试...在Aoolication操作数据库.....");
        if (model.getKey().equals(Constants.SEND_CHATMESSAGE)) { //发送消息
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            // 以保存过得消息只做更新处理
            if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO)
                    || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PIC)
                    || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_VIDEO)
                    || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) {
                IMChatManager.getInstance(getApplicationContext()).updateMediaFail(chatMessage);
            } else { // 新发送的消息
                IMChatManager.getInstance(getInstance()).save(chatMessage);
            }
        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_SUCCESS)) { //服务器回应 消息发送成功(回执)
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            if (!chatMessage.getExtra().isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(chatMessage.getExtra());
                    if (jsonObject.optString("type").equals("16")) {
                        JSONObject jsonObject1 = new JSONObject(jsonObject.optString("extra"));
                        if (!jsonObject1.optString("GMid").equals(userInfo.getId())) {
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //消息发送失败,
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        } else if (model.getKey().equals(Constants.SEND_DELETEMSG_SUCCESS)) { //服务器回执 删除消息成功
        } else if (model.getKey().equals(Constants.REACLL_CHATMESSAGE_SUCCESS)) { //回撤
        } else if (model.getKey().equals(ChatMessage.SEND_PROCEED)) { //上传
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        } else if (model.getKey().equals(Constants.CHAT_NEW_MESSAGE)) { //接收到新的消息
        } else if (model.getKey().equals(Constants.NETWORK_NONE)) { //网络变化,无网络
            stopService(new Intent(getInstance(), ImSocketService.class));
//            stopService(new Intent(getInstance(), AVChatService.class));

        } else if (model.getKey().equals(Constants.NETWORK_WIFI) || model.getKey().equals(Constants.NETWORK_MOBILE)) {//网络变化 wifi,移动
            ImSocketService.startImService(getInstance()); //开启长链接
//            if (ImApplication.getmAgoraAPI() == null) {
//                ImApplication.setupAgoraEngine();
//            } else {
//                AVChatService.startAVChatService(mContext);
//            }
        } else if (model.getKey().equals(ChatMessage.UPDATA_LOCAL_DATA)) { //更新本地数据.不需要过网络
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 100)
    public void MainMessage(MainMessage model) {
        EventBus.getDefault().removeAllStickyEvents();
//        if (model.getKey().equals(Constants.SOCKET_USER_LOGOUT)) {//帐号被挤掉了
//            if (messageCenterManager != null) {
//                messageCenterManager.stopPolling();
//            }
//            ChatSettingMap.clear();
//            userInfo = null;
//            login = "重新登录";
//            stopService(new Intent(getInstance(), ImSocketService.class));
//            stopService(new Intent(getInstance(), AVChatService.class));
//            stopService(new Intent(getInstance(), MsgPushService.class));
//            Intent intent1 = new Intent(getInstance(), ApplyHintActivity.class);
//            intent1.putExtra("type", "logout");
//            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent1);
//            EventBus.getDefault().removeStickyEvent(model);
//
//            UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
//            if(stickyEvent != null) {
//                Log.i(TAG,"删除事件了:");
//                EventBus.getDefault().removeStickyEvent(stickyEvent);
//            }
//            EventBus.getDefault().removeAllStickyEvents();
//        } else if (model.getKey().equals(Constants.EXITLOGIN)) { //重新登录
//            if (messageCenterManager != null) {
//                messageCenterManager.stopPolling();
//            }
//            ChatSettingMap.clear();
//            userInfo = null;
//            login = "重新登录";
//            EventBus.getDefault().removeStickyEvent(model);
//            stopService(new Intent(getInstance(), ImSocketService.class));
//            stopService(new Intent(getInstance(), AVChatService.class));
//            stopService(new Intent(getInstance(), MsgPushService.class));
//            MsgCache.get(getInstance()).remove(Constants.USER_INFO); //清除缓存里的帐号信息
//            ImSharedPreferences.exitLogin();
//            EventBus.getDefault().removeAllStickyEvents();
//            Intent intent1 = new Intent(getInstance(), ApplyHintActivity.class);
//            intent1.putExtra("type", Constants.EXITLOGIN);
//            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent1);
//        } else if (model.getKey().equals(Constants.CHATSETTING)) {
//            ChatSettingMap.clear(); //设置当前帐号,先清除全局好友设置的变量
//            List<ChatSettingEntity> list = ChatSettingManager.getInstance().queryAll(userInfo.getId());//获取在本地数据库中设置的好友设置
//            if (list.size() > 0) {
//                for (ChatSettingEntity chatSettingEntity : list) {
//                    ChatSettingMap.put(chatSettingEntity.getFriend(), chatSettingEntity);
//                }
//
//            }
//        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true, priority = -100)
    public void deleteSticky(UniteUpdateDataModel model) {
        UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        LogUtil.i(TAG, "删除消息体的EventBus,子线程.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = -100)
    public void deleteMainSticky(UniteUpdateDataModel model) {
        UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        Log.i(TAG, "删除消息体的EventBus,Main线程.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = -100)
    public void deleteSticky(MainMessage model) {
        UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
        if(stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        Log.i(TAG, "清除好友消息设置的EventBus,Main线程");
    }
}
