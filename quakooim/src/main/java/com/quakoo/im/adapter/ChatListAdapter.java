package com.quakoo.im.adapter;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.BitmapUtils;
import com.base.utils.CommonUtil;
import com.base.utils.FileUtils;
import com.base.utils.GlideLoader;
import com.base.utils.MsgCache;
import com.base.utils.ToastUtils;
import com.quakoo.im.ChatDBHelper;
import com.quakoo.im.R;
import com.quakoo.im.databinding.ItemChatListComeLayoutBinding;
import com.quakoo.im.databinding.ItemChatListNoticeLayoutBinding;
import com.quakoo.im.databinding.ItemChatListOwnLayoutBinding;
import com.quakoo.im.databinding.MessageAudioLayoutBinding;
import com.quakoo.im.databinding.MessageImageLayoutBinding;
import com.quakoo.im.databinding.MessageTextLayoutBinding;
import com.quakoo.im.databinding.MessageVideoLayoutBinding;
import com.quakoo.im.manager.ChatSettingManager;
import com.quakoo.im.manager.IMChatManager;
import com.quakoo.im.manager.MediaManager;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.quakoo.im.model.EmotionEntity;
import com.quakoo.im.model.UniteUpdateDataModel;
import com.quakoo.im.utils.DateUtil;
import com.quakoo.im.utils.IMCommonUtil;
import com.quakoo.im.utils.NetUtil;
import com.quakoo.im.utils.UnitUtils;
import com.quakoo.im.view.CompletionListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ChatMessage> messageList;
    private String TAG = "ChatListAdapter";
    private HashMap<String, String> emotionKV = null;
    private UserInfo ChatFriend;
    private Handler handler = new Handler();
    private int picMaxWidth, picMaxHeight;//图片最大的宽高 宽高 最大 199
    private int picMinWidth, picMinHeight;//图片最小的宽高 宽高 最小 199
    private int viewID = 0;
    private Long tlong = 0L;


    public ChatListAdapter(Activity context, List<ChatMessage> list, UserInfo chatFriend) {
        this.mContext = context;
        messageList = list;
        ChatFriend = chatFriend;
        picMaxWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_192);
        picMaxHeight = picMaxWidth;
        picMinWidth = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_48);
        picMinHeight = picMinWidth;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            ItemChatListOwnLayoutBinding ownLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_chat_list_own_layout, viewGroup, false);
            return new ChatViewHolder(ownLayoutBinding.getRoot());
        } else if (i == 2) {
            ItemChatListComeLayoutBinding comeLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_chat_list_come_layout, viewGroup, false);
            return new ChatViewHolder(comeLayoutBinding.getRoot());
        } else {
            ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_chat_list_notice_layout, viewGroup, false);
            return new ChatViewHolder(comeLayoutBinding.getRoot());
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (emotionKV == null) {
            emotionKV = new HashMap<String, String>();
            SQLiteDatabase emotionDatabase = ChatDBHelper.getEmotionDatabase();
            Cursor cur = emotionDatabase.rawQuery("select * from emotion", null);
            while (cur.moveToNext()) {
                String emotionDescr = cur.getString(cur.getColumnIndex("e_descr"));
                String emotionFilename = cur.getString(cur.getColumnIndex("e_name"));
                emotionKV.put(emotionDescr, emotionFilename);
            }
            cur.close();
            emotionDatabase.close();
        }
        final ChatMessage message = messageList.get(i);
        String messageinterval = "";
        DateFormat dfHour = new SimpleDateFormat("a HH:mm");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date cur = new Date();//当前时间
        Date prior = null; //前一条消息时间
        if (i == 0) {
            try {
                Date curMessage = df.parse(message.getTime());
                if (DateUtil.doFormatDate(cur, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd"))) {//当前消息与当天时间是不是同一日期
                    messageinterval = message.getTime().substring(0, message.getTime().length() - 3);
                } else {
                    messageinterval = df.format(curMessage);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            ChatMessage previousChatMessage = messageList.get(i - 1);
            try {
                Date curMessage = df.parse(message.getTime());
                prior = df.parse(previousChatMessage.getTime());
                if (DateUtil.doFormatDate(cur, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd"))) { //当前消息与当前日期是同一天
                    long t = curMessage.getTime() - prior.getTime();
                    // 间隔大于一分钟显示
                    if (t >= (1000 * 60 * 3)) {//三分显示一次时间
                        messageinterval = dfHour.format(curMessage);
                    } else {
                        messageinterval = "";
                    }
                } else if (!DateUtil.doFormatDate(cur, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd")) && DateUtil.doFormatDate(prior, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd"))) {
                    //当前消息 与 今天不是同一天,并且 当前消息 与 上一条的消息是同一天
                    long t = curMessage.getTime() - prior.getTime();
                    Log.i(TAG, "时间间隔:" + t);
                    // 间隔大于一分钟显示
                    if (t >= (1000 * 60 * 3)) {//三分显示一次时间
                        messageinterval = df.format(curMessage);
                    } else {
                        messageinterval = "";
                    }
                }
            } catch (ParseException e) {
                messageinterval = null;
                Log.i(TAG, "出错了.");
                e.printStackTrace();
            }
        }
        if (viewHolder.itemView.getTag() == null || !viewHolder.itemView.getTag().toString().equals(message.toString())) {
            viewHolder.itemView.setTag(message.toString());
            if (getItemViewType(i) == 1) { //自己发送
                ItemChatListOwnLayoutBinding ownLayoutBinding = DataBindingUtil.bind(viewHolder.itemView);
                GlideLoader.LoderAvatar(mContext, BaseApplication.getUserInfo().getIcon(), ownLayoutBinding.messageIc, 100);
                ownLayoutBinding.messageContentLayout.removeAllViews();
                ownLayoutBinding.messageContentLayout.addView(getMessageView(message, i), ownLayoutBinding.messageContentLayout.getChildCount());
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
                if (message.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_SUCCESS) || message.getSendMsgState().equals(Constants.SEND_PROCEED_SUCCEED)
                        || message.getSendMsgState().equals("succeed")) {//发送成功 或者上传成功 SEND_CHATMESSAGE_SUCCESS,SEND_PROCEED_SUCCEED 是重构版本后的发送成功,succeed 是之前数据
                    ownLayoutBinding.sendStatus.clearAnimation();
                    ownLayoutBinding.sendStatus.setVisibility(View.GONE);
                    ownLayoutBinding.sendStatus.setClickable(false);
                } else if (message.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_FAIL)) {//发送失败
                    ownLayoutBinding.sendStatus.clearAnimation();
                    ownLayoutBinding.sendStatus.setVisibility(View.VISIBLE);
                    ownLayoutBinding.sendStatus.setImageResource(R.drawable.ic_send_defeated);
                    ownLayoutBinding.sendStatus.setClickable(true);
                    ownLayoutBinding.sendStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetUtil.getNetWork(mContext)) {//检查网络情况
                                UniteUpdateDataModel model = new UniteUpdateDataModel();

                                if (messageList.size() - 1 == i) {
                                    IMChatManager.getInstance(mContext).delete(BaseApplication.getUserInfo().id, message, messageList, true);
                                } else {
                                    IMChatManager.getInstance(mContext).delete(BaseApplication.getUserInfo().id, message, messageList, false);
                                }
                                messageList.remove(message);
                                notifyDataSetChanged();

                                model.setKey(Constants.SEND_CHATMESSAGE);
                                ChatMessage chatMessage1 = message;
                                chatMessage1.setSendMsgState(Constants.SEND_CHATMESSAGE); //修改发送消息的状态
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //重新设置发送时间

                                chatMessage1.setTime(df.format(new Date()));
                                String clientId = CommonUtil.getStringToDate(chatMessage1.getTime()) + (int) (Math.random() * 1000);
                                chatMessage1.setClientId(clientId);

                                model.setValue(model.toJson(chatMessage1));
                                EventBus.getDefault().postSticky(model);
                                messageList.remove(message);
                                notifyDataSetChanged();

                            } else {
                                ToastUtils.showShort(mContext,"请检查网络情况,再重发");
                            }
                        }
                    });
                } else { //发送中
                    ownLayoutBinding.sendStatus.setVisibility(View.VISIBLE);
                    ownLayoutBinding.sendStatus.setImageResource(R.drawable.ic_loading);
                    ownLayoutBinding.sendStatus.startAnimation(animation);
                    ownLayoutBinding.sendStatus.setClickable(false);
                }
                ownLayoutBinding.messageIc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, UserInfoActivity.class);
