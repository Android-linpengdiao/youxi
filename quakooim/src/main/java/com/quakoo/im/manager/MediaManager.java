package com.quakoo.im.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

import com.quakoo.im.model.ChatMessage;
import com.quakoo.im.view.CompletionListener;

public class MediaManager {
    private static String TAG="MediaManager";
    private static volatile MediaPlayer mMediaPlayer;
    private static boolean isPause;
    private static String currentFilePath;
    private AudioManager.AudioStateListener onAudioStateListener;
    static Context context;
    private static volatile String currentid;
    private static MediaPlayer.OnCompletionListener mOnCompletionListener;
    private static CompletionListener mCompletionListener;

    public MediaManager(Context context) {
        this.context = context;
    }

    /**
     * 播放音乐
     *
     * @param message
     * @param onCompletionListener
     */
    public static void playSound(ChatMessage message, MediaPlayer.OnCompletionListener onCompletionListener) {
        String filePath =message.getUrl();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
//            mMediaPlayer = MediaPlayer.create(context, Uri.fromFile(new File(filePath)));
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {

            //判断是否是在播放,并且 将要播放的文件路径与原来(记录)的路劲不是同一个,表示需要切换播放,暂停原来播放的文件,并且主动调用播放完成方法
            if (isPlaying() && !filePath.equals(currentFilePath)){
                mMediaPlayer.stop();
                if (mOnCompletionListener!=null) {
                    mOnCompletionListener.onCompletion(mMediaPlayer);
                }
                mMediaPlayer.reset();
            }
            currentFilePath = filePath;
            mMediaPlayer.reset();
        }
        try {
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mOnCompletionListener = onCompletionListener;
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {

        }
    }

    /**
     * 播放音乐
     *
     * @param message
     * @param onCompletionListener
     */
    public static void playSound(ChatMessage message, CompletionListener onCompletionListener) {
        String filePath =message.getUrl();
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
//            mMediaPlayer = MediaPlayer.create(context, Uri.fromFile(new File(filePath)));
            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
    if (!message.getAudioState().equals(ChatMessage.AUDIO_PLAY_STATE_START)) {
        //第一次播放 再次点击原来的 暂停
        if (isPlaying() && filePath.equals(currentFilePath)) {
            mMediaPlayer.stop();
            if (mCompletionListener != null) {
                mCompletionListener.playInterrupt();
            }
            mMediaPlayer.reset();
            return;
        } else if (isPlaying() && !filePath.equals(currentFilePath)) { //判断是否是在播放,并且 将要播放的文件路径与原来(记录)的路劲不是同一个,表示需要切换播放,暂停原来播放的文件,并且主动调用播放完成方法
            mMediaPlayer.stop();
            if (mCompletionListener != null) {
                mCompletionListener.playInterrupt();
            }
            mMediaPlayer.reset();
        }
    }
        }
        try {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            currentFilePath = filePath;
            mCompletionListener = onCompletionListener;
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mCompletionListener.playStart();
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {

        }
    }





    /**
     * 暂停播放
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            if (mOnCompletionListener!=null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
            if (mCompletionListener!=null){
                mCompletionListener.playInterrupt();
            }
        //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 是否在播放
     * @return
     */
    public static boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {

            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mHandler.postDelayed(this, 1000);
                int currentTime = Math
                        .round(mMediaPlayer.getCurrentPosition() / 1000);
//                String currentStr = String.format("%s%02d:%02d", "当前时间 ",
//                        currentTime / 60, currentTime % 60);

            }
        }
    };
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0x01:

                    break;

                default:
                    break;
            }
        };
    };

}
