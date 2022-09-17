package com.quakoo.im.model;

import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/28.
 */
public class ChatMessage implements Serializable, Cloneable {
    static final long serialVersionUID = 42L;
    public String master = "";//消息拥有者
    public String fromuser = ""; //消息发送方
    public String fromusernick = "";//消息发送方昵称
    public String fromuserremark = ""; //消息发送方备注
    public String touser = ""; //消息接收方
    public String tousernick = ""; //消息接收方昵称
    public String touserremark = ""; //消息接收方备注
    public String content = ""; //消息内容
    public String type = ""; //消息类型?
    public String time = "";//消息时间戳
    public String serveId = ""; //服务器id?
    public String clientId = "";//客户端id
    public String avatar = "";//发送消息方头像
    public String username = ""; //
    public String nickname = "";
    public String userremark = ""; //群中发送消息的用户标识
    public String userIcon = ""; //接收方头像
    public String url = "";
    public String contentType = "";
    public int unreadCount = 0;
    public String extra = "";
    public String thumbnailUrl = "";
    public String audioState = "unread";
    public String prompt = "false";
    public String resend = "false";
    public String progress = "false";
    public String state = "";
    public String sendMsgState = Constants.SEND_CHATMESSAGE;// 1、压缩中 2、发送中 3、发送成功 4、发送失败 (默认发送)
    public String hintState = "read"; //unread @ read 不@
    public String destroy = "false";
    public String label = "";
    public String soundAsr = "";
    public String soundAsrState = "0";// 0、默认状态 ；1、转文字中 ；2、转文字成功 ；3、转文字失败
    public String redOpenState = "false"; //红包状态 是/否  已打开  false 没打开
    public String msgFrom = "1";//1、发送 ；2、接收
    public boolean topChat = false;
    public float upprogress = 0f; //上传进度
    public String redReceiveState = "";//红包领取情况 //已领取 //已被领完


    public ChatSettingEntity settingEntity; //聊天窗口设置 //消息免打扰,阅后.....等设置

    public final static String CHAT_CONTENT_TYPE_TXT = "txt";//文本
    public final static String CHAT_CONTENT_TYPE_AUDIO = "audio";//音频
    public final static String CHAT_CONTENT_TYPE_PIC = "pic"; //图片
    public final static String CHAT_CONTENT_TYPE_VIDEO = "video"; //视频
    public final static String CHAT_CONTENT_TYPE_RED = "red"; //红包
    public final static String CHAT_CONTENT_TYPE_SCREEN = "screen";//开启截屏通知
    public final static String CHAT_CONTENT_TYPE_USER_SCREEN = "user_screen";//用户截屏
    public final static String CHAT_CONTENT_TYPE_DESTROY = "destroy"; //阅读及焚
    public final static String CHAT_CONTENT_TYPE_CALL_AUDIO = "call_audio";
    public final static String CHAT_CONTENT_TYPE_CALL_VIDEO = "call_video";
    public final static String CHAT_CONTENT_TYPE_LOCATION = "location";//定位
    public final static String CHAT_CONTENT_TYPE_NAMECARD = "nameCard";//名片
    public final static String CHAT_CONTENT_TYPE_DELETEMSG = "delete_msg";//删除消息
    public final static String CHAT_CONTENT_TYPE_RECALLMSG = "recall_msg";//回撤消息
    public final static String CHAT_CONTENT_TYPE_DELETE = "delete";//删除
    public final static String CHAT_CONTENT_TYPE_SHARE = "share";
    public final static String CHAT_CONTENT_TYPE_POINT = "point";
    public final static String CHAT_CONTENT_TYPE_OPEN_RED = "open_red";//领取红包
    public final static String CHAT_CONTENT_TYPE_NOTICE = "notice";//通知
    public final static String CHAT_CONTENT_TYPE_CHECKJOIN = "check_join";//邀请审核
    public final static String CHAT_CONTENT_TYPE_SHARE_PLUGIND = "plugins";
    public final static String CHAT_CONTENT_TYPE_HIDE = "hide";
    public final static String CHAT_CONTENT_TYPE_EMOTION = "emotion";
    public final static String CHAT_CONTENT_TYPE_PRODUCT = "product";

    public final static String SEND_COMPRESS = "compress"; //压缩
    public final static String SEND_PROCEED = "proceed"; //上传
    public final static String SEND_PROCEED_IN = "proceed_In"; //上传中
    public final static String SEND_PROCEED_SUCCEED = "proceedSucceed"; //上传完成
    public final static String SEND_FAIL = "fail"; //失败
    public final static String AUDIO_PLAY_STATE_COMPLETION = "audio_play_state_completion"; //播放完成
    public final static String AUDIO_PLAY_STATE_START = "audio_play_state_start"; //播放开始
    public final static String UPDATA_LOCAL_DATA = "updata_Local_data";//更新本地数据,不需要过,网络


    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getFromuser() {
        return fromuser;
    }

