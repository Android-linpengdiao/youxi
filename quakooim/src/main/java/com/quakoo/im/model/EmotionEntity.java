package com.quakoo.im.model;

import java.io.Serializable;
import java.util.List;

public class EmotionEntity implements Serializable {


    private List<DataBean> emotionList;

    public List<DataBean> getEmotionList() {
        return emotionList;
    }

    public void setEmotionList(List<DataBean> emotionList) {
        this.emotionList = emotionList;
    }

    public static class DataBean implements Serializable {

        private String url = "";
        private String thumbnail = "";
        private int width = 0;
        private int height = 0;
        private boolean status = false;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }

}
