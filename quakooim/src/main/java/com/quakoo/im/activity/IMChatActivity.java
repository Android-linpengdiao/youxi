package com.quakoo.im.activity;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.base.view.OnMultiClickListener;
import com.cjt2325.cameralibrary.CameraActivity;
import com.cjt2325.cameralibrary.JCameraView;
import com.google.gson.reflect.TypeToken;
import com.okhttp.SendRequest;
import com.okhttp.callbacks.StringCallback;
import com.quakoo.im.media.MediaFile;
import com.quakoo.im.media.MediaSelectActivity;
import com.quakoo.im.media.MediaUtils;
import com.yanzhenjie.permission.AndPermission;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.MessageBus;
import com.base.UserInfo;
import com.base.manager.DialogManager;
import com.base.utils.BitmapUtils;
import com.base.utils.CommonUtil;
import com.base.utils.FileUtils;
import com.base.utils.GsonUtils;
import com.base.utils.PermissionUtils;
import com.base.utils.ToastUtils;
import com.base.view.BaseBottomSheetDialog;
import com.base.view.RecycleViewDivider;
import com.google.gson.Gson;
import com.quakoo.im.CustomEvents.CustomClickEvents;
import com.quakoo.im.CustomEvents.HeightVariety;
import com.quakoo.im.IMSharedPreferences.ImSharedPreferences;
import com.quakoo.im.R;
import com.quakoo.im.adapter.ChatListAdapter;
import com.quakoo.im.aiyou.ImSocketService;
import com.quakoo.im.databinding.ActivityImchatBinding;
import com.quakoo.im.databinding.DialogImExtPhotoBinding;
import com.quakoo.im.manager.ChatSettingManager;
import com.quakoo.im.manager.IMChatManager;
import com.quakoo.im.manager.MediaManager;
import com.quakoo.im.model.ChatBottomIocnModel;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.EmotionEntity;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.screen.ScreenShotListenManager;
import com.quakoo.im.utils.KeybordS;
import com.quakoo.im.utils.NetUtil;
import com.quakoo.im.utils.UnitUtils;
import com.quakoo.im.view.AudioRecorderButton;
import com.quakoo.im.view.ChatExtView;
import com.quakoo.im.view.CustomEmotionView;
import com.quakoo.im.view.CustomLayoutManager;
import com.yanzhenjie.permission.runtime.Permission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class IMChatActivity extends BaseActivity implements CustomClickEvents, HeightVariety, AudioRecorderButton.AudioFinishRecorderListener,
        ScreenShotListenManager.OnScreenShotListener, CustomEmotionView.OnCustomItemClickListener, ChatExtView.OnChatExtClickListener {
    private ActivityImchatBinding mBinding;
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private ChatListAdapter mChatListAdapter;
    private UserInfo ChatFriend = null;
    private String TAG = "IMChatActivity";
    private String curPhotoPath = "";
    private String master = "";  // ????????????????????????
    private static final int RESULT_GALLERY = 100;
    private static final int RESULT_CAMERA = 200;
    private final static int REQUEST_VIDEO = 300;
    public static final int RESULT_AUDIO = 1000;
    private int TitleBar_height, Input_box_height = 0;
    private String chatType = null;
    private int unreadCount = 0;
    //    private int page = 1;
    private int netWrokState;
    private ScreenShotListenManager manager;
    // ?????????load??????????????????
    private boolean isAllowLoadingMore = true; //????????????????????????
    private boolean isLoadingMore = false; //?????????????????????
    private CustomLayoutManager layoutManager;
    private IMChatManager imChatManager;
    private String ImURL = "";
    private volatile Long Screen_Capture_Date = 0L;//????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: " + chatMessageList.size());
        chatMessageList.clear();
//        EventBus.getDefault().register(this);
        IMChatManager.getInstance(this).notificationCancelAll();
        netWrokState = NetUtil.getNetWorkState(this);
        mBinding = getViewData(R.layout.activity_imchat);
        finishActivity(IMChatActivity.class);
        addActivity(IMChatActivity.this);
        TitleBar_height = (int) getResources().getDimension(R.dimen.TitleBar_height);
        Input_box_height = (int) getResources().getDimension(R.dimen.Input_box_height);
        mBinding.titleView.getPaint().setFakeBoldText(true);
        if (getIntent().hasExtra("userInfo")) {
            ChatFriend = (UserInfo) getIntent().getSerializableExtra("userInfo");
            mBinding.titleView.setText(ChatFriend.getRemark().equals("") ? ChatFriend.getName() : ChatFriend.getRemark());
        }
        if (getIntent().hasExtra("chat_type")) {
            chatType = getIntent().getStringExtra("chat_type");
            ChatFriend.setType(chatType);

        }
        List<ChatMessage> recentMessageList = IMChatManager.getInstance(this).getMessageUnreadRead(BaseApplication.getUserInfo().getId(), chatType, ChatFriend.getId());
        if (recentMessageList.size() > 0) {
            unreadCount = recentMessageList.get(0).getUnreadCount();
        }
        mBinding.newMessage.setText(unreadCount + "????????????");
        if (unreadCount > 20) {
//            if (unreadCount % 20 == 0) {
//                page += unreadCount / 20 - 1;
//            } else {
//                page += unreadCount / 20;
//            }
//            chatMessageList = IMChatManager.getInstance(this).getMoreChatHistory(ImApplication.userInfo.getId(), chatType, ChatFriend.getId(), page); //??????????????????
            mBinding.newMessage.setVisibility(View.VISIBLE);
        } else {
//            chatMessageList = IMChatManager.getInstance(this).getChatHistory(ImApplication.userInfo.getId(), chatType, ChatFriend.getId(), page); //??????????????????
        }
        chatMessageList = IMChatManager.getInstance(this).getChatHistory(BaseApplication.getUserInfo().getId(), chatType, ChatFriend.getId(), chatMessageList.size()); //??????????????????
        Log.i(TAG, "onCreate: chatMessageList " + chatMessageList.size());
        EventBus.getDefault().register(this);

        mBinding.newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.newMessage.setVisibility(View.GONE);
//                mBinding.chatList.smoothScrollToPosition(mBinding.chatList.getAdapter().getItemCount()-unreadCount);
                mBinding.chatList.scrollToPosition(mBinding.chatList.getAdapter().getItemCount() - unreadCount);
            }
        });

        if (chatMessageList.size() == 0) {
            PointMsg(); //?????????????????????
        }
        if (chatMessageList.size() == 20) {
            isAllowLoadingMore = true;
        }
        //????????????????????????
        if (recentMessageList.size() > 0) {
            IMChatManager.getInstance(this).read(BaseApplication.getUserInfo().getId(), recentMessageList.get(0));
            IMChatManager.getInstance(this).updateHintUserToRead(BaseApplication.getUserInfo().getId(), recentMessageList.get(0));
        }
        IMChatManager.getInstance(this).UpdateMessageUnreadRead(BaseApplication.getUserInfo().getId(), chatType, ChatFriend.getId()); //????????????????????????
        mChatListAdapter = new ChatListAdapter(this, chatMessageList, ChatFriend);
        layoutManager = new CustomLayoutManager(this);
        layoutManager.setAdapter(mChatListAdapter);
        mBinding.chatList.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        mBinding.chatList.setLayoutManager(layoutManager);
        RecycleViewDivider divider = new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, UnitUtils.dip2px(this, 10), Color.parseColor("#181526"));
        mBinding.chatList.addItemDecoration(divider);
//        mChatListAdapter.setHasStableIds(true);
        mBinding.chatList.setAdapter(mChatListAdapter);
        mBinding.ChatEdittextView.setHeightVariety(this);
        mBinding.ChatEdittextView.setReceivePersonnel(ChatFriend);
        mBinding.ChatEdittextView.setmRecorderListener(this);

        mBinding.backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBinding.ChatEdittextView.ishide()) {
                    mBinding.ChatEdittextView.setText1(8);
                    unlockContentHeightDelayed();
                }
                KeybordS.hideInput(IMChatActivity.this);
                destroyChat();
                MediaManager.pause();
                if (manager != null) {
                    manager.stopListen();
                }
                finish();
            }
        });

        mBinding.settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatType.equals(Constants.FRAGMENT_FRIEND)) { //??????
                    Intent intent = new Intent(IMChatActivity.this, IMChatSettingsActivity.class);
                    intent.putExtra("userInfo", ChatFriend);
                    startActivity(intent);
                } else if (chatType.equals(Constants.FRAGMENT_GROUP)) {//??????
                    Intent intent = new Intent(IMChatActivity.this, IMChatSettingsActivity.class);
                    intent.putExtra("userInfo", ChatFriend);
                    startActivity(intent);
                }
            }
        });