    public void setFromuser(String fromuser) {
        this.fromuser = fromuser;
    }

    public String getFromusernick() {
        return fromusernick;
    }

    public void setFromusernick(String fromusernick) {
        this.fromusernick = fromusernick;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTousernick() {
        return tousernick;
    }

    public void setTousernick(String tousernick) {
        this.tousernick = tousernick;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContentDescr() {
        if (contentType.equals(CHAT_CONTENT_TYPE_TXT)) {
            return content;
        } else if (contentType.equals(CHAT_CONTENT_TYPE_HIDE)) {
            return "";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_AUDIO)) {
            return "[语音]";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_PIC)) {
            if (CommonUtil.isPicture(url)) {
                return "[图片]";
            } else {
                return "[动画表情]";
            }
        } else if (contentType.equals(CHAT_CONTENT_TYPE_VIDEO)) {
            return "[视频]";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_RED)) {
            return "[红包]";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_LOCATION)) {
            return "[位置]";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_NAMECARD)) {
            return "[个人名片]";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_RECALLMSG)) {
            return "撤回了一条消息";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_SHARE)) {
            return "[链接]";
        } else if (contentType.equals(CHAT_CONTENT_TYPE_SHARE_PLUGIND)) {
            return "[小程序]" + content;
        } else if (contentType.equals(CHAT_CONTENT_TYPE_EMOTION)) {
            return "[动画表情]";
        }  else if (contentType.equals(CHAT_CONTENT_TYPE_PRODUCT)) {
            return "[商品链接]";
        } else {
            return content;
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getServeId() {
        return serveId;
    }

    public void setServeId(String serveId) {
        this.serveId = serveId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getUnreadCount() {
        return this.unreadCount;
    }

    public void setUnreadCount(int count) {
        this.unreadCount = count;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAudioState() {
        return audioState;
    }

    public void setAudioState(String audioState) {
        this.audioState = audioState;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getResend() {
        return resend;
    }

    public void setResend(String resend) {
        this.resend = resend;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getMsgState() {
        return state;
    }

    public void setMsgState(String state) {
        this.state = state;
    }

    public String getSendMsgState() {
        return sendMsgState;
    }

    public void setSendMsgState(String sendMsgState) {
        this.sendMsgState = sendMsgState;
    }

    public String getDestroy() {
        return destroy;
    }

    public void setDestroy(String destroy) {
        this.destroy = destroy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSoundAsr() {
        return soundAsr;
    }

    public void setSoundAsr(String soundAsr) {
        this.soundAsr = soundAsr;
    }

    public String getSoundAsrState() {
        return soundAsrState;
    }

    public void setSoundAsrState(String soundAsrState) {
        this.soundAsrState = soundAsrState;
    }

    public String getRedOpenState() {
        return redOpenState;
    }

    public void setRedOpenState(String redOpenState) {
        this.redOpenState = redOpenState;
    }

    public String getHintState() {
        return hintState;
    }

    public void setHintState(String hintState) {
        this.hintState = hintState;
    }

    public String getMsgFrom() {
        return msgFrom;
    }

    public void setMsgFrom(String msgFrom) {
        this.msgFrom = msgFrom;
    }

    public String getFromuserremark() {
        return fromuserremark;
    }

    public void setFromuserremark(String fromuserremark) {
        this.fromuserremark = fromuserremark;
    }

    public String getTouserremark() {
        return touserremark;
    }

    public void setTouserremark(String touserremark) {
        this.touserremark = touserremark;
    }

    public String getUserremark() {
        return userremark;
    }

    public void setUserremark(String userremark) {
        this.userremark = userremark;
    }

    public boolean isTopChat() {
        return topChat;
    }

    public void setTopChat(boolean topChat) {
        this.topChat = topChat;
    }

    //新增一个发送人员的方法 将UserInfo实体传入 为 消息体-发送者相关的属性赋值
    public void setSendMessagePersonnel(UserInfo userInfo) {
        setMaster(userInfo.getId()); //用户 ->id
        setFromuser(userInfo.getId()); //名字
        setFromusernick(userInfo.getId());
        setFromuserremark(userInfo.getRemark());//备注
        setNickname(userInfo.getName());
        setAvatar(userInfo.getIcon());
    }

    public void setReceiveMessagePersonnel(UserInfo userInfo) {
        setTouser(userInfo.getId());
        setTousernick(userInfo.getName());
        setTouserremark(userInfo.getRemark());
        setUserIcon(userInfo.getIcon());
        //判断接受者是单聊还是群聊
//        setType(Constants.FRAGMENT_FRIEND);
        if (userInfo.getType().equals(Constants.FRAGMENT_GROUP)) {
            setType(Constants.FRAGMENT_GROUP);
        } else {
            setType(Constants.FRAGMENT_FRIEND);
        }

    }

    @Override
    public ChatMessage clone() {
        ChatMessage chatMessage = null;
        try {
            chatMessage = (ChatMessage) super.clone();   //浅复制
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean getTopChat() {
        return this.topChat;
    }

    public float getUpprogress() {
        return upprogress;
    }

    public void setUpprogress(float upprogress) {
        this.upprogress = upprogress;
    }


    public ChatSettingEntity getSettingEntity() {
        return settingEntity;
    }

    public void setSettingEntity(ChatSettingEntity settingEntity) {
        this.settingEntity = settingEntity;
    }

    public String getRedReceiveState() {
        return redReceiveState;
    }

    public void setRedReceiveState(String redReceiveState) {
        this.redReceiveState = redReceiveState;
    }

    /**
     * 获取消息体里 聊天的好友信息
     * @return
     */
    public UserInfo getFriendUser(){
        UserInfo userInfo = new UserInfo();
        if (getFromuser().equals(getMaster())) { //消息发送方,等于消息拥有者,表示这条消息是拥有者发送
            userInfo.setId(getTouser()); //
            userInfo.setIcon(getUserIcon());
            if (getTouserremark() == null || getTouserremark().equals("")) {
                userInfo.setName(getTousernick());
            } else {
                userInfo.setName(getTouserremark());
            }
        } else { //别人
            userInfo.setId(getFromuser());
            if (getType().equals(Constants.FRAGMENT_FRIEND)) {
                userInfo.setIcon(getAvatar());
            }else if (getType().equals(Constants.FRAGMENT_GROUP)){
                userInfo.setIcon(getUserIcon());
            }
            if (getFromuserremark() == null || getFromuserremark().equals("")) {
                userInfo.setName(getFromusernick());
            } else {
                userInfo.setName(getFromuserremark());
            }
        }
        return userInfo;
    }

    public String getFriendID() { //
        ////getMsgFrom ()1、发送 ；2、接收
        String id = "";
        if (getMsgFrom().isEmpty()){//但标记接收/发送的字段为空时
            id = getTouser();
            if (getMaster().equals(getFromuser())){//消息拥有者 = 发送者 表示是自己发送的数据
                if (getType().equals(Constants.FRAGMENT_GROUP)) {
                    id = getUsername();
                }
            }
            return id;
        }
        if (getMsgFrom().equals("2")) {
            if (getType().equals(Constants.FRAGMENT_GROUP)) {
                id = getUsername()==null?"":getUsername();
            } else {
                id = getFromuser()==null?"":getFromuser();
            }
        } else if (getMsgFrom().equals("1")) {
            id = getTouser()==null?"":getTouser();
        }
        return id;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "master='" + master + '\'' +
                ", fromuser='" + fromuser + '\'' +
                ", fromusernick='" + fromusernick + '\'' +
                ", fromuserremark='" + fromuserremark + '\'' +
                ", touser='" + touser + '\'' +
                ", tousernick='" + tousernick + '\'' +
                ", touserremark='" + touserremark + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", serveId='" + serveId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", avatar='" + avatar + '\'' +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userremark='" + userremark + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", url='" + url + '\'' +
                ", contentType='" + contentType + '\'' +
                ", unreadCount=" + unreadCount +
                ", extra='" + extra + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", audioState='" + audioState + '\'' +
                ", prompt='" + prompt + '\'' +
                ", resend='" + resend + '\'' +
                ", progress='" + progress + '\'' +
                ", state='" + state + '\'' +
                ", sendMsgState='" + sendMsgState + '\'' +
                ", hintState='" + hintState + '\'' +
                ", destroy='" + destroy + '\'' +
                ", label='" + label + '\'' +
                ", soundAsr='" + soundAsr + '\'' +
                ", soundAsrState='" + soundAsrState + '\'' +
                ", redOpenState='" + redOpenState + '\'' +
                ", msgFrom='" + msgFrom + '\'' +
                ", topChat=" + topChat +
                ", upprogress=" + upprogress +
                ", redReceiveState='" + redReceiveState + '\'' +
                ", settingEntity=" + settingEntity +
                '}';
    }
}
