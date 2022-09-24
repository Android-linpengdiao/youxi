package com.quakoo.im.CustomEvents;

public interface VideoEvents {
    void Succeed(String data); //成功
    void Progress(long uploadedSize, long totalSize);//进度
    void Failed(String failed); //失败
}