//                        intent.putExtra("friendId", BaseApplication.getUserInfo().getId());
//                        mContext.startActivity(intent);
                    }
                });
                ownLayoutBinding.sendTime.setVisibility(View.GONE);
                if (messageinterval != null && !messageinterval.isEmpty()) {
                    ownLayoutBinding.sendTime.setText(messageinterval);
                    ownLayoutBinding.sendTime.setVisibility(View.VISIBLE);
                }
                if (message.getDestroy().equals("true")) {
                    ownLayoutBinding.destroyStatus.setVisibility(View.VISIBLE);
                } else {
                    ownLayoutBinding.destroyStatus.setVisibility(View.GONE);
                }
            } else if (getItemViewType(i) == 2) { //他人发送
                ItemChatListComeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView);
                if (message.getType().equals(Constants.FRAGMENT_GROUP)) {
                    GlideLoader.LoderAvatar(mContext, message.getAvatar(), comeLayoutBinding.messageIc, 100);
                } else {
                    GlideLoader.LoderAvatar(mContext, message.getAvatar(), comeLayoutBinding.messageIc, 100);
                }
                comeLayoutBinding.name.setText(message.getNickname());
                comeLayoutBinding.messageContentLayout.removeAllViews();
                comeLayoutBinding.messageContentLayout.addView(getMessageView(message, i), comeLayoutBinding.messageContentLayout.getChildCount());
                comeLayoutBinding.sendStatus.setVisibility(View.GONE);
                if (ChatFriend.getType().equals(Constants.FRAGMENT_GROUP)) {
                    comeLayoutBinding.name.setVisibility(View.VISIBLE);
                } else {
                    comeLayoutBinding.name.setVisibility(View.GONE);
                }

                comeLayoutBinding.messageIc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, UserInfoActivity.class);
