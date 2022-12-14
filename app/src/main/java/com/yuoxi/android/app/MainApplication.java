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
import com.yuoxi.android.app.activity.LoginActivity;

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
        EventBus.getDefault().register(this);//??????????????????
        ImSharedPreferences.init(getInstance());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true, priority = 100)
    //????????????????????????????????????,?????????????????? ????????????,???????????????.?????????0
    //7-24 ??????IMChatActivity ????????? 101 ?????????,????????? ?????????.
    public void getMessage(UniteUpdateDataModel model) {
        if (!NetUtil.getNetWork(getBaseContext()) && !model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //?????????????????? ??????????????????
            UniteUpdateDataModel model1 = new UniteUpdateDataModel();
            model1.setKey(Constants.SEND_CHATMESSAGE_FAIL);
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE_FAIL);
            model1.setValue(model1.toJson(chatMessage));
            IMChatManager.getInstance(getInstance()).save(chatMessage);
            EventBus.getDefault().removeStickyEvent(model); //?????????????????????
            UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
            if (stickyEvent != null) {
                Log.i(TAG, "???????????????:");
                EventBus.getDefault().removeStickyEvent(stickyEvent);
            }
            EventBus.getDefault().postSticky(model1);//?????????????????????
            return;
        }
        Log.e("EventBus", "??????...???Aoolication???????????????.....");
        if (model.getKey().equals(Constants.SEND_CHATMESSAGE)) { //????????????
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            // ???????????????????????????????????????
            if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO)
                    || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PIC)
                    || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_VIDEO)
                    || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) {
                IMChatManager.getInstance(getApplicationContext()).updateMediaFail(chatMessage);
            } else { // ??????????????????
                IMChatManager.getInstance(getInstance()).save(chatMessage);
            }
        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_SUCCESS)) { //??????????????? ??????????????????(??????)
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
        } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //??????????????????,
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        } else if (model.getKey().equals(Constants.SEND_DELETEMSG_SUCCESS)) { //??????????????? ??????????????????
        } else if (model.getKey().equals(Constants.REACLL_CHATMESSAGE_SUCCESS)) { //??????
        } else if (model.getKey().equals(ChatMessage.SEND_PROCEED)) { //??????
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        } else if (model.getKey().equals(Constants.CHAT_NEW_MESSAGE)) { //?????????????????????
        } else if (model.getKey().equals(Constants.NETWORK_NONE)) { //????????????,?????????
            stopService(new Intent(getInstance(), ImSocketService.class));
//            stopService(new Intent(getInstance(), AVChatService.class));

        } else if (model.getKey().equals(Constants.NETWORK_WIFI) || model.getKey().equals(Constants.NETWORK_MOBILE)) {//???????????? wifi,??????
            ImSocketService.startImService(getInstance()); //???????????????
//            if (ImApplication.getmAgoraAPI() == null) {
//                ImApplication.setupAgoraEngine();
//            } else {
//                AVChatService.startAVChatService(mContext);
//            }
        } else if (model.getKey().equals(ChatMessage.UPDATA_LOCAL_DATA)) { //??????????????????.??????????????????
            ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
            IMChatManager.getInstance(getInstance()).save(chatMessage);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 100)
    public void MainMessage(MainMessage model) {
        EventBus.getDefault().removeAllStickyEvents();
        if (model.getKey().equals(Constants.SOCKET_USER_LOGOUT)) {//??????????????????
//            if (messageCenterManager != null) {
//                messageCenterManager.stopPolling();
//            }
//            ChatSettingMap.clear();
            userInfo = null;
//            login = "????????????";

            stopService(new Intent(getInstance(), ImSocketService.class));
//            stopService(new Intent(getInstance(), AVChatService.class));
//            stopService(new Intent(getInstance(), MsgPushService.class));

            EventBus.getDefault().removeAllStickyEvents();
            EventBus.getDefault().removeStickyEvent(model);
            MsgCache.get(getInstance()).remove(Constants.USER_INFO); //??????????????????????????????
            ImSharedPreferences.exitLogin();

            Intent intent = new Intent(getInstance(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            EventBus.getDefault().removeStickyEvent(model);

        } else if (model.getKey().equals(Constants.EXITLOGIN)) { //????????????
//            if (messageCenterManager != null) {
//                messageCenterManager.stopPolling();
//            }
//            ChatSettingMap.clear();
            userInfo = null;
//            login = "????????????";

            stopService(new Intent(getInstance(), ImSocketService.class));
//            stopService(new Intent(getInstance(), AVChatService.class));
//            stopService(new Intent(getInstance(), MsgPushService.class));

            EventBus.getDefault().removeAllStickyEvents();
            EventBus.getDefault().removeStickyEvent(model);
            MsgCache.get(getInstance()).remove(Constants.USER_INFO); //??????????????????????????????
            ImSharedPreferences.exitLogin();

            Intent intent = new Intent(getInstance(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        } else if (model.getKey().equals(Constants.CHATSETTING)) {
//            ChatSettingMap.clear(); //??????????????????,????????????????????????????????????
//            List<ChatSettingEntity> list = ChatSettingManager.getInstance().queryAll(userInfo.getId());//????????????????????????????????????????????????
//            if (list.size() > 0) {
//                for (ChatSettingEntity chatSettingEntity : list) {
//                    ChatSettingMap.put(chatSettingEntity.getFriend(), chatSettingEntity);
//                }
//
//            }
        }

    }

    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true, priority = -100)
    public void deleteSticky(UniteUpdateDataModel model) {
        UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        LogUtil.i(TAG, "??????????????????EventBus,?????????.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = -100)
    public void deleteMainSticky(UniteUpdateDataModel model) {
        UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        Log.i(TAG, "??????????????????EventBus,Main??????.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = -100)
    public void deleteSticky(MainMessage model) {
        UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent);
        }
        Log.i(TAG, "???????????????????????????EventBus,Main??????");
    }
}
