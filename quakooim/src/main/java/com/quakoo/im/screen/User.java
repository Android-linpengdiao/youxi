package com.quakoo.im.screen;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/21.
 */

public class User implements Serializable {

    public String master = "";
    public String userId = "";
    public String chatBg = "";

    public User() {
    }

    public User(String master, String userId) {
        this.master = master;
        this.userId = userId;
    }

    public User(String master, String userId, String chatBg) {
        this.master = master;
        this.userId = userId;
        this.chatBg = chatBg;
    }
}