//                        intent.putExtra("friendId", message.getUsername());
//                        mContext.startActivity(intent);
                    }
                });
                comeLayoutBinding.messageIc.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (!ChatFriend.getType().equals(Constants.FRAGMENT_GROUP)) {
                            return true;
                        }
                        UniteUpdateDataModel model = new UniteUpdateDataModel();
                        model.setKey(Constants.AITE_FRIEND);
                        JSONObject jsonObject = new JSONObject();
                        UserInfo userInfo = new UserInfo();//209083
                        userInfo.setId(message.getUsername());
                        userInfo.setName(message.getNickname());
                        List<UserInfo> selectedFriends = new ArrayList<>();
                        selectedFriends.add(userInfo);
                        try {
                            jsonObject.put("userList", selectedFriends);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        model.setValue(jsonObject.toString());
                        EventBus.getDefault().postSticky(model);
                        return true;
                    }
                });
                comeLayoutBinding.sendTime.setVisibility(View.GONE);
                if (messageinterval != null && !messageinterval.isEmpty()) {
                    comeLayoutBinding.sendTime.setText(messageinterval);
                    comeLayoutBinding.sendTime.setVisibility(View.VISIBLE);
                }
                if (message.getDestroy().equals("true")) {
                    comeLayoutBinding.destroyStatus.setVisibility(View.VISIBLE);
                } else {
                    comeLayoutBinding.destroyStatus.setVisibility(View.GONE);
                }
            } else if (getItemViewType(i) == 3) { //通知
                ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView); //通知布局
                comeLayoutBinding.time.setVisibility(View.GONE);
                if (messageinterval != null && !messageinterval.isEmpty()) {
                    comeLayoutBinding.time.setText(messageinterval);
                    comeLayoutBinding.time.setVisibility(View.VISIBLE);
                }
                comeLayoutBinding.noticeIv.setVisibility(View.GONE);
                JSONObject json = null;
                try {
                    json = new JSONObject(message.getExtra());
                    String ext_type = json.optString("type");
                    // 1、发红包；2、截屏类型；3、截屏通知；4、阅后即焚通知；5、位置消息；6、名片消息；7、分享文本连接；8、领取红包；9、通知类型；16、加群审核；10、分享小程序；11、自定义表情
                    //1,2,3,4,5,6,7,8,9,10,11,16,100
                    //只处理 2,3,4,5,8,9,16,100(暂定)
                    //2,3,4,8,9 是 直接显示
                    //16 是加群审核
                    //100是回撤消息
                    //1是红包消息
                    if (ext_type.equals("8")) {
                        comeLayoutBinding.noticeIv.setVisibility(View.VISIBLE);
                        String content = message.getContent();
//                        content = content.replaceAll(BaseApplication.getUserInfo().getName(), "你");
                        String string = json.optString("extra");
                        if (string.indexOf(BaseApplication.getUserInfo().getId()) == -1) {
                            comeLayoutBinding.getRoot().setVisibility(View.GONE);
                            return;
                        }
                        SpannableString ss = new SpannableString(content);
                        // 设置字体颜色
                        ss.setSpan(new ForegroundColorSpan(Color.RED), content.indexOf("红包"), content.indexOf("红包") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        comeLayoutBinding.body.setText(ss);
                        final JSONObject finalJson = json;
                        comeLayoutBinding.body.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                intent.putExtra("chatMessage", message);
////                                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, v, mContext.getResources().getString(R.string.title_activity_hong_bao_details));
////                                ActivityCompat.startActivity(mContext, intent, compat.toBundle());
//                                ((Activity) mContext).startActivity(intent);


                            }
                        });
                    } else if (ext_type.equals("2") || ext_type.equals("3") || ext_type.equals("4")) {
                        String s;
                        if (message.getFromuser().equals(BaseApplication.getUserInfo().getId())) {
                            s = "你" + message.getContent();
                        } else {
                            s = message.getNickname() + message.getContent();
                        }
                        comeLayoutBinding.body.setText(s);
                        ChatSettingEntity entity = ChatSettingManager.getInstance().query(message.getMaster(), message.getMaster().endsWith(message.getFromuser()) ? message.getTouser() : message.getFromuser());
//                        if ( ext_type.equals("3") && !entity.getScreenshot()){ // 等于截屏通知 外加 自己没有开启截屏通知 隐藏 //8-16修改 在ImSocketService 判断当前是否开启了截屏通知,开启获取数据,没有开启不接受数据,所有这里不需要再次判断
//                            comeLayoutBinding.getRoot().setVisibility(View.GONE);
//                        }
                    } else if (ext_type.equals("9")) {
                        comeLayoutBinding.body.setText(message.getContent());
                    } else if (ext_type.equals("16")) {
                        final JSONObject extra = new JSONObject(json.optString("extra"));
                        if (!extra.optString("GMid").equals(BaseApplication.getUserInfo().getId())) {
                            return;
                        }
                        SpannableString ss = null;
                        if (message.getContent().indexOf("已确认") == -1) {
                            String stateText = "去确认";
                            String content = message.getContent() + stateText;
                            ss = new SpannableString(content);
                            // 设置字体颜色
                            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#318c11")), content.indexOf(stateText), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            // 设置字体大小
                            ss.setSpan(new AbsoluteSizeSpan(CommonUtil.sp2px(mContext, 14)), content.indexOf(stateText), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            comeLayoutBinding.body.setText(ss);
                            comeLayoutBinding.body.setMovementMethod(LinkMovementMethod.getInstance());
                            Log.e(TAG, "onBindViewHolder: extra=" + extra);
                            comeLayoutBinding.body.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("cgid", ChatFriend.getId());
//                                    bundle.putString("inviteInfo", extra.toString());
//                                    bundle.putString("messageId", message.getServeId());
//                                    bundle.putSerializable("message", message);
//                                    Intent intent = new Intent();
//                                    intent.putExtras(bundle);
//                                    intent.setClass(mContext, InviteInfoActivity.class);
//                                    mContext.startActivity(intent);
                                }
                            });
                        } else {
                            String content = message.getContent();
                            ss = new SpannableString(content);
                            // 设置字体颜色
                            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#318c11")), content.indexOf("已确认"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            // 设置字体大小
                            ss.setSpan(new AbsoluteSizeSpan(CommonUtil.sp2px(mContext, 14)), content.indexOf("已确认"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            comeLayoutBinding.body.setText(ss);
                            comeLayoutBinding.body.setMovementMethod(LinkMovementMethod.getInstance());
                            Log.e(TAG, "onBindViewHolder: extra=" + extra);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (getItemViewType(i) == 4) {

                ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView); //消息撤回
                comeLayoutBinding.time.setVisibility(View.GONE);
                if (messageinterval != null && !messageinterval.isEmpty()) {
                    comeLayoutBinding.time.setText(messageinterval);
                    comeLayoutBinding.time.setVisibility(View.VISIBLE);
                }
                //7-6  comeLayoutBinding.body.setText("“" + message.getNickname() + "”" + message.getContent());
                //改成 comeLayoutBinding.body.setText("你" + message.getContent());
                String s = message.getNickname() + message.getContent();
                if (message.getFromuser().equals(BaseApplication.getUserInfo().getId())) {
                    s = "你" + message.getContent();
                }
                comeLayoutBinding.body.setText(s);
            } else if (getItemViewType(i) == 5) { // 锁此对话中所发送的消息都已经进行端到端的加密 布局
                ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView); //通知布局
                SpannableString spannableString = new SpannableString(message.getContent());
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_lock);
                int size = CommonUtil.sp2px(mContext, 15);
                drawable.setBounds(0, 0, size, size);
                ImageSpan imageSpan = new ImageSpan(drawable);
                spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                comeLayoutBinding.body.setText(spannableString);
                comeLayoutBinding.body.setGravity(Gravity.CENTER);
                comeLayoutBinding.bodyLayout.setBackgroundResource(R.drawable.bg_chatliat_ic_lock);
                if (!messageinterval.isEmpty()) {
                    comeLayoutBinding.time.setText(messageinterval);
                    comeLayoutBinding.time.setVisibility(View.VISIBLE);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList == null || messageList.size() == 0 || BaseApplication.getUserInfo() == null || BaseApplication.getUserInfo().getId().isEmpty()) {
        } else {
            if (messageList.get(position).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_POINT)) {//"锁此对话中所发送的消息都已经进行\n端到端的加密" 的布局
                return 5;
            } else if (messageList.get(position).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                return 4;
            }
            if (messageList.get(position).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)) {
                return 3;
            }
            if (messageList.get(position).getFromuser().equals(BaseApplication.getUserInfo().getId())) {//检查消息是否是当前用户,是选择布局 1 否则全选 布局2
                return 1;
            } else {
                return 2;
            }
        }
        return 0;

    }


    class ChatViewHolder extends RecyclerView.ViewHolder {
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getMessageView(final ChatMessage message, final int position) {
        if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)) { //文本
            View m = LinearLayout.inflate(mContext, R.layout.message_text_layout, null);
            MessageTextLayoutBinding binding = DataBindingUtil.bind(m);
            binding.mes.setText(IMCommonUtil.getExpressionString(mContext, emotionKV, message.getContent()));
            contentContainer(binding.mes, message, position);
            return m;
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PIC)) {//图片
            final View m = LinearLayout.inflate(mContext, R.layout.message_image_layout, null);
            final MessageImageLayoutBinding binding = DataBindingUtil.bind(m);
            binding.mes.finish();
            int picWidth = 0, picHeight = 0;
            String url = "";
//            if (message.getThumbnailUrl() != null) { //判断存储本地文件的字符串是否为空 ,不为空,判断本地是否存在文件,存在加载本地文件,不存在加载网络图片
            if (false) { //判断存储本地文件的字符串是否为空 ,不为空,判断本地是否存在文件,存在加载本地文件,不存在加载网络图片
                if (FileUtils.fileIsExists(message.getThumbnailUrl())) {//判断文件是否存在
                    if (binding.getRoot().getTag() == null || binding.getRoot().getTag().toString().isEmpty() || !binding.getRoot().getTag().toString().equals(message.getThumbnailUrl())) {
                        Bitmap bitmap = BitmapUtils.getBitmapFromFile(message.getThumbnailUrl());
                        picWidth = bitmap.getWidth();
                        picHeight = bitmap.getHeight();
                        int[] temp = computeWH(picWidth, picHeight);
                        picWidth = temp[0];
                        picHeight = temp[1];
                        url = message.getThumbnailUrl();
                    }
                } else {
                    if (binding.getRoot().getTag() == null || binding.getRoot().getTag().toString().isEmpty() || !binding.getRoot().getTag().toString().equals(message.getUrl())) {
                        String t = message.getUrl().substring(message.getUrl().lastIndexOf('/') + 1);
                        String[] strings = t.split("\\*");
                        try {
                            picWidth = Integer.valueOf(strings[0]);
                            picHeight = Integer.valueOf(strings[1]);
                        } catch (NumberFormatException e) {
                            picWidth = picMaxWidth;
                            picHeight = picMaxHeight;
                        }
                        int[] temp = computeWH(picWidth, picHeight);
                        picWidth = temp[0];
                        picHeight = temp[1];
                        url = message.getUrl();
                    }
                }
            } else {
                if (binding.getRoot().getTag() == null || binding.getRoot().getTag().toString().isEmpty() || !binding.getRoot().getTag().toString().equals(message.getUrl())) {
                    String t = message.getUrl().substring(message.getUrl().lastIndexOf('/') + 1);
                    String[] strings = t.split("\\*");
                    try {
                        picWidth = Integer.valueOf(strings[0]);
                        picHeight = Integer.valueOf(strings[1]);
                    } catch (NumberFormatException e) {
                        picWidth = picMaxWidth;
                        picHeight = picMaxHeight;
                    }
                    int[] temp = computeWH(picWidth, picHeight);
                    picWidth = temp[0];
                    picHeight = temp[1];
                    url = message.getUrl();
                }
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.mes.getLayoutParams();
            layoutParams.width = picWidth;
            layoutParams.height = picHeight;
            binding.mes.setLayoutParams(layoutParams);
            GlideLoader.LoderChatImage(mContext, url, binding.mes, 10);

            binding.mes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, ImImageViewDetailsActivity.class);
//                    ArrayList mlist = new ArrayList();
//                    mlist.add(messageList);
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelableArrayList("data", mlist);
//                    bundle.putSerializable("message", message);
//                    intent.putExtras(bundle);
//                    mContext.startActivity(intent);

//                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, v, mContext.getString(R.string.transitional_image));
//                    ActivityCompat.startActivity(mContext, intent, compat.toBundle());
                }
            });
            if (!message.getSendMsgState().equals(Constants.SEND_PROCEED_SUCCEED)) {
                binding.mes.setPer(message.getUpprogress());
            }
            binding.getRoot().setTag(url);
            contentContainer(binding.mes, message, position);
            return m;
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_VIDEO)) { //视频
            View m = LinearLayout.inflate(mContext, R.layout.message_video_layout, null);
            final MessageVideoLayoutBinding binding = DataBindingUtil.bind(m);
            Log.i(TAG, "getMessageView: " + message.getThumbnailUrl());
            GlideLoader.LoderChatImage(mContext, message.getThumbnailUrl(), binding.mes, 10);
            binding.mes.finish();
            binding.mes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, VideoPlayActivity.class);
