package com.quakoo.im.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.MsgCache;
import com.base.utils.PermissionUtils;
import com.base.utils.ToastUtils;
import com.google.android.material.tabs.TabLayout;
import com.quakoo.im.CustomEvents.CustomClickEvents;
import com.quakoo.im.CustomEvents.HeightVariety;
import com.quakoo.im.IMSharedPreferences.ImSharedPreferences;
import com.quakoo.im.R;
import com.quakoo.im.activity.IMChatActivity;
import com.quakoo.im.adapter.ChatExtPagerAdapter;
import com.quakoo.im.adapter.EmotionPagerAdapter;
import com.quakoo.im.databinding.ChatEdittextLayoutBinding;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.EmotionEntity;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.utils.KeybordS;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatEdittextView extends RelativeLayout {
    private Context mContext;
    private volatile int type = 0;//1是准备打字状态 2是准备语音状态 3是准备发送表情 4是打开红包 相机 相册状态
    private CustomClickEvents events;
    private ChatEdittextLayoutBinding mBinding;
    private HeightVariety mVariety;
    private UserInfo ReceiveUserInfo;
    public static volatile boolean isSettingheight = false; //标识 是否是根据弹出键盘高度来设置的
    private List<Integer> imageView = new ArrayList<Integer>();
    private BaseEmotionView baseEmotion;
    private CustomEmotionView customEmotion = null;
    private ChatExtView chatExtView = null;
    private List<View> list = new ArrayList<View>();
    private List<View> chatExtViews = new ArrayList<View>();
    private EmotionPagerAdapter mAdpter;
    private ChatExtPagerAdapter chatExtPagerAdapter;
    private TabLayout tabLayout;
    private RecyclerView chatList;
    public static final int RESULT_HINT = 900;

    public AudioRecorderButton.AudioFinishRecorderListener mRecorderListener;//录制语音的回调..

    public AudioRecorderButton.AudioFinishRecorderListener getmRecorderListener() {
        return mRecorderListener;
    }

    public void setmRecorderListener(AudioRecorderButton.AudioFinishRecorderListener mRecorderListener) {
        this.mRecorderListener = mRecorderListener;
        mBinding.voiceText.setAudioFinishRecorderListener(mRecorderListener);
    }


    public ChatEdittextView(Context context) {
        super(context);
    }

    public ChatEdittextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View mainView = LayoutInflater.from(context).inflate(R.layout.chat_edittext_layout, null);//获取布局
        mBinding = DataBindingUtil.bind(mainView);//绑定布局
        initView();

        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) mBinding.paneLayout.getLayoutParams();
        if (BaseApplication.keyboardheight == 0) { //当没有获取到键盘高度时。
            params1.height = (int) (KeybordS.screenHeight - (ImSharedPreferences.getKeyboardHeight() +
//                    (int) getResources().getDimension(R.dimen.Input_box_height) + getResources().getDimension(R.dimen.TitleBar_height)+getResources().getDimension(R.dimen.dp_8)));
                    (int) getResources().getDimension(R.dimen.Input_box_height)));
        } else { //获取到键盘高度了。
            params1.height = (int) (KeybordS.screenHeight - (BaseApplication.keyboardheight +
//                    (int) getResources().getDimension(R.dimen.Input_box_height) + getResources().getDimension(R.dimen.TitleBar_height) + getResources().getDimension(R.dimen.dp_8)));
                    (int) getResources().getDimension(R.dimen.Input_box_height)));
        }
        mBinding.paneLayout.setLayoutParams(params1);
        this.addView(mainView);
    }

    public ChatEdittextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEvents(CustomClickEvents events) {
        this.events = events;
    }

    public void initView() {
        mBinding.editYuyin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mute) {
                    ToastUtils.showShort(mContext, "您在禁言中");
                    return;
                }
                if (PermissionUtils.checkPermissionAllGranted(mContext, PermissionUtils.AUDIO)) { //询问麦克风权限
                } else {
                    PermissionUtils.requestPermissions((Activity) mContext, PermissionUtils.AUDIO, IMChatActivity.RESULT_AUDIO); //请求麦克风权限
                    return;
                }
                if (v.getTag().equals("打字")) {
                    type = 0;
                } else if (v.getTag().equals("语音")) {
                    type = 1;
                }
                setText1(type);
            }
        });
        mBinding.editEmotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                KeybordS.closeKeybord(mBinding.editTextInput, mContext);
                if (mute) {
                    ToastUtils.showShort(mContext, "您在禁言中");
                    return;
                }
                if (v.getTag().equals("默认")) {
                    type = 2;
                } else if (v.getTag().equals("发表情")) {
                    type = 3;
                }
                setText1(type);
            }
        });

        mBinding.otherMessage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals("默认")) {
                    type = 5;
                } else if (v.getTag().equals("打开")) {
                    type = 6;
                }
                setText1(type);


            }
        });

        mBinding.editTextInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type = 7;
                setText1(type);

            }
        });
        mBinding.editTextInput.requestFocus();
        mBinding.editTextInput.setFocusable(false);

        mBinding.editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (null != s) {
                    if (s.toString().endsWith("@")
                            && before == 0
                            && ReceiveUserInfo.getType().equals(Constants.FRAGMENT_GROUP)) {
//                        Intent intent = new Intent(mContext, ChoiceFriendListActivity.class);
//                        intent.putExtra("type", Constants.AITE_FRIEND); //传递这是要@
//                        intent.putExtra("userInfo", ReceiveUserInfo); //传递群组信息
//                        mContext.startActivity(intent);
                    }
                    if (s.toString().equals("")) {
//                        mBinding.sendText.setVisibility(GONE);
                        mBinding.sendText.setEnabled(false);
                        mBinding.sendText.setBackgroundResource(R.drawable.bg_text_send_t60);
                        mBinding.otherMessage.setVisibility(VISIBLE);
                    } else {
                        mBinding.sendText.setVisibility(VISIBLE);
                        mBinding.sendText.setEnabled(true);
                        mBinding.sendText.setBackgroundResource(R.drawable.bg_text_send);
                        mBinding.otherMessage.setVisibility(GONE);
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mBinding.sendText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, mBinding.editTextInput.getText().toString());
                }
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setContentType(ChatMessage.CHAT_CONTENT_TYPE_TXT);//消息类型
                chatMessage.setContent(mBinding.editTextInput.getText().toString());//内容
                chatMessage.setSendMessagePersonnel(BaseApplication.getUserInfo());//设置发送者
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                chatMessage.setTime(df.format(new Date()));
                String clientId = CommonUtil.getStringToDate(chatMessage.getTime()) + (int) (Math.random() * 1000);
                chatMessage.setClientId(clientId);
                chatMessage.setExtra(mBinding.editTextInput.getUserIdString() + "");
                chatMessage.setMsgState("read");
                chatMessage.setProgress("true");
                chatMessage.setPrompt("false");
                if (ReceiveUserInfo != null) {
                    chatMessage.setReceiveMessagePersonnel(ReceiveUserInfo);
                }
                chatMessage.setSendMsgState(Constants.SEND_CHATMESSAGE);
                UniteUpdateDataModel model = new UniteUpdateDataModel();
                model.setKey(Constants.SEND_CHATMESSAGE); //发送消息
                model.setValue(model.toJson(chatMessage));
                EventBus.getDefault().postSticky(model);
                mBinding.editTextInput.setText("");//发送信息完成.清空
            }
        });


        mBinding.pane2.photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "相片");
                }
            }
        });
        mBinding.pane2.camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "相机");
                }
            }
        });

        mBinding.pane2.video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "视频");
                }
            }
        });
        mBinding.pane2.location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "位置");
                }
            }
        });
        mBinding.pane2.hongbao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "红包");
                }
            }
        });
        //ic_voicechat
        mBinding.pane2.icVoicechat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "语音通话");
                }
            }
        });
        mBinding.pane2.icVideochat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "视频通话");
                }
            }
        });
        mBinding.pane2.icUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (events != null) {
                    events.Click(v, "个人名片");
                }
            }
        });


        mBinding.imChatExtAudioView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                KeybordS.closeKeybord(mBinding.editTextInput, mContext);
                hideChatEdittextView();
                if (events != null) {
                    events.Click(v, "语音");
                }
            }
        });
        mBinding.imChatExtPhotoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideChatEdittextView();
                if (events != null) {
                    events.Click(v, "相片");
                }
            }
        });
        mBinding.imChatExtVideoView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideChatEdittextView();
                if (events != null) {
                    events.Click(v, "相机");
                }
            }
        });
        mBinding.imChatExtCallView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideChatEdittextView();
                if (events != null) {
                    events.Click(v, "电话");
                }
            }
        });


        mBinding.editTextInput.setInputLayout(this);


        //表情初始化开始
        baseEmotion = new BaseEmotionView(mContext);
        baseEmotion.setEditText(mBinding.editTextInput);
        //自定义表情
        customEmotion = new CustomEmotionView(mContext, getEmotionList().getEmotionList());
        list.add(baseEmotion);
        imageView.add(R.drawable.ic_emotion);
