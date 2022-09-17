package com.quakoo.im.model;

import java.io.Serializable;

public class ChatSettingEntity implements Serializable {
    private String master = ""; //所属用户
    private String friend = ""; //好友ID
    private boolean screenshot = false;//截屏通知
    private boolean destroy = false;//阅后即焚
    private boolean shield = false;//消息免打扰
    private String chattheme = "";//聊天背景
    private boolean chatTop = false;//消息置顶

    public boolean isChatTop() {
        return chatTop;
    }

    public void setChatTop(boolean chatTop) {
        this.chatTop = chatTop;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public boolean getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(boolean screenshot) {
        this.screenshot = screenshot;
    }

    public boolean getDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean getShield() {
        return shield;
    }

    public void setShield(boolean shield) {
        this.shield = shield;
    }

    public String getChattheme() {
        return chattheme;
    }

    public void setChattheme(String chattheme) {
        this.chattheme = chattheme;
    }

    /**
     * 判断 两个ChatSettingEntity是否相同
     * @param entity
     * @return true 相同,false不同
     */
    public boolean IsSame(ChatSettingEntity entity){
        if (this.master.equals(entity.master) && this.getFriend().equals(entity.getFriend()) && this.getChattheme().equals(entity.getChattheme())&&
                this.getShield()==entity.getShield() && this.getDestroy() == entity.getDestroy()&& this.getShield() == entity.getShield() ){
            return true;
        }
        return false;
    }


}