//                    intent.putExtra("attachVideo", message.getUrl());
//                    intent.putExtra("thumbnail", message.getThumbnailUrl());
//                    intent.putExtra("friend", "1");
//                    mContext.startActivity(intent);

//                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, binding.mes, mContext.getResources().getString(R.string.transitional_image));
//                    ActivityCompat.startActivity(mContext, intent, compat.toBundle());

                }
            });
            if (message.getSendMsgState().equals(Constants.SEND_PROCEED_SUCCEED) || message.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_SUCCESS)) {
                binding.mes.setPer(1);
            } else {
                if (message.getFromuser().equals(BaseApplication.getUserInfo().getId())) {
                    binding.mes.setPer(message.getUpprogress());
                }
            }
            ViewGroup.LayoutParams layoutParams = m.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(
                        mContext.getResources().getDimensionPixelOffset(R.dimen.dp_95),
                        mContext.getResources().getDimensionPixelOffset(R.dimen.dp_160));
            } else {
                layoutParams.height = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_160);
                layoutParams.width = mContext.getResources().getDimensionPixelOffset(R.dimen.dp_95);
            }
            m.setLayoutParams(layoutParams);
            contentContainer(binding.mes, message, position);

            return m;
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO)) { //语音
            final View m = LinearLayout.inflate(mContext, R.layout.message_audio_layout, null);
            final MessageAudioLayoutBinding audioLayoutBinding = DataBindingUtil.bind(m);
            int temp = Double.valueOf(message.getExtra()).intValue();
            audioLayoutBinding.mes.setText(temp + "''");
            AnimationDrawable animationDrawable = null;
            if (message.getFromuser().equals(BaseApplication.getUserInfo().getId())) {//本人发送
                audioLayoutBinding.leftImg.setVisibility(View.GONE);
                audioLayoutBinding.rightImg.setVisibility(View.VISIBLE);
                animationDrawable = (AnimationDrawable) audioLayoutBinding.rightImg.getBackground();
                audioLayoutBinding.rightAudioState.setVisibility(View.GONE);
                audioLayoutBinding.leftAudioState.setVisibility(View.GONE);
            } else {
                audioLayoutBinding.leftImg.setVisibility(View.VISIBLE);
                audioLayoutBinding.rightImg.setVisibility(View.GONE);
                animationDrawable = (AnimationDrawable) audioLayoutBinding.leftImg.getBackground();
                audioLayoutBinding.leftAudioState.setVisibility(View.GONE);
                if (message.getAudioState().equals("unread")) {
                    audioLayoutBinding.rightAudioState.setVisibility(View.VISIBLE);
                } else {
                    audioLayoutBinding.rightAudioState.setVisibility(View.GONE);
                }
            }
            final AnimationDrawable finalAnimationDrawable = animationDrawable;
            audioLayoutBinding.audioLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewID != 0 || tlong != 0L) {
                        if (viewID == v.getId()) {
                            if ((new Date().getTime() - tlong) < 500) { //两个时间小于500毫秒
                                return;
                            }
                            viewID = v.getId();
                            tlong = new Date().getTime();
                        }
                    } else {
                        viewID = v.getId();
                        tlong = new Date().getTime();
                    }

                    audioLayoutBinding.rightAudioState.setVisibility(View.GONE);
                    audioLayoutBinding.leftAudioState.setVisibility(View.GONE);
                    finalAnimationDrawable.stop();
                    finalAnimationDrawable.start(); //开始播放动画
                    MediaManager.playSound(message, new CompletionListener() {
                        @Override
                        public void playInterrupt() { //中断
                            finalAnimationDrawable.stop();
                            finalAnimationDrawable.selectDrawable(0); //回到第一帧
                            message.setAudioState("read");
                            IMChatManager.getInstance(mContext).updateAudioState(message);
                        }

                        @Override
                        public void playPause() { //暂停
                            message.setAudioState("read");
                            IMChatManager.getInstance(mContext).updateAudioState(message);

                        }

                        @Override
                        public void playStart() {//开始

                        }

                        @Override
                        public void onCompletion(MediaPlayer mp) {//结束
                            finalAnimationDrawable.stop();
                            finalAnimationDrawable.selectDrawable(0); //回到第一帧
                            UniteUpdateDataModel model = new UniteUpdateDataModel();
                            model.setKey(ChatMessage.AUDIO_PLAY_STATE_COMPLETION);
                            model.setValue(model.toJson(message));
                            EventBus.getDefault().postSticky(model);
                            message.setAudioState("read");
                            IMChatManager.getInstance(mContext).updateAudioState(message);
                        }
                    });
                }

            });


            if (message.getAudioState().equals(ChatMessage.AUDIO_PLAY_STATE_START)) { //自动播放
                handler.removeCallbacks(null);
                handler.post(new Runnable() {
                    public void run() {
                        audioLayoutBinding.audioLayout.performClick();
                    }
                });
            }


            if (message.getSoundAsrState().equals("1")) {
                audioLayoutBinding.asrViewLayout.setVisibility(View.VISIBLE);
                audioLayoutBinding.asrProgress.setVisibility(View.VISIBLE);
                audioLayoutBinding.asrContect.setText("");
                audioLayoutBinding.asrContect.setVisibility(View.GONE);
                audioLayoutBinding.asrConfirm.setVisibility(View.GONE);
            } else if (message.getSoundAsrState().equals("2")) {
                audioLayoutBinding.asrViewLayout.setVisibility(View.VISIBLE);
                audioLayoutBinding.asrContect.setText(message.getSoundAsr());
                audioLayoutBinding.asrContect.setVisibility(View.VISIBLE);
                audioLayoutBinding.asrConfirm.setVisibility(View.VISIBLE);
                audioLayoutBinding.asrProgress.setVisibility(View.GONE);
            } else {
                audioLayoutBinding.asrViewLayout.setVisibility(View.GONE);
            }

            contentContainer(audioLayoutBinding.audioLayout, message, position);
            if (temp > 3) {
                int s = temp > 10 ? 10 : temp;
                LinearLayout.LayoutParams layoutParams = null;
                if (audioLayoutBinding.mes.getLayoutParams() != null) {
                    layoutParams = (LinearLayout.LayoutParams) audioLayoutBinding.mes.getLayoutParams();
                    layoutParams.width = CommonUtil.dip2px(mContext, (10 * s));

                } else {
                    layoutParams = (LinearLayout.LayoutParams) new ViewGroup.LayoutParams(CommonUtil.dip2px(mContext, (20 * s)), CommonUtil.dip2px(mContext, 40));
                }
                if (getItemViewType(position) == 1) {
                    audioLayoutBinding.mes.setGravity(Gravity.RIGHT | Gravity.CENTER);
                } else if (getItemViewType(position) == 2) {
                    audioLayoutBinding.mes.setGravity(Gravity.LEFT | Gravity.CENTER);
                }
                audioLayoutBinding.mes.setLayoutParams(layoutParams);
            }
            if (getItemViewType(position) == 1) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) audioLayoutBinding.audioLayout.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                audioLayoutBinding.audioLayout.setLayoutParams(layoutParams);
                audioLayoutBinding.audioLayout.setBackgroundResource(R.drawable.bg_im_chat_owm_sound);
                audioLayoutBinding.temp.setGravity(Gravity.END);
            } else if (getItemViewType(position) == 2) {
                audioLayoutBinding.audioLayout.setBackgroundResource(R.drawable.bg_my_collection_find);
            }
            return m;
        }




