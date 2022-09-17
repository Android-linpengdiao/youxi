package com.quakoo.im.screen;


import java.util.List;

/**
 * Created by Administrator on 2017/11/21.
 */

public interface UserDao {

    void insertThread(User screenInfo);

    void deleteThread(String master, String user_id);

    void updateThread(String master, String user_id);

    void updateThread(String master, String user_id, String chat_bg);

    List<User> queryThread(String master, String user_id);

    List<User> queryThreadAll(String master);

    boolean isExists(String master, String user_id);

}
