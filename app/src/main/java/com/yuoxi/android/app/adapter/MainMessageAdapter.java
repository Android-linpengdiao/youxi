package com.yuoxi.android.app.adapter;

import android.content.Context;
import android.view.View;

import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.GlideLoader;
import com.base.view.OnClickListener;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.model.ChatSettingEntity;
import com.yuoxi.android.app.MainApplication;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ItemMainMessageBinding;
import com.yuoxi.android.app.databinding.ItemYuewanGridBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 消息 - 消息
 */
public class MainMessageAdapter extends BaseRecyclerAdapter<ChatMessage, ItemMainMessageBinding> {

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MainMessageAdapter(Context context) {
        super(context);
    }

    public MainMessageAdapter(Context context, List list) {
        super(context, list);
        mContext = context;
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.item_main_message;
    }

    @Override
    protected void onBindItem(ItemMainMessageBinding binding, ChatMessage chatMessage, int position) {

        binding.chatHint.setVisibility(View.GONE);
        UserInfo userInfo = null;
        ChatSettingEntity entity = null;
        String uid = "";

        if (chatMessage.getType().equals(Constants.FRAGMENT_GROUP)) {
            if (chatMessage.getHintState().equals("unread") && chatMessage.getUnreadCount() > 0) {
                binding.chatHint.setVisibility(View.VISIBLE);
            } else {
                binding.chatHint.setVisibility(View.GONE);
            }
        }
        if (chatMessage.getType().equals(Constants.FRAGMENT_GROUP) || chatMessage.getType().equals(Constants.FRAGMENT_FRIEND)) {
            userInfo = chatMessage.getFriendUser();
            if (binding.getRoot().getTag() == null || binding.getRoot().getTag().toString().isEmpty() || !binding.getRoot().getTag().equals(userInfo.getIcon())) {
                GlideLoader.getInstance().LoaderUserIcon(mContext, userInfo.getIcon(), binding.userIconView);
                binding.getRoot().setTag(userInfo.getIcon());
            }
        } else if (chatMessage.getType().equals(Constants.FRAGMENT_NOTICE)) {
            if (binding.getRoot().getTag() == null || binding.getRoot().getTag().toString().isEmpty() || !binding.getRoot().getTag().equals(chatMessage.getAvatar())|| !binding.getRoot().getTag().equals(R.mipmap.ic_message_system)) {
                if (!chatMessage.getFromuser().equals("0")) {
                    userInfo = chatMessage.getFriendUser();
                    GlideLoader.getInstance().LoaderUserIcon(mContext, userInfo.getIcon(), binding.userIconView);
                    binding.getRoot().setTag(userInfo.getIcon());
                } else {
                    GlideLoader.getInstance().LoaderUserIcon(mContext,null,binding.userIconView);
                    binding.getRoot().setTag(R.mipmap.ic_message_system);
                }
            }
        }
        entity = chatMessage.getSettingEntity();
        if (onClickListener != null) {
            binding.viewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(v,mList.get(position));
                    chatMessage.setUnreadCount(0);
                    notifyItemChanged(position, "");
                }
            });
            binding.viewLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onClickListener.onLongClick(v,chatMessage);
                    return true;
                }
            });
        }
        binding.noMsgTip.setVisibility(View.GONE);
        //设置消息免打扰
        if (entity != null && entity.getShield()) {
            binding.noMsgTip.setVisibility(View.VISIBLE);
        }
        //未读消息设置
        if (chatMessage.getUnreadCount() > 0) { //存在未读消息
            binding.unreadCount.setVisibility(View.VISIBLE);
            if (entity != null && entity.getShield()) { //10
                binding.unreadCount.setVisibility(View.GONE);
                binding.unreadCount1.setVisibility(View.VISIBLE);
//                    binding.unreadCount.setVisibility(View.GONE);
            } else {
                binding.unreadCount.setVisibility(View.VISIBLE);
                binding.unreadCount1.setVisibility(View.GONE);
                if (chatMessage.getUnreadCount() > 99) { //18
                    binding.unreadCount.setText("99⁺");//⁺
                } else {
                    binding.unreadCount.setText("" + chatMessage.getUnreadCount());
//                        binding.unreadCount.setText("99⁺");//⁺

                }
            }
        } else {
            binding.unreadCount.setVisibility(View.GONE);
            binding.unreadCount1.setVisibility(View.GONE);
        }
        // 结束
        if (chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())) {//自己
            binding.nickname.setText(chatMessage.getTousernick()); //名字
            binding.remarks.setText(chatMessage.getTouserremark()); //昵称
        } else { //别人
            binding.nickname.setText(chatMessage.getFromusernick()); //名字
            binding.remarks.setText(chatMessage.getFromuserremark()); //昵称
        }
        if (chatMessage.getFromuserremark() == null || chatMessage.getFromuserremark().equals("")) {
            binding.nickname.setVisibility(View.VISIBLE);
            binding.remarks.setVisibility(View.GONE);
        } else {
            binding.nickname.setVisibility(View.GONE);
            binding.remarks.setVisibility(View.VISIBLE);
        }
        if (chatMessage.getFromuser().equals("0")) {
            binding.nickname.setText(mContext.getResources().getString(R.string.app_name_num_aid)); //名字
            binding.remarks.setText(mContext.getResources().getString(R.string.app_name_num_aid)); //昵称

        }
        if (chatMessage.getType().equals(Constants.FRAGMENT_GROUP)) {
            if (entity != null && entity.getDestroy()) {//群阅后即焚
                binding.mes.setText(chatMessage.getContentDescr());
            } else { //没有 开启阅后即焚
                binding.mes.setText(chatMessage.getNickname() + ":" + chatMessage.getContentDescr());
            }
        }else {
            binding.mes.setText(chatMessage.getContentDescr());
        }
        if (chatMessage.getContentDescr().isEmpty()) {
            binding.mes.setText("");
        }
        if (chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_NOTICE) ||chatMessage.getContentType().equals(ChatMessage.CHAT_CONTENT_TYPE_RECALLMSG) ){
            String s;
            if (chatMessage.getFromuser().equals(MainApplication.getInstance().getUserInfo().getId())){
                s = "你"+ chatMessage.getContent();
            }else {
                s = chatMessage.getNickname() + chatMessage.getContent();
            }
            if (!chatMessage.getExtra().isEmpty()){
                try {
                    JSONObject jsonObject = new JSONObject(chatMessage.getExtra());
                    String type = jsonObject.optString("type");
                    if (type.equals("8") || type.equals("16") || type.equals("9")){
                        s = chatMessage.getContent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            binding.mes.setText(s);
        }

        if (chatMessage.getSendMsgState() != null && chatMessage.getSendMsgState().equals(ChatMessage.SEND_FAIL)) { //发送失败
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.progress.setVisibility(View.GONE);
        }
        if (chatMessage.getSendMsgState() != null && chatMessage.getSendMsgState().equals(Constants.SEND_CHATMESSAGE_FAIL)) {//发送失败
            binding.progress.setVisibility(View.VISIBLE);
        } else {
            binding.progress.setVisibility(View.GONE);
        }
        DateFormat oldDay = new SimpleDateFormat("dd");
        DateFormat newDay = new SimpleDateFormat("dd");
        DateFormat dfHour = new SimpleDateFormat("a HH:mm");
        DateFormat dfDay = new SimpleDateFormat("MM月dd日");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date cur = new Date();
            Date chatTime = df.parse(chatMessage.getTime());
            long diff = cur.getTime() - chatTime.getTime();
            if (diff / (1000 * 60 * 60 * 24) >= 1) {
                binding.sendTimeView.setText(dfDay.format(chatTime));
            } else {
                if (Integer.parseInt(oldDay.format(chatTime)) < Integer.parseInt(newDay.format(new Date()))) {
                    binding.sendTimeView.setText(dfDay.format(chatTime));
                } else {
                    binding.sendTimeView.setText(dfHour.format(chatTime));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.sendTimeView.setText(dfHour.format(new Date()));
        }

    }
}