//        list.add(customEmotion);
//        imageView.add(R.drawable.ic_emotion_custom);
        mAdpter = new EmotionPagerAdapter(mContext, list, imageView);
        mBinding.pane1.emotionViewpager.setAdapter(mAdpter);
        mBinding.pane1.emotionViewpager.setOffscreenPageLimit(1);
        mBinding.pane1.faceContainer.setupWithViewPager(mBinding.pane1.emotionViewpager);
        //tab布局
        for (int i = 0; i < mBinding.pane1.faceContainer.getTabCount(); i++) {
            TabLayout.Tab tab = mBinding.pane1.faceContainer.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mAdpter.getTabView(i));
            }
        }
        //tab分割线
        LinearLayout linearLayout = (LinearLayout) mBinding.pane1.faceContainer.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(mContext, R.drawable.layout_divider_vertical));

        // 扩展功能
        chatExtView = new ChatExtView(mContext);
        chatExtViews.add(chatExtView);
        chatExtPagerAdapter = new ChatExtPagerAdapter(mContext, chatExtViews);
        mBinding.pane2.chatExtViewpager.setAdapter(chatExtPagerAdapter);
        mBinding.pane2.chatExtViewpager.setOffscreenPageLimit(1);

        mBinding.muteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private boolean mute = false;

    public void setMute(boolean mute) {
        this.mute = mute;
        mBinding.muteView.setVisibility(mute ? VISIBLE : GONE);
    }

    public ChatExtView getChatExtView() {
        return chatExtView;
    }

    public CustomEmotionView getCustomEmotion() {
        return customEmotion;
    }

    public void setCustomClickEvents(CustomClickEvents events) {
        this.events = events;
    }


    int[] Images = new int[]{R.drawable.ic_im_chat_add_emotion_black, R.drawable.ic_im_chat_text_toggle_black, R.drawable.ic_im_chat_sound_toggle_black};

    public void setText1(int i) {
        Log.e("ChatEdittextView_1", "状态:" + i);
        if (i == 5) {
            if (chatList != null) {
                //setStackFromEnd
                RecyclerView.LayoutManager layoutManager = chatList.getLayoutManager();
                LinearLayoutManager manager = (LinearLayoutManager) layoutManager;
                int num = chatList.getAdapter().getItemCount(); //获取 列表最大数量
                if (manager.findLastVisibleItemPosition() <= num) {
                    manager.scrollToPositionWithOffset(num - 1, 0);
                }
            }
        }
        if (i == 0) {//默认状态,需要将图标换成 小键盘图标,隐藏输入框,显示 "按住说话" 表情图标切换至默认状态,关闭 画板 并且标记 状态为0

            mBinding.editYuyin.setTag("语音");
            mBinding.editYuyin.setImageResource(Images[1]);
            mBinding.editTextInput.setVisibility(GONE);
            mBinding.voiceText.setVisibility(VISIBLE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(GONE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.closeKeybord(mBinding.editTextInput, mContext); //关闭键盘
            if (mVariety != null) {
                mVariety.unlockContentHeightDelayed();
            }


        } else if (i == 1) {//语音状态,需要将图标换成 语音图标,输入框显示 隐藏"按住说话" 表情图标切换至默认状态,打开小键盘 并且标记 状态为1
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(GONE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.openKeybord(mBinding.editTextInput, mContext); //打开键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 2) { //发表情状态,需要将图标换成 语音图标,输入框显示 隐藏"按住说话" 表情图标切换至打开状态,关闭小键盘 并且标记 状态为2
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[1]);
            mBinding.editEmotion.setTag("发表情");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(VISIBLE);
            mBinding.emotionPanelLayout.setVisibility(VISIBLE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.closeKeybord(mBinding.editTextInput, mContext); //关闭键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 3) { //发表情状态,需要将图标换成 语音图标,输入框显示 隐藏"按住说话" 表情图标切换至打开状态,打开小键盘 并且标记 状态为3
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(INVISIBLE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(GONE);
//            KeybordS.openKeybord(mBinding.editTextInput, mContext); //打开键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 4) { //表情默认状态,需要将图标换成 语音图标,输入框显示 隐藏"按住说话" 表情图标切换至关闭状态,开启小键盘 并且标记 状态为4
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(VISIBLE);
            mBinding.emotionPanelLayout.setVisibility(VISIBLE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.openKeybord(mBinding.editTextInput, mContext); //打开键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 5) { //表情默认状态,需要将图标换成 语音图标,输入框显示, 隐藏"按住说话" 表情图标切换至关闭状态,关闭小键盘 并且标记 开启其他消息 状态为5
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("打开");
            mBinding.paneLayout.setVisibility(VISIBLE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(VISIBLE);
            KeybordS.closeKeybord(mBinding.editTextInput, mContext); //关闭键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 6) { //表情默认状态,需要将图标换成 语音图标,输入框显示, 隐藏"按住说话" 表情图标切换至关闭状态,开启小键盘 并且标记 关闭其他消息 状态为4
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(INVISIBLE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.openKeybord(mBinding.editTextInput, mContext); //打开键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 7) { //输入框点击事件触发
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(INVISIBLE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.openKeybord(mBinding.editTextInput, mContext); //打开键盘
            if (mVariety != null) {
                mVariety.lockContentHeight();
            }
        } else if (i == 8) {
            mBinding.editYuyin.setTag("打字");
            mBinding.editYuyin.setImageResource(Images[2]);
            mBinding.editTextInput.setVisibility(VISIBLE);
            mBinding.voiceText.setVisibility(GONE);
//            mBinding.editEmotion.setImageResource(Images[0]);
            mBinding.editEmotion.setTag("默认");
            mBinding.otherMessage.setTag("默认");
            mBinding.paneLayout.setVisibility(GONE);
            mBinding.emotionPanelLayout.setVisibility(GONE);
            mBinding.otherPane2Layout.setVisibility(GONE);
            KeybordS.closeKeybord(mBinding.editTextInput, mContext); //关闭键盘
        }
    }

    public void hideChatEdittextView() {
        mBinding.editEmotion.setTag("默认");
        mBinding.otherMessage.setTag("默认");
        mBinding.paneLayout.setVisibility(GONE);
        mBinding.emotionPanelLayout.setVisibility(GONE);
        mBinding.otherPane2Layout.setVisibility(GONE);
        KeybordS.closeKeybord(mBinding.editTextInput, mContext); //关闭键盘
    }

    public void setPanlheight(int height, boolean isHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mBinding.paneLayout.getLayoutParams();
        params.height = height;
        mBinding.paneLayout.setLayoutParams(params);
        invalidate();

    }

    public void setHeightVariety(HeightVariety heightVariety) {
        mVariety = heightVariety;
    }

    public boolean ishide() {
        if (type == 0) {
            return true;
        }
        return mBinding.paneLayout.getVisibility() == GONE || mBinding.paneLayout.getVisibility() == INVISIBLE;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (ishide()) {
                setText1(8);
                if (mVariety != null) {
                    mVariety.unlockContentHeightDelayed();
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //设置消息接受者
    public void setReceivePersonnel(UserInfo userInfo) {
        ReceiveUserInfo = userInfo;
    }

    public void keyboardOpen() { //由Activity监听到键盘打开事件,让控件隐藏布局
        mBinding.extLayout.setVisibility(GONE);
        mBinding.paneLayout.setVisibility(GONE);
//        if (mBinding.paneLayout.getVisibility() == GONE || mBinding.paneLayout.getVisibility() == INVISIBLE) {
//            mBinding.paneLayout.setVisibility(VISIBLE);
//        }

    }

    public void keyboardHide() {//由Activity监听到键盘隐藏事件,让控件隐藏布局
        mBinding.extLayout.setVisibility(VISIBLE);
        if (type == 0 || type == 1 || type == 3 || type == 6 || type == 7 || type == 8) {
            mBinding.paneLayout.setVisibility(GONE);
        }

    }


    public void setEmotionList(EmotionEntity emotionList) {
        MsgCache.get(mContext).put(Constants.EMOTIN_LIST + BaseApplication.getUserInfo().getId(), emotionList);
    }

    public EmotionEntity getEmotionList() {
        EmotionEntity emotionEntity = (EmotionEntity) MsgCache.get(BaseApplication.getInstance()).getAsObject(Constants.EMOTIN_LIST + BaseApplication.getUserInfo().getId());
        if (emotionEntity != null && emotionEntity.getEmotionList() != null && emotionEntity.getEmotionList().size() > 0) {
            return emotionEntity;
        } else {
            EmotionEntity emotions = new EmotionEntity();
            List<EmotionEntity.DataBean> list = new ArrayList<>();
            EmotionEntity.DataBean dataBean = new EmotionEntity.DataBean();
            dataBean.setUrl("add");
            dataBean.setStatus(false);
            list.add(dataBean);
            emotions.setEmotionList(list);
            setEmotionList(emotions);
            return emotions;
        }
    }

    public MsgEditText getEditText() {
        return mBinding.editTextInput;
    }

    public void setListView(RecyclerView chatList) {

        this.chatList = chatList;

    }

}