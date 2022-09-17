package com.quakoo.im.manager;

import android.content.Context;

import com.base.BaseApplication;
import com.base.Constants;
import com.base.UserInfo;
import com.base.utils.CommonUtil;
import com.base.utils.MsgCache;
import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.screen.BlacklistDaoImpl;
import com.quakoo.im.screen.ChatBgDaoImpl;
import com.quakoo.im.screen.DestoryDaoImpl;
import com.quakoo.im.screen.ScreenDaoImpl;
import com.quakoo.im.screen.ShieldDaoImpl;
import com.quakoo.im.screen.User;

import java.util.List;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class DBManager {

    private static DBManager mInstance;

    private Context context;

    private ScreenDaoImpl screen;
    private DestoryDaoImpl destory;
    private ShieldDaoImpl shield;
    private BlacklistDaoImpl blacklist;
    private ChatBgDaoImpl chatBg;

    public static synchronized DBManager getInstance() {
        if (null == mInstance) {
            mInstance = new DBManager();
        }
        return mInstance;
    }

    private DBManager() {
        context = BaseApplication.getInstance();
        screen = new ScreenDaoImpl(context);
        destory = new DestoryDaoImpl(context);
        shield = new ShieldDaoImpl(context);
        blacklist = new BlacklistDaoImpl(context);
        chatBg = new ChatBgDaoImpl(context);
    }

    public ScreenDaoImpl getScreen() {
        if (null == screen) {
            screen = new ScreenDaoImpl(context);
        }
        return screen;
    }

    public DestoryDaoImpl getDestory() {
        if (null == destory) {
            destory = new DestoryDaoImpl(context);
        }
        return destory;
    }

    public ShieldDaoImpl getShield() {
        if (null == shield) {
            shield = new ShieldDaoImpl(context);
        }
        return shield;
    }

    public BlacklistDaoImpl getBlacklist() {
        if (null == blacklist) {
            blacklist = new BlacklistDaoImpl(context);
        }
        return blacklist;
    }

    public ChatBgDaoImpl getChatBg() {
        if (null == chatBg) {
            chatBg = new ChatBgDaoImpl(context);
        }
        return chatBg;
    }

    public synchronized boolean shielQueryThreadAll(ChatMessage cm) {
        List<User> users = shield.queryThreadAll(getUserInfo().id + "");
        boolean shield = false;
        if (users.size() > 0) {
            for (User info : users) {
                if (cm.getFromuser().equals(info.userId)) {
                    shield = true;
                    break;
                } else {
                    shield = false;
                }
            }
        }
        return shield;
    }

    public synchronized boolean blackQueryThreadAll(ChatMessage cm) {
        List<User> users = blacklist.queryThreadAll(getUserInfo().id + "");
        boolean shield = false;
        if (users.size() > 0) {
            for (User info : users) {
                if (cm.getFromuser().equals(info.userId)) {
                    shield = true;
                    break;
                } else {
                    shield = false;
                }
            }
        }
        return shield;
    }

    public static UserInfo getUserInfo() {
        UserInfo userinfo = (UserInfo) MsgCache.get(BaseApplication.getInstance()).getAsObject(Constants.USER_INFO);
        if (!CommonUtil.isBlank(userinfo)) {
            return userinfo;
        }
        return new UserInfo();
    }


}
