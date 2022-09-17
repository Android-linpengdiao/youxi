package com.base;

public class Constants {

    public static final String USER_INFO = "user_info";
    public static final String Columns = "Columns";
    public static final String MediaAsset = "MediaAsset";
    public static final String FRIENDS = "friends";
    public static final String GROUPS = "groups";
    public static final String ListByType = "ListByType";
    public static final String str_update_wait = "正在上传 %s";
    public static final String str_download_wait = "正在下载 %s";
    public static final String BuglyAppID = "0219c90857";

    public final static String Xiaomi = "Xiaomi";
    public final static String huawei = "HUAWEI";
    public final static String Meizu = "Meizu";

    public static final int perPage = 100;


//    public static final String USER_INFO = "user_info";
    public static final String CURRENT_INFO = "current_info";
    public static final String ACCOUNT_INFO = "account_info";
    public static final String PLUGINS_INFO = "plugins_info";
    public static final String EMOTIN_LIST = "emotion_list";
    public static final String TOPCHAT_LIST = "topchat_list";

    public final static String FRAGMENT_FRIEND = "friend";//单聊
    public final static String FRAGMENT_GROUP = "group";//群聊
    public final static String FRAGMENT_NOTICE = "notice";
    public final static String FRAGMENT_NAMECARD="namecard";
    public final static String FRAGMENT_RED="red";

    public final static int RESULT_CHAT_GROUP = 100;

    //好友列表
    public static final String TYPE_GROUP = "type_group";
    public static final String TYPE_JOIN = "type_join";
    public static final String TYPE_EXIT = "type_exit";
    public static final String TYPE_DELETE = "type_delete";
    public static final String TYPE_NAME = "type_name";
    public static final String TYPE_ZHUANFA = "type_zhuanfa";
    public static final String TYPE_HINT = "type_hint";
    public static final String TYPE_MY_USER = "type_my_user";
    public static final String TYPE_GROUP_USER = "type_group_user";
    public static final String TYPE_LOGIN = "type_login";
    public static final String TYPE_SHARE = "type_share";
    public static final String TYPE_SHARE_TXT = "type_share_txt";
    public static final String TYPE_SHARE_IMAGE = "type_share_image";
    public static final String TYPE_CHAT_GROUP = "type_chat_group";
    public static final String TYPE_FRIND_REQUEST="friend_request";//轮询,好友请求key
    public static final String TYPE_FRIND_REQUEST_READ="friend_request_read";
    public static final String TYPE_GROUP_CREATER = "type_group_creater";
    public static final String TYPE_GROUP_ADMIN = "type_group_admin";
    public static final String TYPE_FRIND_MSG_NUMBER="friend_msg_number";
    //
    public static final String AITE_FRIEND ="@friend";

    public final static String CHAT_NEW_MESSAGE = "CHAT_NEW_MESSAGE";
    public final static String CHAT_RECENT_MESSAGE = "CHAT_RECENT_MESSAGE";
    public final static String CHAT_DELETE_MESSAGE = "CHAT_DELETE_MESSAGE";

    public final static String CHAT_USER_LOGOUT = "CHAT_USER_LOGOUT";
    public final static String SOCKET_USER_LOGOUT = "SOCKET_USER_LOGOUT";

    //测试
    public final static String test_action = "test_action";

    public final static String SET_MSG_INDEX = "setIndex";
    public final static String GET_MSG_INDEX = "getIndex";

    //聊天
    public final static String SEND_CHATMESSAGE = "send_chatmessage"; //发送/发送中
    public final static String SEND_CHATMESSAGE_SUCCESS = "send_chatmessage_success"; //发送成功
    public final static String SEND_CHATMESSAGE_FAIL = "send_chatmessage_fail"; //发送失败
    public final static String SEND_DELETEMSG="send_deletemsg";
    public final static String SEND_DELETEMSG_SUCCESS = "send_deletemsg_success";
    public final static String REACLL_CHATMESSAGE_SUCCESS = "recall_chatmessage_success";
    public final static String SEND_RECALLMSG="send_recallmsg";
    public final static String CHAT_GROUP_ANNOUN = "chat_group_announ";
    public final static String CHAT_UPDATE_MESSAGE = "chat_update_message";

    public final static String FRIENDS_NOTICE = "friends_notice";
    public final static String APPLY_FRIENDS = "apply_friends";

    public final static String CHAT_EXTRA_TYPE_SCREEN = "chat_extra_screen";
    public final static String CHAT_EXTRA_TYPE_DESTROY = "chat_extra_destroy";
    public final static String SEND_RED_HONGBAO="send_red_hongbao"; //发送红包
    public final static String SEND_NOTICE="send_notice"; //通知消息直接发往服务器不存本地
    public final static String UPDATE_EMOTION="update_emotion"; //通知消息直接发往服务器不存本地

    public final static String UPDATA_GROUPINFO="update_groupinfo"; //更新群名称

    //语音聊天

    public final static String ACTION_CLOSE = "action_close";
    public final static String ACTION_MEDIA = "action_media_compression";

    public final static String AVCHAT_OPEN = "open";
    public final static String AVCHAT_CLOSE = "close";
    public final static String AVCHAT_TYPE_AUDIO = "audio";
    public final static String AVCHAT_TYPE_VIDEO = "video";
    public final static String VOICE_TYPE_SENG = "voice_send"; //语音发起
    public final static String VOICE_TYPE_RECEIVE = "voice_receive"; //语音被邀请
    public final static String VIDEO_TYPE_SENG = "video_send";
    public final static String VIDEO_TYPE_RECEIVE = "video_receive";
    public final static String AVCHAT_TYPE_GROUP_AUDIO = "group_audio"; //群语音
    public final static String AVCHAT_TYPE_GROUP_VIDEO = "group_video";
    public final static String AVCHAT_TYPE_RECEIVE = "group_voice_receive"; //群语音被邀请
    public final static String AVCHAT_TYPE_SENG = "group_voice_send"; //群聊视频发起者

    //表情包
    public final static String EMOTION = "emotion";
    public final static String EMOTION_TU = "Emotion_tu";
    public final static String EMOTION_NEWTU = "Emotion_newtu";
    public final static String EMOTION_UP = "Emotion_UP";
    public final static String EMOTION_CUSTOM = "Emotion_CUSTOM";

//    public final static String Xiaomi = "Xiaomi";
//    public final static String huawei = "HUAWEI";
//    public final static String Meizu = "Meizu";

    public final static  String Register="注册";
    public final static  String ResetPassword="重置密码";
    public final static  String UpdatePassword="忘记密码密码";

    public final static int IM_SERVICE_ID = 100;
    public final static int AV_SERVICE_ID = 200;
    public final static int PUSH_SERVICE_ID = 300;

    //用于文件上传
    public final static String SEND_PROCEED = "proceed"; //上传
    public final static String SEND_PROCEED_IN = "proceed_In"; //上传中
    public final static String SEND_PROCEED_SUCCEED = "proceedSucceed"; //上传完成
    public final static String EXITLOGIN="ExitLogin"; //退出登录
    public final static String NETWORK_NONE="NETWORK_NONE"; //网络变化 无网络
    public final static String NETWORK_MOBILE="NETWORK_MOBILE"; //网络变化 移动网络
    public final static String NETWORK_WIFI="NETWORK_WIFI"; //网络变化 wifi
    public final static String CHANGE_ACCOUNT="CHANGE_ACCOUNT"; //网络变化 切换帐号


    public final static String CHATSETTING="ChatSetting"; //好友设置
    public final static String IMSOCKETSERVICE_SENG="ImSocketService_Seng"; //用于给ImSocketService 发送消息.

}
