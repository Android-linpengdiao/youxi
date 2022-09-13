package com.okhttp;

import java.io.Serializable;

/**统一响应数据*/
public class ResultClient<T> implements Serializable {

    /**响应状态*/
    private boolean success = true;

    /**返回数据*/
    private T data;

    /**响应描述*/
    private  String msg;

    /**响应代码*/
    private int code;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
