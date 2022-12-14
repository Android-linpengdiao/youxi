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
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.quakoo.im.databinding.MessagePluginLayoutBinding;
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
import java.util.Arrays;
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
    private int picMaxWidth, picMaxHeight;//????????????????????? ?????? ?????? 199
    private int picMinWidth, picMinHeight;//????????????????????? ?????? ?????? 199
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
        Date cur = new Date();//????????????
        Date prior = null; //?????????????????????
        if (i == 0) {
            try {
                Date curMessage = df.parse(message.getTime());
                if (DateUtil.doFormatDate(cur, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd"))) {//????????????????????????????????????????????????
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
                if (DateUtil.doFormatDate(cur, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd"))) { //???????????????????????????????????????
                    long t = curMessage.getTime() - prior.getTime();
                    // ???????????????????????????
                    if (t >= (1000 * 60 * 3)) {//????????????????????????
                        messageinterval = dfHour.format(curMessage);
                    } else {
                        messageinterval = "";
                    }
                } else if (!DateUtil.doFormatDate(cur, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd")) && DateUtil.doFormatDate(prior, "yyyy-MM-dd").equals(DateUtil.doFormatDate(curMessage, "yyyy-MM-dd"))) {
                    //???????????? ??? ?????????????????????,?????? ???????????? ??? ??????????????????????????????
                    long t = curMessage.getTime() - prior.getTime();
                    Log.i(TAG, "????????????:" + t);
                    // ???????????????????????????
                    if (t >= (1000 * 60 * 3)) {//????????????????????????
                        messageinterval = df.format(curMessage);
                    } else {
                        messageinterval = "";
                    }
                }
            } catch (ParseException e) {
                messageinterval = null;
                Log.i(TAG, "?????????.");
                e.printStackTrace();
            }
        }
        if (viewHolder.itemView.getTag() == null || !viewHolder.itemView.getTag().toString().equals(message.toString())) {
            viewHolder.itemView.setTag(message.toString());
            if (getItemViewType(i) == 1) { //????????????
                ItemChatListOwnLayoutBinding ownLayoutBinding = DataBindingUtil.bind(viewHolder.itemView);
                GlideLoader.LoderAvatar(mContext, BaseApplication.getUserInfo().getIcon(), ownLayoutBinding.messageIc, 100);
                ownLayoutBinding.messageContentLayout.removeAllViews();
                ownLayoutBinding.messageContentLayout.addView(getMessageView(message, i), ownLayoutBinding.messageContentLayout.getChildCount());
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
                if (message.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_SUCCESS) || message.getSendMsgState().equals(Constants.SEND_PROCEED_SUCCEED)
                        || message.getSendMsgState().equals("succeed")) {//???????????? ?????????????????? SEND_CHATMESSAGE_SUCCESS,SEND_PROCEED_SUCCEED ?????????????????????????????????,succeed ???????????????
                    ownLayoutBinding.sendStatus.clearAnimation();
                    ownLayoutBinding.sendStatus.setVisibility(View.GONE);
                    ownLayoutBinding.sendStatus.setClickable(false);
                } else if (message.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_FAIL)) {//????????????
                    ownLayoutBinding.sendStatus.clearAnimation();
                    ownLayoutBinding.sendStatus.setVisibility(View.VISIBLE);
                    ownLayoutBinding.sendStatus.setImageResource(R.drawable.ic_send_defeated);
                    ownLayoutBinding.sendStatus.setClickable(true);
                    ownLayoutBinding.sendStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetUtil.getNetWork(mContext)) {//??????????????????
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
                                chatMessage1.setSendMsgState(Constants.SEND_CHATMESSAGE); //???????????????????????????
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //????????????????????????

                                chatMessage1.setTime(df.format(new Date()));
                                String clientId = CommonUtil.getStringToDate(chatMessage1.getTime()) + (int) (Math.random() * 1000);
                                chatMessage1.setClientId(clientId);

                                model.setValue(model.toJson(chatMessage1));
                                EventBus.getDefault().postSticky(model);
                                messageList.remove(message);
                                notifyDataSetChanged();

                            } else {
                                ToastUtils.showShort(mContext, "?????????????????????,?????????");
                            }
                        }
                    });
                } else { //?????????
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
            } else if (getItemViewType(i) == 2) { //????????????
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
            } else if (getItemViewType(i) == 3) { //??????
                ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView); //????????????
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
                    // 1???????????????2??????????????????3??????????????????4????????????????????????5??????????????????6??????????????????7????????????????????????8??????????????????9??????????????????16??????????????????10?????????????????????11??????????????????
                    //1,2,3,4,5,6,7,8,9,10,11,16,100
                    //????????? 2,3,4,5,8,9,16,100(??????)
                    //2,3,4,8,9 ??? ????????????
                    //16 ???????????????
                    //100???????????????
                    //1???????????????
                    if (ext_type.equals("8")) {
                        comeLayoutBinding.noticeIv.setVisibility(View.VISIBLE);
                        String content = message.getContent();
//                        content = content.replaceAll(BaseApplication.getUserInfo().getName(), "???");
                        String string = json.optString("extra");
                        if (string.indexOf(BaseApplication.getUserInfo().getId()) == -1) {
                            comeLayoutBinding.getRoot().setVisibility(View.GONE);
                            return;
                        }
                        SpannableString ss = new SpannableString(content);
                        // ??????????????????
                        ss.setSpan(new ForegroundColorSpan(Color.RED), content.indexOf("??????"), content.indexOf("??????") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
                            s = "???" + message.getContent();
                        } else {
                            s = message.getNickname() + message.getContent();
                        }
                        comeLayoutBinding.body.setText(s);
                        ChatSettingEntity entity = ChatSettingManager.getInstance().query(message.getMaster(), message.getMaster().endsWith(message.getFromuser()) ? message.getTouser() : message.getFromuser());
//                        if ( ext_type.equals("3") && !entity.getScreenshot()){ // ?????????????????? ?????? ?????????????????????????????? ?????? //8-16?????? ???ImSocketService ???????????????????????????????????????,??????????????????,???????????????????????????,?????????????????????????????????
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
                        if (message.getContent().indexOf("?????????") == -1) {
                            String stateText = "?????????";
                            String content = message.getContent() + stateText;
                            ss = new SpannableString(content);
                            // ??????????????????
                            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#318c11")), content.indexOf(stateText), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            // ??????????????????
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
                            // ??????????????????
                            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#318c11")), content.indexOf("?????????"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            // ??????????????????
                            ss.setSpan(new AbsoluteSizeSpan(CommonUtil.sp2px(mContext, 14)), content.indexOf("?????????"), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            comeLayoutBinding.body.setText(ss);
                            comeLayoutBinding.body.setMovementMethod(LinkMovementMethod.getInstance());
                            Log.e(TAG, "onBindViewHolder: extra=" + extra);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (getItemViewType(i) == 4) {

                ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView); //????????????
                comeLayoutBinding.time.setVisibility(View.GONE);
                if (messageinterval != null && !messageinterval.isEmpty()) {
                    comeLayoutBinding.time.setText(messageinterval);
                    comeLayoutBinding.time.setVisibility(View.VISIBLE);
                }
                //7-6  comeLayoutBinding.body.setText("???" + message.getNickname() + "???" + message.getContent());
                //?????? comeLayoutBinding.body.setText("???" + message.getContent());
                String s = message.getNickname() + message.getContent();
                if (message.getFromuser().equals(BaseApplication.getUserInfo().getId())) {
                    s = "???" + message.getContent();
                }
                comeLayoutBinding.body.setText(s);
            } else if (getItemViewType(i) == 5) { // ?????????????????????????????????????????????????????????????????? ??????
                ItemChatListNoticeLayoutBinding comeLayoutBinding = DataBindingUtil.bind(viewHolder.itemView); //????????????
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
            if (messageList.get(position).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_POINT)) {//"????????????????????????????????????????????????\n??????????????????" ?????????
                return 5;
            } else if (messageList.get(position).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG)) {
                return 4;
            }
            if (messageList.get(position).getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE)) {
                return 3;
            }
            if (messageList.get(position).getFromuser().equals(BaseApplication.getUserInfo().getId())) {//?????????????????????????????????,??????????????? 1 ???????????? ??????2
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
        if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_TXT)) { //??????
            View m = LinearLayout.inflate(mContext, R.layout.message_text_layout, null);
            MessageTextLayoutBinding binding = DataBindingUtil.bind(m);
            binding.mes.setText(IMCommonUtil.getExpressionString(mContext, emotionKV, message.getContent()));
            contentContainer(binding.mes, message, position);
            return m;
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PIC)) {//??????
            final View m = LinearLayout.inflate(mContext, R.layout.message_image_layout, null);
            final MessageImageLayoutBinding binding = DataBindingUtil.bind(m);
            binding.mes.finish();
            int picWidth = 0, picHeight = 0;
            String url = "";
//            if (message.getThumbnailUrl() != null) { //???????????????????????????????????????????????? ,?????????,??????????????????????????????,????????????????????????,???????????????????????????
            if (false) { //???????????????????????????????????????????????? ,?????????,??????????????????????????????,????????????????????????,???????????????????????????
                if (FileUtils.fileIsExists(message.getThumbnailUrl())) {//????????????????????????
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
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_VIDEO)) { //??????
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
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_AUDIO)) { //??????
            final View m = LinearLayout.inflate(mContext, R.layout.message_audio_layout, null);
            final MessageAudioLayoutBinding audioLayoutBinding = DataBindingUtil.bind(m);
            int temp = Double.valueOf(message.getExtra()).intValue();
            audioLayoutBinding.mes.setText(temp + "''");
            AnimationDrawable animationDrawable = null;
            if (message.getFromuser().equals(BaseApplication.getUserInfo().getId())) {//????????????
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
                            if ((new Date().getTime() - tlong) < 500) { //??????????????????500??????
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
                    finalAnimationDrawable.start(); //??????????????????
                    MediaManager.playSound(message, new CompletionListener() {
                        @Override
                        public void playInterrupt() { //??????
                            finalAnimationDrawable.stop();
                            finalAnimationDrawable.selectDrawable(0); //???????????????
                            message.setAudioState("read");
                            IMChatManager.getInstance(mContext).updateAudioState(message);
                        }

                        @Override
                        public void playPause() { //??????
                            message.setAudioState("read");
                            IMChatManager.getInstance(mContext).updateAudioState(message);

                        }

                        @Override
                        public void playStart() {//??????

                        }

                        @Override
                        public void onCompletion(MediaPlayer mp) {//??????
                            finalAnimationDrawable.stop();
                            finalAnimationDrawable.selectDrawable(0); //???????????????
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


            if (message.getAudioState().equals(ChatMessage.AUDIO_PLAY_STATE_START)) { //????????????
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
        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE_PLUGIND)) {//???????????????
            final View m = LinearLayout.inflate(mContext, R.layout.message_plugin_layout, null);
            final MessagePluginLayoutBinding binding = DataBindingUtil.bind(m);

            JSONObject object = null;
            try {
                object = new JSONObject(message.getExtra());
                binding.pluginsName.setText(object.optString("name"));
                binding.pluginsInfo.setText(object.optString("info"));
                GlideLoader.getInstance().LoaderImage(mContext, object.optString("image"), binding.pluginsImage,6);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            binding.tagRecyclerView.setLayoutManager(layoutManager);
            PluginTagAdapter tagAdapter = new PluginTagAdapter(mContext);
            binding.tagRecyclerView.setAdapter(tagAdapter);
            tagAdapter.refreshData(Arrays.asList("5???","??????","??????"));

            final JSONObject finalObject = object;
            binding.pluginsContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    PluginsEntity.DataBean dataBean = new PluginsEntity.DataBean();
//                    dataBean.setId(finalObject.optInt("pid"));
//                    dataBean.setName(finalObject.optString("name"));
//                    dataBean.setInfo(finalObject.optString("info"));
//                    dataBean.setIcon(finalObject.optString("icon"));
//                    dataBean.setImage(finalObject.optString("image"));
//                    dataBean.setExtra(finalObject.optString("extra"));
//                    PluginsManager.getInstance().openPluginsInfo((Activity) mContext, dataBean, ChatFriend.getId());
                }
            });
            contentContainer(binding.pluginsContainer, message, position);
            return m;
        }


//        else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RED)) { //??????
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
//                                    if (redEnvelope.status.equals("0")) { //??????????????????.
//                                        if (!redEnvelope.getUid.equals("0")) { //???????????????,?????????
//                                            if (redEnvelope.uid.equals(BaseApplication.getUserInfo().getId())) { //???????????????,????????????????????????
//                                                Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                                intent.putExtra("chatMessage", message);
//                                                mContext.startActivity(intent);
//                                            } else if (redEnvelope.getUid.equals(BaseApplication.getUserInfo().getId())) {//???????????????,??????????????????
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
//                                                ToastUtils.show("???????????????????????????,????????????");
//                                            }
//                                        } else {
//                                            //????????????
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
//                                        //??????????????? ???????????????????????? (?????????????????????,???????????????????????????)
//                                        if (redEnvelope.uid.equals(BaseApplication.getUserInfo().getId())) {
//                                            message.setRedReceiveState("?????????");
//                                            IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                            notifyItemChanged(position);
//                                            updateMessage(position, message);
//                                            Intent intent = new Intent(mContext, HongBaoDetailsActivity.class);
//                                            intent.putExtra("chatMessage", message);
//                                            mContext.startActivity(intent);
//                                            return;
//                                        }
//                                        if (redEnvelope.getUid.equals(BaseApplication.getUserInfo().getId())) {//??????????????????????????????
//                                            message.setRedOpenState("true");
//                                            message.setRedReceiveState("?????????");
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
//                                                message.setRedReceiveState("?????????");
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
//                                        message.setRedReceiveState("?????????");
//                                        IMChatManager.getInstance(BaseApplication.getInstance()).updateRedOpenState(BaseApplication.getUserInfo().getId(), message);
//                                        notifyItemChanged(position);
//                                        updateMessage(position, message);
//                                        openHongbaoDialog.show();
//
//                                    } else if (redEnvelope.status.equals("2")) {
//                                        message.setRedOpenState("true");
//                                        message.setRedReceiveState("?????????");
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
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NAMECARD)) { //??????
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
//        }  else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_LOCATION)) { //??????
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
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_EMOTION)) {//???????????????
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
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_PRODUCT)) {//??????
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
//                binding.priceView.setText("???" + price);
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
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE)) {//????????????
//            final View m = LinearLayout.inflate(mContext, R.layout.message_plugin_layout, null);
//            final MessagePluginLayoutBinding binding = DataBindingUtil.bind(m);
//
//            String address = "";
//            try {
//                JSONObject object = new JSONObject(message.getExtra());
//                if (!CommonUtil.isBlank(object)) {
//                    // 1.2.3.0????????????
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
//        } else if (message.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_SHARE_PLUGIND)) {//???????????????
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
//                                        // ?????????url??????????????????
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
//                                                    com.queke.baseim.utils.ToastUtils.showShort(ImApplication.getInstance(), "???????????????");
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
//                                        if (tag.equals("??????")) {
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
//                                settingsDialog.setTitle("???????????????????????????");
//                                settingsDialog.setTextViewTextColor(settingsDialog.determineTextView, "#127EF1");
//                                settingsDialog.setTextViewTextColor(settingsDialog.cancelTextView, "#127EF1");
//                                settingsDialog.show();
//
//                                break;
//                            case R.id.recall_message:
//                                chatMessage.setContentType(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG);
//                                chatMessage.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//                                String clientId = CommonUtil.getStringToDate(chatMessage.getTime()) + (int) (Math.random() * 100);
//                                chatMessage.setContent("?????????????????????");
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
            //?????????????????????
            onBindViewHolder(holder, position);
        }
    }


    public int[] computeWH(int picWidth, int picHeight) {

        int max = picWidth >= picHeight ? picWidth : picHeight;//????????????????????????
        if (picWidth > 0 && picHeight > 0) {
            if (max == picWidth) {//???????????????
                if (max >= picMaxWidth) { //??? ?????????????????? ?????????
                    picHeight = (picMaxWidth * picHeight) / picWidth;
                    picWidth = picMaxWidth;
                } else {
                    if ((picHeight < picMinHeight) || (picWidth < picMinWidth)) {
                        picHeight = (picMaxWidth * 3 / 4 * picHeight) / picWidth;
                        picWidth = picMaxWidth * 3 / 4;
                    }
                }
            } else { //???????????? ???
                if (max >= picMaxHeight) { //??? ???????????????????????????
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