//        CommonUtil.observeSoftKeyboard(this, new CommonUtil.OnSoftKeyboardChangeListener() {
//            @Override
//            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
//                if (visible && softKeybardHeight > 0) {
//                    LogUtil.d(TAG, "onSoftKeyBoardChange: " + softKeybardHeight);
//                    if (BaseApplication.keyboardheight == 0) {
//                        BaseApplication.keyboardheight = softKeybardHeight;
//                        ImSharedPreferences.setValue(ImSharedPreferences.KEY_KEYBOARHEIGHT, softKeybardHeight);
//                    }
//                }
//            }
//        });
        mBinding.ChatEdittextView.setPanlheight(ImSharedPreferences.getKeyboardHeight(), true);
        KeybordS keybordS = new KeybordS(this);
        keybordS.setOnKeyboardChangeListener(new KeybordS.KeyboardChangeListener() {
            @Override
            public void onKeyboardShow(int keyboardHight) {
                if (BaseApplication.keyboardheight != keyboardHight) {
                    BaseApplication.keyboardheight = 0;
                }
                if (BaseApplication.keyboardheight == 0) {
                    BaseApplication.keyboardheight = keyboardHight;
                }
                if (BaseApplication.keyboardheight != keyboardHight) {
                    mBinding.ChatEdittextView.setPanlheight(keyboardHight, true);
                }
                lockContentHeight();
                if (mBinding.ChatEdittextView != null && !mBinding.ChatEdittextView.isSettingheight) {
                    mBinding.ChatEdittextView.setPanlheight(keyboardHight, true);
                }
                if (mBinding.ChatEdittextView != null) {
                    mBinding.ChatEdittextView.keyboardOpen();
                }

                if (mBinding.chatList != null) {
                    mBinding.chatList.smoothScrollToPosition(mBinding.chatList.getAdapter().getItemCount());
                }

            }

            @Override
            public void onKeyboardHide() {
                unlockContentHeightDelayed();
                if (mBinding.ChatEdittextView != null) {
                    mBinding.ChatEdittextView.keyboardHide();
                }
            }
        });

        mBinding.ChatEdittextView.setCustomClickEvents(this);
        mBinding.ChatEdittextView.setFocusable(true);
        mBinding.ChatEdittextView.setListView(mBinding.chatList);
        mBinding.audioRecorderView.setRecorderListener(this);


        manager = ScreenShotListenManager.newInstance(this);
        manager.setListener(this);
        mBinding.ChatEdittextView.getCustomEmotion().setOnItemClickListener(this);
        mBinding.ChatEdittextView.getChatExtView().setOnClickListener(this);
        loadChatGroupInfo();
        getGroupNumber(ChatFriend.getRemark().equals("") ? ChatFriend.getName() : ChatFriend.getRemark());
        mBinding.chatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mBinding.ChatEdittextView.hideChatEdittextView();
                return false;
            }
        });
        mBinding.chatList.scrollToPosition(mChatListAdapter.getItemCount() - 1);//???????????????????????????
        mBinding.chatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                    }
                    if (!recyclerView.canScrollVertically(-1) && !isLoadingMore && isAllowLoadingMore) {
                        isLoadingMore = true;
                        new LoadMoreChat().execute();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mBinding.chatList.getAdapter().getItemCount() - layoutManager.findFirstVisibleItemPosition() + 5 >= unreadCount) {
                    mBinding.newMessage.setVisibility(View.GONE);
                }
            }
        });

        mBinding.orderView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                String mScheme = "scheme://order/OrderHomeActivity?uid=" + getUserInfo().getId();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mScheme));
                startActivity(intent);
            }
        });


    }

    private static Object lock = new Object();

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 101)
    public void huoqxiaoxi(final UniteUpdateDataModel model) {
        Log.i(TAG, "huoqxiaoxi: getKey = " + model.getKey() + "  ;  model = " + GsonUtils.toJson(model));
        synchronized (lock) {
            if (model.getKey().equals(Constants.SEND_CHATMESSAGE)) { //????????????
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                if (chatMessage.getFromuser().equals(BaseApplication.getUserInfo().getId()) && chatMessage.getTouser().equals(ChatFriend.getId())) { //?????????????????? ????????? ?????????????????????????????????????????????/??????
                    for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                        ChatMessage chatMessage1 = chatMessageList.get(i);
                        if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                            int position = chatMessageList.indexOf(chatMessage1);
                            chatMessageList.set(position, chatMessage);
                            mChatListAdapter.notifyItemChanged(position);
                            mBinding.chatList.smoothScrollToPosition(mBinding.chatList.getAdapter().getItemCount());
                            return;
                        }
                    }
                    chatMessageList.add(chatMessage);
                    mChatListAdapter.notifyItemChanged(chatMessageList.indexOf(chatMessage));
                    mBinding.chatList.smoothScrollToPosition(mBinding.chatList.getAdapter().getItemCount());
                    EventBus.getDefault().removeStickyEvent(model);
                }
            } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_SUCCESS)) { //??????????????? ??????????????????(??????)
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                    ChatMessage chatMessage1 = chatMessageList.get(i);
                    if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                        if (chatMessage1.getThumbnailUrl() != null && !chatMessage1.getThumbnailUrl().isEmpty()) {
                            if (chatMessage.getThumbnailUrl().isEmpty()) {
                                chatMessage.setThumbnailUrl(chatMessage1.getThumbnailUrl());
                            }
                        }
                        chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE_SUCCESS);
                        int position = chatMessageList.indexOf(chatMessage1);
                        chatMessageList.set(position, chatMessage);
                        mChatListAdapter.notifyItemChanged(position);
                        return;
                    }
                }
                EventBus.getDefault().removeStickyEvent(model);

            } else if (model.getKey().equals(Constants.SEND_CHATMESSAGE_FAIL)) { //??????????????????,
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                    ChatMessage chatMessage1 = chatMessageList.get(i);
                    if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                        int position = chatMessageList.indexOf(chatMessage1);
                        chatMessageList.set(position, chatMessage);
                        mChatListAdapter.notifyItemChanged(position);
                        return;
                    }
                }
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.CHAT_UPDATE_MESSAGE)) { //????????????
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                chatMessage.setRedOpenState("true");
                for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                    ChatMessage chatMessage1 = chatMessageList.get(i);
                    if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                        int position = chatMessageList.indexOf(chatMessage1);
                        Log.i(TAG, "position: " + position + " ; getRedOpenState = " + chatMessage.getRedOpenState());
                        chatMessageList.set(position, chatMessage);
                        mChatListAdapter.notifyItemChanged(position);
                        return;
                    }
                }
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.SEND_DELETEMSG_SUCCESS)) { //??????????????? ??????????????????
                for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                    ChatMessage message = chatMessageList.get(i);
                    if (message.getServeId().equals(model.getValue())) {
                        int position = chatMessageList.indexOf(message);
                        if (chatMessageList.size() - 1 == i) {
                            IMChatManager.getInstance(getApplication()).delete(BaseApplication.getUserInfo().id, message, chatMessageList, true);
                        } else {
                            IMChatManager.getInstance(getApplication()).delete(BaseApplication.getUserInfo().id, message, chatMessageList, false);
                        }
                        chatMessageList.remove(message);
                        mChatListAdapter.notifyDataSetChanged();

                        return;
                    }
                }

            } else if (model.getKey().equals(Constants.REACLL_CHATMESSAGE_SUCCESS)) { //??????
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                //??????????????? ?????? ?????? ?????????????????????????????????,???????????????????????????????????????,?????????????????????
                if ((chatMessage.getFromuser().equals(BaseApplication.getUserInfo().getId()) && chatMessage.getTouser().equals(ChatFriend.getId()))
                        ||
                        chatMessage.getFromuser().equals(ChatFriend.getId()) && chatMessage.getTouser().equals(BaseApplication.getUserInfo().getId())) {
                    for (ChatMessage chatMessage1 : chatMessageList) {

                        if (chatMessage1.getServeId().equals(chatMessage.getServeId())) {
                            int p = chatMessageList.indexOf(chatMessage1);
                            chatMessage1.setContentType(chatMessage.getContentType());
                            chatMessage1.setContent(chatMessage.getContent());
                            chatMessageList.set(p, chatMessage1);
                            mChatListAdapter.notifyItemChanged(p);
                            EventBus.getDefault().removeStickyEvent(model);
                            return;
                        }

//                    if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {//???????????????
//                        EventBus.getDefault().removeStickyEvent(model);
//                        return;
//                    } else if (chatMessage1.getServeId().equals(chatMessage.getServeId())) {
//                        int p = chatMessageList.indexOf(chatMessage1);
//                        chatMessage1.setContentType(chatMessage.getContentType());
//                        chatMessage1.setContent(chatMessage.getContent());
//                        chatMessageList.set(p, chatMessage1);
//                        mChatListAdapter.notifyItemChanged(p, "");
//                        EventBus.getDefault().removeStickyEvent(model);
//                        return;
//                    }
                    }
//                chatMessageList.add(chatMessage);
//                mChatListAdapter.notifyItemChanged(chatMessageList.indexOf(chatMessage), "");
//                RecyclerView.LayoutManager layoutManager = mBinding.chatList.getLayoutManager();
//                LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
//                manager.scrollToPositionWithOffset(chatMessageList.size(), 1);
//                EventBus.getDefault().removeStickyEvent(model);
//                IMChatManager.getInstance(this).updateRecentChatUnread(ImApplication.userInfo.getId(), chatMessage);
                }


            } else if (model.getKey().equals(Constants.CHAT_NEW_MESSAGE)) { //?????????????????????
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                //??????????????? ?????? ?????? ?????????????????????????????????,???????????????????????????????????????,?????????????????????
                if ((chatMessage.getFromuser().equals(BaseApplication.getUserInfo().getId()) && chatMessage.getTouser().equals(ChatFriend.getId()))
                        ||
                        chatMessage.getFromuser().equals(ChatFriend.getId()) && chatMessage.getTouser().equals(BaseApplication.getUserInfo().getId())) {
                    for (ChatMessage chatMessage1 : chatMessageList) {
                        if (chatMessage1.getClientId().equals(chatMessage.getClientId())
                                || chatMessage1.getServeId().equals(chatMessage.getServeId())) {
                            mChatListAdapter.notifyItemChanged(chatMessageList.indexOf(chatMessage));
                            EventBus.getDefault().removeStickyEvent(model);
                            return;
                        }
                    }
                    chatMessageList.add(chatMessage);
                    mChatListAdapter.notifyItemChanged(chatMessageList.indexOf(chatMessage));
                    RecyclerView.LayoutManager layoutManager = mBinding.chatList.getLayoutManager();
                    LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                    manager.scrollToPositionWithOffset(chatMessageList.size(), 0);
                    chatMessage.setUnreadCount(0);
                    if (imChatManager == null) {
                        imChatManager = IMChatManager.getInstance(IMChatActivity.this);
                    }
                    imChatManager.updateRecentChatUnread(BaseApplication.getUserInfo().getId(), chatMessage);
                    imChatManager.read(BaseApplication.getUserInfo().getId(), chatMessage);
                    UniteUpdateDataModel stickyEvent = EventBus.getDefault().getStickyEvent(UniteUpdateDataModel.class);
                    if (stickyEvent != null) {
                        EventBus.getDefault().removeStickyEvent(stickyEvent);
                    }
                }
            } else if (model.getKey().equals(ChatMessage.SEND_PROCEED)) { //??????????????????,?????????????????????
                final ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                chatMessageList.add(chatMessage);
                mChatListAdapter.notifyItemChanged(chatMessageList.indexOf(chatMessage));
                RecyclerView.LayoutManager layoutManager = mBinding.chatList.getLayoutManager();
                LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                manager.scrollToPositionWithOffset(chatMessageList.size(), 0);
                EventBus.getDefault().removeStickyEvent(model);
                if (chatMessage.getSendMsgState().equals(Constants.SEND_PROCEED)) { //???????????? ?????? ????????????
                    final int position = chatMessageList.indexOf(chatMessage);
                    if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO) || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PIC) || chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) { //????????????
                        File file = new File(chatMessage.getUrl());
                        SendRequest.getUpFile(file, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                Log.e(TAG, "onError:" + "??????");
                                chatMessage.setSendMsgState(Constants.SEND_PROCEED);
                            }

                            @Override
                            public void onResponse(String response, int id) { //????????????
                                Log.e(TAG, "response:" + response);
                                chatMessage.setMsgState("read");
                                chatMessage.setSendMsgState(Constants.SEND_PROCEED_SUCCEED);
                                JSONObject object = null;
                                try {
                                    object = new JSONObject(response);
                                    String url;
                                    if (file.getPath().endsWith(".gif")) {
                                        JSONObject jsonObject = object.optJSONObject("ok");
                                        String jpg = jsonObject.optString("jpg");
                                        String gif = jsonObject.optString("gif");
                                        chatMessage.setUrl(gif);
                                    } else {
                                        url = object.getString("ok");
                                        chatMessage.setUrl(url);
                                    }

//                                String url = object.getString("ok");
//                                chatMessage.setUrl(url);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) {
                                    String address;
                                    String longitude;
                                    String latitude;
                                    String picture;
                                    String aoiname;
                                    try {
                                        JSONObject jsonObject = new JSONObject(chatMessage.getExtra());
                                        address = jsonObject.getString("content");
                                        longitude = jsonObject.getString("longitude");
                                        latitude = jsonObject.getString("latitude");
                                        aoiname = jsonObject.getString("aoiname");
                                        picture = chatMessage.getUrl();
                                        JSONObject extra = new JSONObject();
                                        extra.put("content", address);
                                        extra.put("longitude", longitude);
                                        extra.put("latitude", latitude);
                                        extra.put("picture", picture);
                                        extra.put("aoiname", aoiname);
                                        chatMessage.setExtra(extra.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                UniteUpdateDataModel model = new UniteUpdateDataModel();
                                model.setKey(Constants.SEND_CHATMESSAGE);
                                model.setValue(model.toJson(chatMessage));
                                EventBus.getDefault().postSticky(model);
                            }

                            @Override
                            public void inProgress(float progress, long total, int id) {//????????????
                                Log.e(TAG, "?????????" + progress);
                                chatMessage.setSendMsgState(Constants.SEND_PROCEED_IN);
                                chatMessage.setUpprogress(progress);
                                chatMessageList.set(position, chatMessage);
                                mChatListAdapter.notifyItemChanged(position);
                            }
                        });
                    } else if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_VIDEO)) { //????????????
//                        ImApplication.getUploadDownloadManager().addTask(chatMessage);
                    }
                }
            } else if (model.getKey().equals(Constants.SEND_PROCEED_IN)) {//??????????????????
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                //??????????????? ?????? ?????? ?????????????????????????????????,???????????????????????????????????????,?????????????????????
                if ((chatMessage.getFromuser().equals(BaseApplication.getUserInfo().getId()) && chatMessage.getTouser().equals(ChatFriend.getId()))
                        ||
                        chatMessage.getFromuser().equals(ChatFriend.getId()) && chatMessage.getTouser().equals(BaseApplication.getUserInfo().getId())) {
                    for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                        ChatMessage chatMessage1 = chatMessageList.get(i);
                        if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                            int position = chatMessageList.indexOf(chatMessage1);
                            chatMessageList.set(position, chatMessage);
                            mChatListAdapter.notifyItemChanged(position);
                            return;
                        }
                    }
                    chatMessageList.add(chatMessage);
                    mChatListAdapter.notifyItemChanged(chatMessageList.indexOf(chatMessage));
                    RecyclerView.LayoutManager layoutManager = mBinding.chatList.getLayoutManager();
                    LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                    manager.scrollToPositionWithOffset(chatMessageList.size(), 1);
                    EventBus.getDefault().removeStickyEvent(model);
                }
            } else if (model.getKey().equals(Constants.TYPE_DELETE)) {//??????????????????
                chatMessageList.clear();
                mChatListAdapter.notifyDataSetChanged();
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.UPDATA_GROUPINFO)) {
                ChatFriend.setName(model.getValue());
                getGroupNumber(model.getValue());
            } else if (model.getKey().equals(Constants.TYPE_EXIT) || model.getKey().equals(Constants.TYPE_JOIN)) {
                getGroupNumber(ChatFriend.getName());
            } else if (model.getKey().equals(Constants.AITE_FRIEND)) {//@ ??????
                JSONObject jsonObject = model.getValueJson();
                JSONArray jsonArray;
                try {
                    if (jsonObject != null && jsonObject.length() != 0) {
                        jsonArray = new JSONArray(jsonObject.get("userList").toString());
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            UserInfo user = gson.fromJson(jsonArray.get(i).toString(), UserInfo.class);
                            mBinding.ChatEdittextView.getEditText().addAtSpan(user.name, Integer.parseInt(user.id));
                            mBinding.ChatEdittextView.getEditText().setSelection(mBinding.ChatEdittextView.getEditText().getText().length());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(ChatMessage.AUDIO_PLAY_STATE_COMPLETION)) { //????????????
                if (AudioRecorderButton.mCurrentState != AudioRecorderButton.STATE_NORMAL) {//?????????????????????????????????
                    return;
                }
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class); //????????????????????????
                int p = -1;
                for (int j = chatMessageList.size() - 1; j >= 0; j--) {
                    ChatMessage chatMessage1 = chatMessageList.get(j);
                    if (chatMessage.getClientId().equals(chatMessage1.getClientId())) {
                        p = j;
                        for (int i = p + 1; i < chatMessageList.size(); i++) {
                            ChatMessage chatMessage2 = chatMessageList.get(i);
                            //chatMessage2 ???????????? ????????????????????????????????????
                            if (!chatMessage2.getFromuser().equals(BaseApplication.getUserInfo().getId()) && chatMessage2.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO) && (chatMessage2.getAudioState().equals("unread")) || chatMessage2.getAudioState().equals(ChatMessage.AUDIO_PLAY_STATE_START)) {
                                chatMessage2.setAudioState(ChatMessage.AUDIO_PLAY_STATE_START);
                                chatMessageList.set(i, chatMessage2);
                                mChatListAdapter.notifyItemChanged(i);
                                EventBus.getDefault().removeStickyEvent(model);
                                return;
                            }
                        }
                        return;
                    }
                }

            } else if (model.getKey().equals(ChatMessage.AUDIO_PLAY_STATE_START)) {//????????????

            } else if (model.getKey().equals(ChatMessage.UPDATA_LOCAL_DATA)) {
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class); //????????????????????????
                if ((chatMessage.getFromuser().equals(BaseApplication.getUserInfo().getId()) && chatMessage.getTouser().equals(ChatFriend.getId()))
                        ||
                        chatMessage.getFromuser().equals(ChatFriend.getId()) && chatMessage.getTouser().equals(BaseApplication.getUserInfo().getId())) { //??????????????????????????????,???????????????
                    for (ChatMessage chatMessage1 : chatMessageList) {
                        if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                            int p = chatMessageList.indexOf(chatMessage1);
                            chatMessageList.set(p, chatMessage);
                            mChatListAdapter.notifyItemChanged(p);
                            EventBus.getDefault().removeStickyEvent(model);
                            mBinding.chatList.smoothScrollToPosition(chatMessageList.size());
                            return;
                        }
                    }
                    chatMessageList.add(chatMessage);
                    mChatListAdapter.notifyDataSetChanged();
                    mBinding.chatList.smoothScrollToPosition(chatMessageList.size());
                    EventBus.getDefault().removeStickyEvent(model);

                }

            } else if (model.getKey().equals(Constants.SEND_NOTICE)) { //???????????????????????????3
                ChatMessage chatMessage = (ChatMessage) model.Json_Model(ChatMessage.class);
                JSONObject json = null;
                try {
                    json = new JSONObject(chatMessage.getExtra());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String ext_type = json.optString("type");
                if (!ext_type.equals("3")) {
                    return;
                }
                //??????????????????
                ChatSettingEntity entity = ChatSettingManager.getInstance().query(chatMessage.getMaster(), chatMessage.getMaster().endsWith(chatMessage.getFromuser()) ? chatMessage.getTouser() : chatMessage.getFromuser());
                if (ext_type.equals("3") && !entity.getScreenshot()) { // ?????????????????? ?????? ?????????????????????????????? ??????
                    return;
                }
                for (int i = chatMessageList.size() - 1; i >= 0; i--) { //????????????????????????
                    ChatMessage chatMessage1 = chatMessageList.get(i);
                    if (chatMessage1.getClientId().equals(chatMessage.getClientId())) {
                        if (chatMessage1.getThumbnailUrl() != null && !chatMessage1.getThumbnailUrl().isEmpty()) {
                            if (chatMessage.getThumbnailUrl().isEmpty()) {
                                chatMessage.setThumbnailUrl(chatMessage1.getThumbnailUrl());
                            }
                        }
                        chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE_SUCCESS);
                        int position = chatMessageList.indexOf(chatMessage1);
                        chatMessageList.set(position, chatMessage);
                        mChatListAdapter.notifyItemChanged(position);
                        return;
                    }
                }
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE_SUCCESS);
                chatMessageList.add(chatMessage);
                mChatListAdapter.notifyItemChanged(chatMessageList.size());
                mBinding.chatList.smoothScrollToPosition(mBinding.chatList.getAdapter().getItemCount());
                EventBus.getDefault().removeStickyEvent(model);
            } else if (model.getKey().equals(Constants.UPDATE_EMOTION)) {
                mBinding.ChatEdittextView.getCustomEmotion().refreshData(IMChatActivity.this, getEmotionList().getEmotionList());
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void action(ChatBottomIocnModel model) {
        ToastUtils.showShort(getApplicationContext(), model.getName());
    }

    private boolean mute = false;

    /**
     * ??????????????????
     */
    private void loadChatGroupInfo() {
//        SendRequest.loadChatGroupInfo(ImApplication.userInfo.token, ChatFriend.getId(),
//                new GenericsCallback<GroupInfoEntity>(new JsonGenericsSerializator()) {
//
//                    @Override
//                    public void onBefore(Request request, int id) {
//                        showLoadingDialog();
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        hideLoadingDialog();
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(GroupInfoEntity response, int id) {
//                        if (response.isSuccess()) {
//                            if (response != null && response.getData() != null) {
//                                mute = response.getData().getMute() == 1 ? true : false;
//                                mBinding.ChatEdittextView.setMute(mute);
//                                SharedPreferencesUtils.setCanSendQr(ChatFriend.getId(),response.getData().getCanSendQr() == 1 ? false : true);
//                            }
//                        }
//                    }
//
//                });
    }

    private void getGroupNumber(final String name) {
//        if (ChatFriend.getType().equals(Constants.FRAGMENT_GROUP)) {
//            SendRequest.getGroupNumber(ImApplication.userInfo.getToken(), ChatFriend.getId(), new StringCallback() {
//                @Override
//                public void onError(Call call, Exception e, int id) {
//
//                }
//
//                @Override
//                public void onResponse(String response, int id) {
//                    try {
//                        JSONObject json = new JSONObject(response);
//                        boolean success = json.optBoolean("success");
//                        if (success) {
//                            int number = json.optInt("data");
//                            mBinding.titlebar.setTitle(name + "(" + number + ")");
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.startListen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImSocketService.startImService(IMChatActivity.this);
        mBinding.ChatEdittextView.getCustomEmotion().refreshData(IMChatActivity.this, getEmotionList().getEmotionList());
    }

    @Override
    protected void onStop() {
        MediaManager.pause();
        if (manager != null) {
            manager.stopListen();
        }
        super.onStop();

    }

    public void destroyChat() {
        ChatSettingEntity user = ChatSettingManager.getInstance().query(getUserInfo().getId(), ChatFriend.getId());
        IMChatManager.getInstance(this).UpdateMessageUnreadRead(BaseApplication.getUserInfo().getId(), chatType, ChatFriend.getId()); //????????????????????????
        if (user.getDestroy()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(chatType);
            chatMessage.setFromuser(ChatFriend.getId());
            chatMessage.setTouser(getUserInfo().getId());
            chatMessage.setDestroy("true");
            IMChatManager.getInstance(getApplication()).destroyChat(getUserInfo().getId(), chatMessage);
        }


    }

    @Override
    protected void onDestroy() {
        if (manager != null) {
            manager.stopListen();
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MediaManager.pause();
        KeybordS.hideInput(this);

    }


    /**
     * ????????????,??????,???????????????....
     *
     * @param view
     * @param tag
     */
    @SuppressLint("WrongConstant")
    @Override
    public void Click(View view, String tag) {
        int id = view.getId();
        if (id == R.id.imChatExtAudioView) { //??????
            AndPermission.with(IMChatActivity.this)
                    .runtime()
                    .permission(Permission.Group.MICROPHONE)
                    // ????????????????????????????????????????????????????????????????????????????????????????????????
                    .rationale((context, permissions, executor) -> {
                        // ????????????????????????????????????
                        String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????,??????????????????????????????,???????????????????????????????????????";
                        DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {
                                String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????";
                                ToastUtils.showShort(getApplicationContext(), title);
                            }

                            @Override
                            public void onItemRight() {
                                executor.execute();
                            }
                        });
                    })
                    // ??????????????????
                    .onGranted(permissions -> {
                        mBinding.audioRecorderView.init();


                    })
                    // ???????????????????????????????????????????????????????????????
                    .onDenied(permissions -> {
                        /**
                         * ????????????????????????????????????????????????
                         */
                        if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                            //true????????????????????????????????????
                            String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????,??????????????????????????????,???????????????????????????????????????";
                            DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, "??????", "????????????", new DialogManager.Listener() {
                                @Override
                                public void onItemLeft() {

                                }

                                @Override
                                public void onItemRight() {
                                    CommonUtil.toSetting(getApplicationContext());
                                }
                            });
                        } else {
                            String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????";
                            ToastUtils.showShort(getApplicationContext(), title);
                        }

                    })
                    .start();

        } else if (id == R.id.imChatExtPhotoView) { //??????
            onClickIMChatExtPhoto();

        } else if (id == R.id.imChatExtVideoView) {//??????
            AndPermission.with(IMChatActivity.this)
                    .runtime()
                    .permission(Permission.Group.CAMERA, Permission.Group.MICROPHONE)
                    // ????????????????????????????????????????????????????????????????????????????????????????????????
                    .rationale((context, permissions, executor) -> {
                        // ????????????????????????????????????
                        String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????,??????????????????????????????,???????????????????????????????????????";
                        DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {
                                String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????";
                                ToastUtils.showShort(getApplicationContext(), title);
                            }

                            @Override
                            public void onItemRight() {
                                executor.execute();
                            }
                        });
                    })
                    // ??????????????????
                    .onGranted(permissions -> {
                        int type = JCameraView.BUTTON_STATE_ONLY_RECORDER;
                        int minTime = 1;
                        int maxTime = 60;
                        CameraActivity.startCameraActivity(IMChatActivity.this, minTime, maxTime, "#44bf19", type, REQUEST_WXCAMERA);


                    })
                    // ???????????????????????????????????????????????????????????????
                    .onDenied(permissions -> {
                        /**
                         * ????????????????????????????????????????????????
                         */
                        if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                            //true????????????????????????????????????
                            String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????,??????????????????????????????,???????????????????????????????????????";
                            DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, "??????", "????????????", new DialogManager.Listener() {
                                @Override
                                public void onItemLeft() {

                                }

                                @Override
                                public void onItemRight() {
                                    CommonUtil.toSetting(getApplicationContext());
                                }
                            });
                        } else {
                            String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????";
                            ToastUtils.showShort(getApplicationContext(), title);
                        }

                    })
                    .start();
        } else if (id == R.id.imChatExtCallView) {//??????

            JSONObject json = new JSONObject();
            try {
                json.put("pid", 0);
                json.put("name", "???????????????3! ??????????????????????????????");
                json.put("info", "??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????");
                json.put("icon", "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF");
                json.put("image", "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF");
                json.put("extra", null);
                doSendPlugin(json);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    // ???????????????
    private void doSendPlugin(JSONObject extra) {
        ChatMessage cm = new ChatMessage();
        cm.setExtra(extra.toString());
        cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_SHARE_PLUGIND);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cm.setTime(df.format(new Date()));
        String clientId = CommonUtil.getStringToDate(cm.getTime()) + (int) (Math.random() * 1000);
        cm.setClientId(clientId);
        cm.setSendMessagePersonnel(BaseApplication.getUserInfo());
        cm.setReceiveMessagePersonnel(ChatFriend);
        cm.setMsgState("read");
        cm.setProgress("true");
        cm.setPrompt("false");
        cm.setSendMsgState(Constants.SEND_PROCEED);
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.SEND_CHATMESSAGE); //????????????
        model.setValue(model.toJson(cm));
        EventBus.getDefault().postSticky(model);
    }


    private static final int REQUEST_IMAGE = 100;
    private static final int REQUEST_CAMERA = 200;
    private static final int REQUEST_WXCAMERA = 300;
    private static final int REQUEST_FRIEND = 400;

    private void onClickIMChatExtPhoto() {
        View contentView = LayoutInflater.from(IMChatActivity.this).inflate(R.layout.dialog_im_ext_photo, null);
        final DialogImExtPhotoBinding userSexBinding = DataBindingUtil.bind(contentView);
        final BaseBottomSheetDialog bottomSheetDialog = new BaseBottomSheetDialog(IMChatActivity.this) {
            @Override
            protected View initContentView() {
                return userSexBinding.getRoot();
            }
        };
        bottomSheetDialog.show();
        userSexBinding.cameraView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AndPermission.with(IMChatActivity.this)
                        .runtime()
                        .permission(Permission.Group.CAMERA)
                        // ????????????????????????????????????????????????????????????????????????????????????????????????
                        .rationale((context, permissions, executor) -> {
                            // ????????????????????????????????????
                            String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????,??????????????????????????????,???????????????????????????????????????";
                            DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, new DialogManager.Listener() {
                                @Override
                                public void onItemLeft() {
                                    String title = "??????????????????" + getString(R.string.app_name) + " ?????? ??????";
                                    ToastUtils.showShort(getApplicationContext(), title);
                                }

                                @Override
                                public void onItemRight() {
                                    executor.execute();
                                }
                            });
                        })
                        // ??????????????????
                        .onGranted(permissions -> {
                            int type = JCameraView.BUTTON_STATE_ONLY_CAPTURE;
                            int minTime = 1;
                            int maxTime = 60;
                            CameraActivity.startCameraActivity(IMChatActivity.this, minTime, maxTime, "#44bf19", type, REQUEST_WXCAMERA);

                        })
                        // ???????????????????????????????????????????????????????????????
                        .onDenied(permissions -> {
                            /**
                             * ????????????????????????????????????????????????
                             */
                            if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                                //true????????????????????????????????????
                                String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????,??????????????????????????????,???????????????????????????????????????";
                                DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, "??????", "????????????", new DialogManager.Listener() {
                                    @Override
                                    public void onItemLeft() {

                                    }

                                    @Override
                                    public void onItemRight() {
                                        CommonUtil.toSetting(getApplicationContext());
                                    }
                                });
                            } else {
                                String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????";
                                ToastUtils.showShort(getApplicationContext(), title);
                            }

                        })
                        .start();
            }
        });
        userSexBinding.photoAlbumView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                AndPermission.with(IMChatActivity.this)
                        .runtime()
                        .permission(Permission.Group.STORAGE)
                        // ????????????????????????????????????????????????????????????????????????????????????????????????
                        .rationale((context, permissions, executor) -> {
                            // ????????????????????????????????????
                            String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????,??????????????????????????????,???????????????????????????????????????";
                            DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, new DialogManager.Listener() {
                                @Override
                                public void onItemLeft() {
                                    String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????";
                                    ToastUtils.showShort(getApplicationContext(), title);
                                }

                                @Override
                                public void onItemRight() {
                                    executor.execute();
                                }
                            });
                        })
                        // ??????????????????
                        .onGranted(permissions -> {
                            Bundle bundle = new Bundle();
                            bundle.putInt("mediaType", MediaUtils.MEDIA_TYPE_PHOTO);
                            bundle.putInt("maxNumber", 9);
                            openActivity(MediaSelectActivity.class, bundle, REQUEST_IMAGE);

                        })
                        // ???????????????????????????????????????????????????????????????
                        .onDenied(permissions -> {
                            /**
                             * ????????????????????????????????????????????????
                             */
                            if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                                //true????????????????????????????????????
                                String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????,??????????????????????????????,???????????????????????????????????????";
                                DialogManager.getInstance().addPermissionDialog(IMChatActivity.this, title, "??????", "????????????", new DialogManager.Listener() {
                                    @Override
                                    public void onItemLeft() {

                                    }

                                    @Override
                                    public void onItemRight() {
                                        CommonUtil.toSetting(getApplicationContext());
                                    }
                                });
                            } else {
                                String title = "??????????????????" + getString(R.string.app_name) + " ?????????????????? ??????";
                                ToastUtils.showShort(getApplicationContext(), title);
                            }

                        })
                        .start();
            }
        });
        userSexBinding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        userSexBinding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    /**
     * ????????????
     *
     * @param data
     */
    private void compressImage(Intent data) {
        try {
            if (data != null) {
                String imageJson = data.getStringExtra("imageJson");
                if (!TextUtils.isEmpty(imageJson)) {
                    Gson gson = new Gson();
                    List<MediaFile> mediaFiles = gson.fromJson(imageJson, new TypeToken<List<MediaFile>>() {
                    }.getType());
                    if (mediaFiles != null && mediaFiles.size() > 0) {
                        for (int i = 0; i < mediaFiles.size(); i++) {
                            String path = mediaFiles.get(i).getPath();
                            Luban.with(this)
                                    .load(path)// ??????????????????????????????
                                    .ignoreBy(500)// ??????????????????????????????
                                    .setTargetDir(FileUtils.getTempPath())// ?????????????????????????????????
                                    .setCompressListener(new OnCompressListener() { //????????????
                                        @Override
                                        public void onStart() {
                                        }

                                        @Override
                                        public void onSuccess(File file) {
                                            doSendPic(file.getPath());
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            doSendPic(path);
                                        }
                                    }).launch();//????????????

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //????????????
    private void openCamera() {
        // ????????????????????????
        String fileName = System.currentTimeMillis() + "_IMG.jpg";
        File file = FileUtils.createTempFile(fileName);
        if (null != file && file.exists()) {
            curPhotoPath = file.getPath();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //??????7.0????????????????????????
        if (Build.VERSION.SDK_INT >= 24) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            Uri uri = getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(intent, RESULT_CAMERA);
    }

    // ????????????
    private void doSendPic(String picPath) {
        if (!CommonUtil.isBlank(picPath)) {

            // ????????????????????????
//            int angle = PhotoBitmapUtils.readPictureDegree(picPath);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picPath, options);
            options.inSampleSize = BitmapUtils.calculateInSampleSize(options, 1080, 1920);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);

            // ??????????????????????????????
//            bitmap = PhotoBitmapUtils.rotaingImageView(angle, bitmap);

            String fileName = System.currentTimeMillis() + "_image.jpg";
            File compressFile = FileUtils.createAttachmentFile(master, fileName);
            FileUtils.compressBmpToFile(bitmap, compressFile, 200);
            if (compressFile.exists()) {
                ChatMessage cm = new ChatMessage();
                cm.setSendMessagePersonnel(BaseApplication.getUserInfo());//?????????
                cm.setReceiveMessagePersonnel(ChatFriend);//?????????
                cm.setSendMsgState(Constants.SEND_PROCEED);//???????????? ??????
                cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_PIC);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
                cm.setTime(df.format(new Date())); //??????????????????
                String clientId = CommonUtil.getStringToDate(cm.getTime()) + (int) (Math.random() * 1000);//
                cm.setClientId(clientId); //????????????????????????
                cm.setMsgState(Constants.SEND_PROCEED); //??????????????? ??????
                cm.setProgress("true"); //
                cm.setPrompt("false");
                cm.setContent(compressFile.getPath());
                cm.setUrl(compressFile.getPath());
                cm.setThumbnailUrl(compressFile.getPath());
                UniteUpdateDataModel model = new UniteUpdateDataModel();
                model.setKey(Constants.SEND_PROCEED); //????????????
                model.setValue(model.toJson(cm));
                EventBus.getDefault().postSticky(model);
            }

        }
    }

    // ???????????????

    /**
     * @param videoPath ???????????????
     * @param cover     ???????????????
     * @param videoTime //??????(????????????)
     */
    public void doSendVideo(String videoPath, String cover, String videoTime) {
        if (!CommonUtil.isBlank(videoPath)) {
            ChatMessage cm = new ChatMessage();
            cm.setContent(videoPath);
            cm.setUrl(videoPath);
            cm.setThumbnailUrl(cover);
            cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_VIDEO);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cm.setTime(df.format(new Date()));
            String clientId = CommonUtil.getStringToDate(cm.getTime()) + (int) (Math.random() * 1000);
            cm.setClientId(clientId);
            cm.setSendMessagePersonnel(BaseApplication.getUserInfo());
            cm.setReceiveMessagePersonnel(ChatFriend);
            cm.setMsgState("read");
            cm.setProgress("true");
            cm.setPrompt("false");
            cm.setSendMsgState(Constants.SEND_PROCEED);
            UniteUpdateDataModel model = new UniteUpdateDataModel();
            model.setKey(Constants.SEND_PROCEED); //????????????
            model.setValue(model.toJson(cm));
            EventBus.getDefault().postSticky(model);

        }
    }


    private void openCameraVideo() {
//        Intent intent = new Intent(IMChatActivity.this, WXCameraActivity.class);
//        intent.putExtra(WXCameraActivity.VIDEO_FILE, FileUtils.getTempPath());
//        intent.putExtra(WXCameraActivity.TYPE, JCameraView.BUTTON_STATE_BOTH);
//        startActivityForResult(intent, REQUEST_VIDEO);

//        CameraActivity.startCameraActivity(IMChatActivity.this, 0, 10, "#44bf19", JCameraView.BUTTON_STATE_BOTH, REQUEST_VIDEO);
    }


    public void lockContentHeight() { //????????????
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mBinding.chatList.getLayoutParams();
        if (BaseApplication.keyboardheight == 0) { //????????????????????????????????????
            params1.height = (int) (KeybordS.screenHeight - (ImSharedPreferences.getKeyboardHeight() +
//                    Input_box_height + getResources().getDimension(R.dimen.TitleBar_height) + getResources().getDimension(R.dimen.dp_8)));
                    Input_box_height));
        } else { //???????????????????????????
//            if (params1.weight != 0.0F) {
//                params1.height = (int) (KeybordS.screenHeight - (ImApplication.keyboardheight +
//                        Input_box_height + getResources().getDimension(R.dimen.TitleBar_height) + getResources().getDimension(R.dimen.dp_8)));
//                params1.weight = 0.0F;
//                mBinding.chatList.setLayoutParams(params1);
//            }

            params1.height = (int) (KeybordS.screenHeight - (BaseApplication.keyboardheight +
//                        Input_box_height + getResources().getDimension(R.dimen.TitleBar_height) + getResources().getDimension(R.dimen.dp_8)));
                    Input_box_height));
//            params1.weight = 0.0F;
            mBinding.chatList.setLayoutParams(params1);
            ImSharedPreferences.setValue(ImSharedPreferences.KEY_KEYBOARHEIGHT, BaseApplication.keyboardheight);
        }


    }


    public void unlockContentHeightDelayed() { //????????????
        if (mBinding.ChatEdittextView.ishide()) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.chatList.getLayoutParams();
//            params.weight = 1.0F;
            mBinding.chatList.setLayoutParams(params);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//??????????????????

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            destroyChat();
            MediaManager.pause();
            if (!mBinding.ChatEdittextView.ishide()) {
                mBinding.ChatEdittextView.setText1(8);
                unlockContentHeightDelayed();
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == RESULT_OK) {
            String path = "";
            switch (arg0) {
                case REQUEST_IMAGE:
                    compressImage(arg2);
                    break;
                case RESULT_CAMERA:
                    path = curPhotoPath;
                    doSendPic(path);
                    break;
                case REQUEST_VIDEO:
                    if (null != arg2) {
                        String type = arg2.getStringExtra("type");
                        if (type.equals("capture")) {
                            String picturePath = arg2.getStringExtra("picturePath");
                            String thumbnailPath = arg2.getStringExtra("thumbnailPath");
                            doSendPic(picturePath);
                        } else if (type.equals("record")) {
                            String videoPath = arg2.getStringExtra("videoPath");
                            String coverPath = arg2.getStringExtra("coverPath");
                            doSendVideo(videoPath, coverPath, null);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        switch (requestCode) {
            case RESULT_CAMERA:
                for (int i = 0; i < PermissionUtils.camera.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        break;
                    }
                }
                if (granted) {
                    openCamera();
                } else {
                    PermissionUtils.openAppDetails(IMChatActivity.this, "???????????????");
                }
                break;
            case REQUEST_VIDEO:
                for (int i = 0; i < PermissionUtils.camera_video.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        break;
                    }
                }
                if (granted) {
                    openCameraVideo();
                } else {
                    PermissionUtils.openAppDetails(IMChatActivity.this, "??????,???????????????");
                }
                break;
            case RESULT_AUDIO:
                for (int i = 0; i < PermissionUtils.audio.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        granted = false;
                        break;
                    }
                }
                if (granted) {
                    mBinding.ChatEdittextView.setText1(0);
                } else {
                    PermissionUtils.openAppDetails(IMChatActivity.this, "??????,???????????????");
                }
                break;
        }
    }

    @Override
    public void onFinish(float seconds, String filePath) {
        //???????????????????????????????????? ???????????????????????????
        ChatMessage message = new ChatMessage();
        message.setContentType(ChatMessage.CHAT_CONTENT_TYPE_AUDIO);
        message.setSendMessagePersonnel(BaseApplication.getUserInfo());
        message.setReceiveMessagePersonnel(ChatFriend);
        message.setUrl(filePath); //?????????URL??????
        message.setAudioState("unread"); //????????????
        int i = (int) seconds;
        message.setExtra("" + i);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //
        message.setTime(df.format(new Date())); //??????????????????
        String clientId = CommonUtil.getStringToDate(message.getTime()) + (int) (Math.random() * 1000);//
        message.setClientId(clientId); //????????????????????????
        message.setMsgState(Constants.SEND_PROCEED); //??????????????? ??????
        message.setProgress("true"); //
        message.setPrompt("false");
        message.setSendMsgState(Constants.SEND_PROCEED);
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.SEND_PROCEED); //????????????
        model.setValue(model.toJson(message));
        EventBus.getDefault().postSticky(model);
    }

    @Override
    public void onCancel() {

    }


    /**
     * ????????????
     *
     * @param imagePath
     */
    @Override
    public void onShot(String imagePath) {
        int startPosition = imagePath.lastIndexOf("/") + 1 <= 0 ? 0 : imagePath.lastIndexOf("/") + 1;
        int endPosition = imagePath.indexOf(".") <= 0 ? imagePath.length() : imagePath.indexOf(".");
        String temp = imagePath.substring(startPosition, endPosition);
        if (ImURL.equals(temp)) {
            return;
        }
        ImURL = temp;
        Date date = new Date();
        if (Screen_Capture_Date != 0 && (Screen_Capture_Date - date.getTime()) > 2000L) {
            return;
        }
        Screen_Capture_Date = date.getTime();
        ChatMessage message = new ChatMessage();
        message.setSendMessagePersonnel(BaseApplication.getUserInfo());
        message.setReceiveMessagePersonnel(ChatFriend);
        message.setContent("?????????????????????");
        message.setContentType(ChatMessage.CHAT_CONTENT_TYPE_NOTICE);
        JSONObject object = new JSONObject();
        try {
            JSONObject extra = new JSONObject();
            extra.put("content", message.content);
            object.put("type", "3");
            object.put("extra", extra.toString());
            message.setExtra(object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.setTime(df.format(new Date())); //??????????????????
        String clientId = CommonUtil.getStringToDate(message.getTime()) + (int) (Math.random() * 1000);//
        message.setClientId(clientId); //????????????????????????
        message.setMsgState("read");
        message.setProgress("true");
        message.setPrompt("false");
        message.setSendMsgState(Constants.SEND_CHATMESSAGE);
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        ChatSettingEntity entity = ChatSettingManager.getInstance().query(message.getMaster(), message.getMaster().endsWith(message.getFromuser()) ? message.getTouser() : message.getFromuser());
        if (!entity.getScreenshot()) {
            model.setKey(Constants.IMSOCKETSERVICE_SENG); //??????????????????
        } else {
            model.setKey(Constants.SEND_NOTICE); //????????????
        }
        model.setValue(model.toJson(message));
        EventBus.getDefault().postSticky(model);

    }

    /**
     * ???????????????
     *
     * @param dataBean
     * @param position
     */
    @Override
    public void onCustomItemClicked(EmotionEntity.DataBean dataBean, int position) {
        ChatMessage message = new ChatMessage();
        message.setSendMessagePersonnel(BaseApplication.getUserInfo());
        message.setReceiveMessagePersonnel(ChatFriend);
        message.setUrl(dataBean.getUrl());
        message.setContentType(ChatMessage.CHAT_CONTENT_TYPE_EMOTION);
        JSONObject extra = new JSONObject();
        try {
            extra.put("url", dataBean.getUrl());
            extra.put("width", dataBean.getWidth());
            extra.put("height", dataBean.getHeight());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        message.setExtra(extra.toString());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        message.setTime(df.format(new Date())); //??????????????????
        String clientId = CommonUtil.getStringToDate(message.getTime()) + (int) (Math.random() * 1000);//
        message.setClientId(clientId); //????????????????????????
        message.setMsgState("read");
        message.setProgress("true");
        message.setPrompt("false");

        message.setSendMsgState(Constants.SEND_CHATMESSAGE);
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.SEND_CHATMESSAGE); //????????????
        model.setValue(model.toJson(message));
        EventBus.getDefault().postSticky(model);
    }

    private void sendVideoCall(String content) {
        // ??????????????????
        if (CommonUtil.isBlank(content)) {
            return;
        }
        ChatMessage cm = new ChatMessage();
        cm.setContent("[????????????]");
        cm.setExtra(content);
        cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_CALL_VIDEO);
    }

    /**
     * ??????????????????
     *
     * @param descr
     */
    @Override
    public void onChatExtClick(String descr) {
        Log.i(TAG, "onChatExtClick: ");
//        Intent intent;
//        if (!getResources().getString(R.string.hongbao).equals(descr) && mute) {
//            ToastUtils.showShort(getApplication(), "???????????????");
//            return;
//        }
//        if (getResources().getString(R.string.picture).equals(descr)) { //??????
//            if (checkPermissions(PermissionUtils.STORAGE, RESULT_GALLERY)) {
//                intent = new Intent(IMChatActivity.this, ImMediaActivity.class);
//                intent.putExtra("userInfo", ChatFriend);
//                startActivity(intent);
//            }
//        } else if (getResources().getString(R.string.site).equals(descr)) {//??????
//            if (checkPermissions(PermissionUtils.CAMERA, RESULT_CAMERA)) {
//                openCamera();
//            }
//        } else if (getResources().getString(R.string.video).equals(descr)) {//????????????
//            if (checkPermissions(PermissionUtils.CAMERA_VIDEO, REQUEST_VIDEO)) {
//                openCameraVideo();
//            }
//        } else if (getResources().getString(R.string.location).equals(descr)) {//??????
//            intent = new Intent(IMChatActivity.this, MapLocationActivity.class);
//            intent.putExtra("userInfo", ChatFriend);
//            startActivity(intent);
//        } else if (getResources().getString(R.string.hongbao).equals(descr)) { //??????
//            intent = null;
//            if (chatType.equals(Constants.FRAGMENT_FRIEND)) { //??????
//                intent = new Intent(IMChatActivity.this, SendRedActivity.class);
//                intent.putExtra("type", Constants.FRAGMENT_FRIEND);
//            } else if (chatType.equals(Constants.FRAGMENT_GROUP)) {//??????
//                intent = new Intent(IMChatActivity.this, SendRedGroupctivity.class);
//                intent.putExtra("type", Constants.FRAGMENT_GROUP);
//            }
//            intent.putExtra("getUid", ChatFriend.getId());
//            intent.putExtra("userInfo", ChatFriend);
//            startActivity(intent);
//        } else if (getResources().getString(R.string.voicechat).equals(descr)) {//????????????
//            int netWrokState = NetUtil.getNetWorkState(this);
//            final Intent intent1;
//            if (ChatFriend.getType().equals(Constants.FRAGMENT_FRIEND)) {
//                intent1 = new Intent(IMChatActivity.this, VoiceChatActivity.class);
//                intent1.putExtra("type", Constants.VOICE_TYPE_SENG);
//                intent1.putExtra("ChatFriend", ChatFriend);
//            } else {
//                intent1 = new Intent(IMChatActivity.this, ChoiceFriendListActivity.class);
//                intent1.putExtra("type", Constants.AVCHAT_TYPE_GROUP_AUDIO);
//                intent1.putExtra("ChatFriend", ChatFriend);
//            }
//            if (netWrokState == -1) {
//                DialogManager.showDialog(IMChatActivity.this, "??????", "???????????????????????????????????????????????????", "??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                }, null, null, null);
//            } else if (netWrokState == 0) {
//                DialogManager.showDialog(IMChatActivity.this, "??????", "??????????????????????????????????????????????????????????????????????????????", "??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent1);
//                    }
//                }, "??????", null, null);
//            } else if (netWrokState == 1) {
//                startActivity(intent1);
//            }
//        } else if (getResources().getString(R.string.videochat).equals(descr)) {//????????????
//            int netWrokState;
//            Intent intent1;
//            netWrokState = NetUtil.getNetWorkState(this);
//            if (ChatFriend.getType().equals(Constants.FRAGMENT_FRIEND)) {
//                intent1 = new Intent(IMChatActivity.this, VideoAChatctivity.class);
//                intent1.putExtra("type", Constants.VIDEO_TYPE_SENG);
//                intent1.putExtra("ChatFriend", ChatFriend);
//            } else {
//                intent1 = new Intent(IMChatActivity.this, ChoiceFriendListActivity.class);
//                intent1.putExtra("type", Constants.AVCHAT_TYPE_GROUP_VIDEO);
//                intent1.putExtra("ChatFriend", ChatFriend);
//            }
//            if (netWrokState == -1) {
//                DialogManager.showDialog(IMChatActivity.this, "??????", "???????????????????????????????????????????????????", "??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                }, null, null, null);
//            } else if (netWrokState == 0) {
//                DialogManager.showDialog(IMChatActivity.this, "??????", "??????????????????????????????????????????????????????????????????????????????", "??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent1);
//                    }
//                }, "??????", null, null);
//            } else if (netWrokState == 1) {
//                startActivity(intent1);
//            }
//        } else if (getResources().getString(R.string.usercard).equals(descr)) { //??????
//            intent = new Intent(IMChatActivity.this, ChoiceFriendListActivity.class);
//            intent.putExtra("type", "namecard");
//            intent.putExtra("userInfo", ChatFriend);
//            startActivity(intent);
//        } else if (getResources().getString(R.string.collect).equals(descr)) { //??????
//            intent = new Intent(IMChatActivity.this, MyCollectionListActivity.class);
//            startActivity(intent);
//        } else if (getResources().getString(R.string.product).equals(descr)) { //??????
//            try {
//                String url = "html/wallet/publishGoods.html";
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("token", BaseApplication.getUserInfo().getToken());
//                jsonObject.put("userJson", GsonUtils.toJson(BaseApplication.getUserInfo()));
//                jsonObject.put("url", url);
//                WebPageModule.startWebModule(IMChatActivity.this, url, jsonObject.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageBus bus) {
        if (bus.getCodeType().equals(MessageBus.msgId_product)) {
            try {
                JSONObject extra = new JSONObject(String.valueOf(bus.getMessage()));
                doSendProduct(extra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * sid : ??????id
     * icon:????????????
     * uid:?????????id
     * username:???????????????
     * usericon:???????????????
     * price:??????
     * title:????????????
     */
    public void doSendProduct(JSONObject extra) {
        ChatMessage cm = new ChatMessage();
        cm.setExtra(extra.toString());
        cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_PRODUCT);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cm.setTime(df.format(new Date()));
        String clientId = CommonUtil.getStringToDate(cm.getTime()) + (int) (Math.random() * 1000);
        cm.setClientId(clientId);
        cm.setSendMessagePersonnel(BaseApplication.getUserInfo());
        cm.setReceiveMessagePersonnel(ChatFriend);
        cm.setMsgState("read");
        cm.setProgress("true");
        cm.setPrompt("false");
        cm.setSendMsgState(Constants.SEND_PROCEED);
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.SEND_CHATMESSAGE); //????????????
        model.setValue(model.toJson(cm));
        EventBus.getDefault().postSticky(model);


    }

    // ???????????????????????????task
    private class LoadMoreChat extends AsyncTask<Void, Void, List<ChatMessage>> {

        @Override
        protected List<ChatMessage> doInBackground(Void... params) {
            List<ChatMessage> moreCms = IMChatManager.getInstance(BaseApplication.getInstance()).getChatHistory(BaseApplication.getUserInfo().getId(), chatType, ChatFriend.getId(), chatMessageList.size()); //??????????????????
//            List<ChatMessage> moreCms = IMChatManager.getInstance(ImApplication.mContext).getChatHistory(ImApplication.userInfo.getId(), chatType, ChatFriend.getId(), mBinding.chatList.getAdapter().getItemCount()); //??????????????????
            return moreCms;
        }

        @Override
        protected void onPostExecute(List<ChatMessage> result) {
            if (!isFinishing()) {
                List<ChatMessage> more = result;
                if (more.size() > 0) {
                    chatMessageList.addAll(0, more);
                    mChatListAdapter.notifyDataSetChanged();
                    layoutManager.scrollToPositionWithOffset(more.size() - 1, 0, false);
                    if (more.size() >= 20) {
//                        page = page + 1;
                        isAllowLoadingMore = true;
                    } else {
                        isLoadingMore = true;
                        isAllowLoadingMore = false;
                    }
                    isLoadingMore = false;

                }
            }
        }
    }

    // ?????????????????????
    private void PointMsg() {
        ChatMessage cm = new ChatMessage();
        cm.setContent("??????????????????????????????????????????????????????????????????");
        cm.setContentType(ChatMessage.CHAT_CONTENT_TYPE_POINT);
        cm.setSendMessagePersonnel(BaseApplication.getUserInfo());
        cm.setReceiveMessagePersonnel(ChatFriend);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cm.setTime(df.format(new Date()));
        String clientId = CommonUtil.getStringToDate(cm.getTime()) + (int) (Math.random() * 1000);
        cm.setClientId(clientId);
        cm.setUsername(BaseApplication.getUserInfo().getId());
        cm.setNickname(BaseApplication.getUserInfo().getName());
        cm.setUserIcon(BaseApplication.getUserInfo().getIcon());
        cm.setSendMsgState(ChatMessage.SEND_PROCEED);
        chatMessageList.add(0, cm);
    }

    public boolean isBaseOnWidth() {
        return true;
    }

    public float getSizeInDp() {
        return 375;
    }

}