package com.quakoo.im.view;

import android.media.MediaPlayer;

public interface CompletionListener extends MediaPlayer.OnCompletionListener {

    void playInterrupt(); //播放中断

    void playPause();//播放暂停

    void playStart();//播放开始
    //结束 使用MediaPlayer.OnCompletionListener.onCompletion(MediaPlayer mp);
}