//        else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) { //红包
//
//            View m = LinearLayout.inflate(mContext, R.layout.message_hongbao_layout, null);
//            MessageHongbaoLayoutBinding binding = DataBindingUtil.bind(m);
//            binding.hongbaoDescription.setText(message.getContent());
//            Log.i(TAG, "getMessageView: getRedOpenState = " + message.getRedOpenState());
//            if (message.getRedOpenState().equals("false")) {
//                binding.openStatus.setImageResource(R.drawable.hongbao_no_open);
//                binding.hongbaoOpenLayout.setBackgroundResource(R.drawable.bg_hongbao_on_open);
//                binding.hongbaoOpenState.setVisibility(View.GONE);
//            } else {
//                binding.openStatus.setImageResource(R.drawable.hongbao_open);
//                binding.hongbaoOpenLayout.setBackgroundResource(R.drawable.bg_hongbao_open);
//                binding.hongbaoOpenState.setText(message.getRedReceiveState());
//                binding.hongbaoOpenState.setVisibility(View.VISIBLE);
//            }
//            binding.hongbaoLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    KeybordS.hideInput(mContext);
//                    SendRequest.loadRed(BaseApplication.getUserInfo().getToken(), message.getExtra(), new StringCallback() {
//                        @Override
//                        public void onError(Call call, Exception e, int id) {
//
//                        }
//
//                        @Override
//                        public void onResponse(String response, int id) {
//                            try {
//                                JSONObject object = new JSONObject(response);
//                                boolean success = object.optBoolean("success");
//                                if (success) {
//                                    JSONObject obj = object.optJSONObject("data");
//                                    RedEnvelopeEntity redEnvelope = RedEnvelopeEntity.getInstanceFromJson(obj);
//                                    if (redEnvelope.status.equals("0")) { //可以领取红包.
//                                        if (!redEnvelope.getUid.equals("0")) { //红包领取人,不是群
//                                            if (redEnvelope.uid.equals(BaseApplication.getUserInfo().getId())) { //红包发送者,等于当前登录用户
//                                                Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                                intent.putExtra("chatMessage", message);
//                                                mContext.startActivity(intent);
//                                            } else if (redEnvelope.getUid.equals(BaseApplication.getUserInfo().getId())) {//红包领取人,等于当前用户
//                                                OpenHongbaoDialog openHongbaoDialog = new OpenHongbaoDialog(mContext, message, redEnvelope);
//                                                openHongbaoDialog.setEvents(new AdapterEvents() {
//                                                    @Override
//                                                    public void Click(Object o) {
//                                                        ChatMessage chatMessage = (ChatMessage) o;
//                                                        messageList.set(position, chatMessage);
//                                                        notifyItemChanged(position);
//                                                        updateMessage(position, chatMessage);
//                                                    }
//                                                });
//                                                openHongbaoDialog.show();
//                                            } else {
//                                                ToastUtils.show("专享红包只有指定人,可以领取");
//                                            }
//                                        } else {
//                                            //可以领取
//                                            OpenHongbaoDialog openHongbaoDialog = new OpenHongbaoDialog(mContext, message, redEnvelope);
//                                            openHongbaoDialog.setEvents(new AdapterEvents() {
//                                                @Override
//                                                public void Click(Object o) {
//                                                    ChatMessage chatMessage = (ChatMessage) o;
//                                                    messageList.set(position, chatMessage);
//                                                    notifyItemChanged(position);
//                                                    updateMessage(position, chatMessage);
//                                                }
//                                            });
//                                            openHongbaoDialog.show();
//                                        }
//                                    } else if (redEnvelope.status.equals("1")) {
//                                        message.setRedOpenState("true");
//
//                                        //不可以领取 红包发送人是自己 (红包不可以领取,并且发送红包是自己)
//                                        if (redEnvelope.uid.equals(BaseApplication.getUserInfo().getId())) {
//                                            message.setRedReceiveState("已领完");
//                                            IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                            notifyItemChanged(position);
//                                            updateMessage(position, message);
//                                            Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                            intent.putExtra("chatMessage", message);
//                                            mContext.startActivity(intent);
//                                            return;
//                                        }
//                                        if (redEnvelope.getUid.equals(BaseApplication.getUserInfo().getId())) {//单人红包领取人是自己
//                                            message.setRedOpenState("true");
//                                            message.setRedReceiveState("已领完");
//                                            IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                            notifyItemChanged(position);
//                                            updateMessage(position, message);
//                                            Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                            intent.putExtra("chatMessage", message);
//                                            mContext.startActivity(intent);
//                                            return;
//                                        }
//                                        for (UserInfo userInfo : redEnvelope.users) {
//                                            if (userInfo.getId().equals(BaseApplication.getUserInfo().getId())) {
//                                                message.setRedOpenState("true");
//                                                message.setRedReceiveState("已领完");
//                                                IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                                notifyItemChanged(position);
//                                                updateMessage(position, message);
//                                                Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                                intent.putExtra("chatMessage", message);
//                                                mContext.startActivity(intent);
//                                                return;
//                                            }
//                                        }
//                                        OpenHongbaoDialog openHongbaoDialog = new OpenHongbaoDialog(mContext, message, redEnvelope);
//                                        openHongbaoDialog.setEvents(new AdapterEvents() {
//                                            @Override
//                                            public void Click(Object o) {
//                                                Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                                intent.putExtra("chatMessage", message);
//                                                mContext.startActivity(intent);
//                                            }
//                                        });
//                                        message.setRedOpenState("true");
//                                        message.setRedReceiveState("已领完");
//                                        IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                        notifyItemChanged(position);
//                                        updateMessage(position, message);
//                                        openHongbaoDialog.show();
//
//                                    } else if (redEnvelope.status.equals("2")) {
//                                        message.setRedOpenState("true");
//                                        message.setRedReceiveState("已过期");
//                                        IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                        notifyItemChanged(position);
//                                        updateMessage(position, message);
//
//                                    }
//                                } else {
//                                    String msg = object.getString("msg");
//                                    ToastUtils.show(msg);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });
//
//                }
//            });
//            return m;
//
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NAMECARD)) { //名片
//            View m = LinearLayout.inflate(mContext, R.layout.message_name_card_layout, null);
//            MessageNameCardLayoutBinding cardLayoutBinding = DataBindingUtil.bind(m);
//            final UserInfo userInfo = new UserInfo();
//            try {
//                JSONObject object = new JSONObject(message.getExtra());
//                userInfo.setName(object.getString("content"));
//                userInfo.setIcon(object.getString("icon"));
//                userInfo.setId(object.getString("uid"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            if (userInfo != null) {
//
//                cardLayoutBinding.cardIdTv.setText(userInfo.getId());
//                cardLayoutBinding.nameCardNameTv.setText(userInfo.getName());
//                GlideLoader.LoderAvatar(mContext, userInfo.getIcon(), cardLayoutBinding.nameCardAvatarImg, 100);
//                cardLayoutBinding.nameCardLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(mContext, UserInfoActivity.class);
//                        intent.putExtra("friendId", userInfo.getId());
//                        mContext.startActivity(intent);
//                    }
//                });
//            }
//            contentContainer(cardLayoutBinding.nameCardLayout, message, position);
//            return m;
//        }  else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) { //定位
//            View m = LinearLayout.inflate(mContext, R.layout.message_location_layout, null);
//            MessageLocationLayoutBinding locationLayoutBinding = DataBindingUtil.bind(m);
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(message.getExtra());
//                String address = jsonObject.getString("content");
//                String longitude = jsonObject.getString("longitude");
//                String latitude = jsonObject.getString("latitude");
//                String aoiname = jsonObject.getString("aoiname");
//                locationLayoutBinding.aoiname.setText(aoiname);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            locationLayoutBinding.address.setText(message.getContent());
//            GlideLoader.LoderLocationImage(mContext, message.getUrl(), locationLayoutBinding.mes, 4);
//            locationLayoutBinding.locationLayout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, MapPositionActivity.class);
//                    intent.putExtra("extra", message.getExtra());
//                    mContext.startActivity(intent);
//                }
//            });
//            contentContainer(locationLayoutBinding.locationLayout, message, position);
//            return m;
//
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_EMOTION)) {//自定义表情
//            final View m = LinearLayout.inflate(mContext, R.layout.message_emotion_layout, null);
//            final MessageEmotionLayoutBinding binding = DataBindingUtil.bind(m);
//            int picWidth, picHeight;
//            String t = message.getUrl().substring(message.getUrl().lastIndexOf('/') + 1);
//            String[] strings = t.split("\\*");
//            picWidth = Integer.valueOf(strings[0]);
//            picHeight = Integer.valueOf(strings[1]);
//            int[] temp = computeWH(picWidth, picHeight);
//            picWidth = temp[0];
//            picHeight = temp[1];
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.emotion.getLayoutParams();
//            layoutParams.width = picWidth;
//            layoutParams.height = picHeight;
//            binding.emotion.setLayoutParams(layoutParams);
//
//            GlideLoader.LoderUrlEmotionImage(mContext, message.getUrl(), binding.emotion);
//            binding.emotion.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, EmotionInfoActivity.class);
//                    intent.putExtra("message", message);
//                    mContext.startActivity(intent);
//                }
//            });
//
//            contentContainer(binding.emotion, message, position);
//            return m;
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PRODUCT)) {//商品
//            final View m = LinearLayout.inflate(mContext, R.layout.message_product_layout, null);
//            final MessageProductLayoutBinding binding = DataBindingUtil.bind(m);
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(message.getExtra());
//                int sid = jsonObject.optInt("sid");
//                String icon = jsonObject.getString("icon");
//                int uid = jsonObject.optInt("uid");
//                String username = jsonObject.getString("username");
//                String usericon = jsonObject.getString("usericon");
//                String price = jsonObject.getString("price");
//                String title = jsonObject.getString("title");
//                binding.titleView.setText(title);
//                GlideLoader.LoderUrlEmotionImage(mContext, icon, binding.coverView);
//                binding.userNameView.setText(username);
//                GlideLoader.LoderUrlEmotionImage(mContext, usericon, binding.userIconView);
//                binding.priceView.setText("￥" + price);
//                binding.productContainer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            String url = "html/wallet/indent.html";
//                            JSONObject jsonObject = new JSONObject();
//                            jsonObject.put("token", BaseApplication.getUserInfo().getToken());
//                            jsonObject.put("userJson", GsonUtils.toJson(BaseApplication.getUserInfo()));
//                            jsonObject.put("url", url);
//                            jsonObject.put("sid", sid);
//                            WebPageModule.startWebModule(mContext, url, jsonObject.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//            contentContainer(binding.productContainer, message, position);
//            return m;
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE)) {//分享链接
//            final View m = LinearLayout.inflate(mContext, R.layout.message_plugin_layout, null);
//            final MessagePluginLayoutBinding binding = DataBindingUtil.bind(m);
//
//            String address = "";
//            try {
//                JSONObject object = new JSONObject(message.getExtra());
//                if (!CommonUtil.isBlank(object)) {
//                    // 1.2.3.0之后版本
//                    binding.pluginsName.setText(object.optString("title"));
//                    binding.pluginsInfo.setText(object.optString("desc"));
//                    GlideLoader.LoderImage(mContext, object.optString("thumUrl"), binding.pluginsImage);
//                    if (!CommonUtil.isBlank(object.optString("icon"))) {
//                        GlideLoader.LoderImage(mContext, object.optString("icon"), binding.gameIcon);
//                    } else {
//                        GlideLoader.LoderDrawable(mContext, R.mipmap.ic_game, binding.gameIcon);
//                    }
//                    if (!CommonUtil.isBlank(object.optString("name"))) {
//                        binding.gameName.setText(object.optString("name"));
//                    } else {
//                        binding.gameName.setText(mContext.getResources().getString(R.string.app_name_game));
//                    }
//                    if (!CommonUtil.isBlank(object.optString("url"))) {
//                        address = object.optString("url");
//                    }
//                }
//                final String finalAddress = address;
//                binding.pluginsContainer.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent();
//                        intent.setData(Uri.parse(finalAddress));
//                        intent.setAction(Intent.ACTION_VIEW);
//                        mContext.startActivity(intent);
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            contentContainer(binding.pluginsContainer, message, position);
//            return m;
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE_PLUGIND)) {//分享小程序
//            final View m = LinearLayout.inflate(mContext, R.layout.message_plugin_layout, null);
//            final MessagePluginLayoutBinding binding = DataBindingUtil.bind(m);
//
//            JSONObject object = null;
//            try {
//                object = new JSONObject(message.getExtra());
//                binding.pluginsName.setText(object.optString("name"));
//                binding.pluginsInfo.setText(object.optString("info"));
//                GlideLoader.LoderImage(mContext, object.optString("image"), binding.pluginsImage);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            final JSONObject finalObject = object;
//            binding.pluginsContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PluginsEntity.DataBean dataBean = new PluginsEntity.DataBean();
//                    dataBean.setId(finalObject.optInt("pid"));
//                    dataBean.setName(finalObject.optString("name"));
//                    dataBean.setInfo(finalObject.optString("info"));
//                    dataBean.setIcon(finalObject.optString("icon"));
//                    dataBean.setImage(finalObject.optString("image"));
//                    dataBean.setExtra(finalObject.optString("extra"));
//                    PluginsManager.getInstance().openPluginsInfo((Activity) mContext, dataBean, ChatFriend.getId());
//                }
//            });
//            contentContainer(binding.pluginsContainer, message, position);
//            return m;
//        }

        else {
            View m = LinearLayout.inflate(mContext, R.layout.message_text_layout, null);
            return new View(mContext);
        }

    }

    private void updateMessage(int position, ChatMessage message) {
        Log.i(TAG, " position = " + position + " ; getRedOpenState = " + message.getRedOpenState());
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.CHAT_UPDATE_MESSAGE);
        model.setValue(model.toJson(message));
        EventBus.getDefault().postSticky(model);
    }

    private void contentContainer(View view, final ChatMessage message, final int position) {
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                ChatOnLongDialog onLongDialog = new ChatOnLongDialog(mContext, message);
//                onLongDialog.show();
//                onLongDialog.setmEvents(new OnClickEvents() {
//                    @Override
//                    public void onClick(final View view, Object object) {
//                        final ChatMessage chatMessage = (ChatMessage) object;
//                        switch (view.getId()) {
//                            case R.id.content_clipboard:
//                                CommonUtil.contentClipboard(mContext, chatMessage);
//                                break;
//                            case R.id.send_message:
//                                Intent intent = new Intent(mContext, ShareChatListAvtivity.class);
//                                intent.putExtra("type", Constants.TYPE_ZHUANFA);
//                                Bundle bundle = new Bundle();
//                                bundle.putSerializable("ChatMessage", chatMessage);
//                                intent.putExtras(bundle);
//                                mContext.startActivity(intent);
//                                break;
//                            case R.id.add_emotion:
//                                EmotionEntity.DataBean dataBean = new EmotionEntity.DataBean();
//                                dataBean.setThumbnail(chatMessage.getUrl());
//                                dataBean.setUrl(chatMessage.getUrl());
//                                EmotionEntity emotions = getEmotionList();
//                                emotions.getEmotionList().add(dataBean);
//                                setEmotionList(emotions);
//                                break;
//                            case R.id.conversion_text_tv:
//                                message.setSoundAsrState("1");
//                                notifyDataSetChanged();
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        String soundPath = FileUtils.getChatPath() + BaseApplication.getUserInfo().id + File.separator;
//                                        String soundName = message.getUrl().substring(message.getUrl().lastIndexOf("/") + 1);
//                                        // 查看该url是否已经下载
//                                        if (!new File(soundPath + soundName).exists()) {
//                                            FileUtils.DownLoadSound(message.getUrl(), soundPath + soundName);
//                                        }
//                                        String asrString = ChatASRManager.chatASR(soundPath + soundName);
//                                        if (!CommonUtil.isBlank(asrString)) {
//                                            message.setSoundAsr(asrString);
//                                            message.setSoundAsrState("2");
//                                            mContext.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    notifyDataSetChanged();
//                                                }
//                                            });
//                                        } else {
//                                            message.setSoundAsrState("3");
//                                            mContext.runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    notifyDataSetChanged();
//                                                    com.queke.baseim.utils.ToastUtils.showShort(ImApplication.getInstance(), "转文字失败");
//                                                }
//                                            });
//                                        }
//                                    }
//                                }).start();
//                                break;
//                            case R.id.delete_tv:
//
//
//                                SettingsDialog settingsDialog = new SettingsDialog(mContext);
//                                settingsDialog.setEvents(new CustomClickEvents() {
//                                    @Override
//                                    public void Click(View view, String tag) {
//                                        if (tag.equals("确认")) {
//                                            if (CommonUtil.isBlank(chatMessage.getServeId())) {
//                                                if (messageList.size() - 1 == position) {
//                                                    IMChatManager.getInstance(mContext).delete(BaseApplication.getUserInfo().id, chatMessage, messageList, true);
//                                                } else {
//                                                    IMChatManager.getInstance(mContext).delete(BaseApplication.getUserInfo().id, chatMessage, messageList, false);
//                                                }
//                                                messageList.remove(chatMessage);
//                                                notifyDataSetChanged();
//                                            } else {
//                                                chatMessage.setContentType(ChatMessage.CHAT_CONTENT_TYPE_DELETEMSG);
//                                                UniteUpdateDataModel model = new UniteUpdateDataModel();
//                                                model.setKey(Constants.SEND_DELETEMSG);
//                                                model.setValue(model.toJson(chatMessage));
//                                                EventBus.getDefault().postSticky(model);
//                                            }
//                                        }
//
//                                    }
//                                });
//                                settingsDialog.setTitle("是否删除该条消息？");
//                                settingsDialog.setTextViewTextColor(settingsDialog.determineTextView, "#127EF1");
//                                settingsDialog.setTextViewTextColor(settingsDialog.cancelTextView, "#127EF1");
//                                settingsDialog.show();
//
//                                break;
//                            case R.id.recall_message:
//                                chatMessage.setContentType(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG);
//                                chatMessage.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                                String clientId = CommonUtil.getStringToDate(chatMessage.getTime()) + (int) (Math.random() * 100);
//                                chatMessage.setContent("撤回了一条消息");
//                                chatMessage.setClientId(clientId);
//                                UniteUpdateDataModel model = new UniteUpdateDataModel();
//                                model.setKey(Constants.SEND_RECALLMSG);
//                                model.setValue(model.toJson(chatMessage));
//                                EventBus.getDefault().postSticky(model);
//                                break;
//                            case R.id.collection_tv:
//                                IMChatManager.getInstance(mContext).chatCollect(BaseApplication.getUserInfo().id, chatMessage);
//                                mContext.runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        DialogManager.ShowCollectionDialog(mContext);
//                                    }
//                                });
//                                break;
//                            default:
//                                break;
//                        }
//                    }
//                });
//                return false;
//            }
//        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (payloads == null || payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            //仅更新个别控件
            onBindViewHolder(holder, position);
        }
    }


    public int[] computeWH(int picWidth, int picHeight) {

        int max = picWidth >= picHeight ? picWidth : picHeight;//取宽高中最大的值
        if (picWidth > 0 && picHeight > 0) {
            if (max == picWidth) {//最大值是宽
                if (max >= picMaxWidth) { //宽 大于或者等于 最大值
                    picHeight = (picMaxWidth * picHeight) / picWidth;
                    picWidth = picMaxWidth;
                } else {
                    if ((picHeight < picMinHeight) || (picWidth < picMinWidth)) {
                        picHeight = (picMaxWidth * 3 / 4 * picHeight) / picWidth;
                        picWidth = picMaxWidth * 3 / 4;
                    }
                }
            } else { //最大值是 高
                if (max >= picMaxHeight) { //高 大于或者等于最大值
                    picWidth = (picMaxWidth * picWidth) / picHeight;
                    picHeight = picMaxHeight;
                } else {
                    if ((picHeight < picMinHeight) || (picWidth < picMinWidth)) {
                        picWidth = (picMaxHeight * 3 / 4 * picWidth) / picHeight;
                        picHeight = picMaxHeight * 3 / 4;
                    }
                }
            }
        }
//        picHeight = picHeight < picMinHeight ? picMinHeight : picHeight;
//        picWidth = picWidth < picMinWidth ? picMinWidth : picWidth;
        int[] ints = new int[]{picWidth, picHeight};
        return ints;
    }

    public void setEmotionList(EmotionEntity emotionList) {
        MsgCache.get(BaseApplication.getInstance()).put(Constants.EMOTIN_LIST + BaseApplication.getInstance().getUserInfo().getId(), emotionList);
        UniteUpdateDataModel model = new UniteUpdateDataModel();
        model.setKey(Constants.UPDATE_EMOTION);
        EventBus.getDefault().postSticky(model);
    }

    public EmotionEntity getEmotionList() {
        EmotionEntity emotionEntity = (EmotionEntity) MsgCache.get(BaseApplication.getInstance()).getAsObject(Constants.EMOTIN_LIST + BaseApplication.getInstance().getUserInfo().getId());
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

}
