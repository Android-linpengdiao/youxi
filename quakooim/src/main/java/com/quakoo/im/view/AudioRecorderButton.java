package com.quakoo.im.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.base.BaseApplication;
import com.base.utils.FileUtils;
import com.quakoo.im.R;
import com.quakoo.im.manager.AudioManager;
import com.quakoo.im.manager.MediaManager;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;

public class AudioRecorderButton extends AppCompatButton {
    private String TAG = "AudioRecorderButton";
    public static final int STATE_NORMAL = 1;// 默认的状态
    public static final int STATE_RECORDING = 2;// 正在录音
    public static final int STATE_WANT_TO_CONFIRM = 3;// 希望确定
    public static final int STATE_WANT_TO_CANCEL = 4;// 希望取消

    public static int mCurrentState = STATE_NORMAL; // 当前的状态
    public static boolean isRecording = false;// 已经开始录音

    private AudioManager mAudioManager;

    // 是否触发longClick
    private boolean mReady;
    private android.media.AudioManager audioManager;
    private Context context;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DIMISS = 0x112;
    private static final int MSG_TIME_OUT = 0x113;
    private static final int UPDATE_TIME = 0x114;

    private boolean mThreadFlag = false;
    private int time = 0;
    private float mTime;
    private boolean isOverDue = false;

    private float maxTime = 30.0f;
    /*
     * 获取音量大小的线程
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    time++;
                    if (time % 10 == 0) {
                        mHandler.sendEmptyMessage(UPDATE_TIME);
                    }
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                    if (mTime >= maxTime) {//如果时间超过maxTime秒，自动结束录音
                        while (!mThreadFlag) {//记录已经结束了录音，不需要再次结束，以免出现问题
                            audioRecorderView.post(new Runnable() {
                                @Override
                                public void run() {
                                    audioRecorderView.setVisibility(GONE);
                                }
                            });
                            mAudioManager.release();
                            if (audioFinishRecorderListener != null) {
                                //发消息给主线程，告诉他reset（）;
                                mHandler.sendEmptyMessage(MSG_TIME_OUT);
                                audioFinishRecorderListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                            }
                            mThreadFlag = !mThreadFlag;
                        }
                        isRecording = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    // 显示對話框在开始录音以后
                    audioRecorderView.setVisibility(VISIBLE);
                    isRecording = true;
                    // 开启一个线程
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
//                    audioRecorderView.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    audioRecorderView.setVisibility(GONE);
//                    DialogManager.showAudioDialog((Activity) context);
                    break;
                case MSG_TIME_OUT://录音超时
                    reset();
                    break;
                case UPDATE_TIME://更新时间
                    if (time % 10 == 0) {
                        audioRecorderView.updateTime(time / 10);
                    }
                    break;
            }
        }
    };

    public AudioRecorderView audioRecorderView;

    public void setAudioRecorderView(AudioRecorderView audioRecorderView) {
        this.audioRecorderView = audioRecorderView;
    }

    /**
     * 以下2个方法是构造方法
     */
    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        audioManager = (android.media.AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//        String dir = Environment.getExternalStorageDirectory().getPath() + "/" + context.getPackageName() + "/audio";
        mAudioManager = AudioManager.getInstance(FileUtils.getChatPath());
        mAudioManager.setOnAudioStateListener(new AudioManager.AudioStateListener() {
            public void wellPrepared() {
                if (!isOverDue) {
                    mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);//开启线程
                }
            }
        });
        // 由于这个类是button所以在构造方法中添加监听事件
        setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                if (!isOverDue) {
                    mReady = true;
                    mAudioManager.prepareAudio();
                }
                return true;
            }
        });
    }

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener {
        void onFinish(float seconds, String filePath);

        void onCancel();
    }

    private AudioFinishRecorderListener audioFinishRecorderListener;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
        audioFinishRecorderListener = listener;
    }

    android.media.AudioManager.OnAudioFocusChangeListener afChangeListener = new android.media.AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == android.media.AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
            } else if (focusChange == android.media.AudioManager.AUDIOFOCUS_LOSS) {
                audioManager.abandonAudioFocus(afChangeListener);
                // Stop playback
            }
        }
    };

    public void myRequestAudioFocus() {
        audioManager.requestAudioFocus(afChangeListener, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    /**
     * 屏幕的触摸事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float last = 0;// 获得y轴坐标
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mThreadFlag = false;
                changeState(STATE_RECORDING);
                myRequestAudioFocus();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    Log.i(TAG, "onTouchEvent: getX" + event.getX());
                    Log.i(TAG, "onTouchEvent: getY " + event.getY());
                    // 如果想要取消，根据x,y的坐标看是否需要确定、取消
                    int move = BaseApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.dp_110);
                    //录制按钮的半径
                    int margin = BaseApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.dp_46);
                    if (event.getY() < 0 && event.getX() > move - margin) {
                        changeState(STATE_WANT_TO_CONFIRM);
                    } else if (event.getY() < 0 && event.getX() < -move + margin) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime <= 1.0f) {//小于1秒
//                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessage(MSG_DIALOG_DIMISS);//显示对话框
                    if (audioFinishRecorderListener != null) {
                        audioFinishRecorderListener.onCancel();
                    }
                } else if (mCurrentState == STATE_RECORDING
                        || mCurrentState == STATE_WANT_TO_CONFIRM) { // 正在录音的时候，结束；希望确定
                    audioRecorderView.setVisibility(GONE);
                    mAudioManager.release();
                    if (audioFinishRecorderListener != null) {
                        audioFinishRecorderListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                    Log.d(TAG, "onTouchEvent: 录制时长:" + mTime);
                    Log.d(TAG, "onTouchEvent: 文件路径:" + mAudioManager.getCurrentFilePath());
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // 想要取消
                    audioRecorderView.setVisibility(GONE);
                    mAudioManager.cancel();
                }
                reset();
                audioManager.abandonAudioFocus(afChangeListener);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isRecording = false;
        mTime = 0;
        time = 1;
        mReady = false;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancle(int x, int y) {
        if (y < 0) {
            return true;
        } else {
            return false;
        }
    }

    boolean isWantToCancel = false;

    /**
     * 改变
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
//                    setText(context.getString(R.string.HoldToTalk));
                    break;
                case STATE_RECORDING:
//                    if (isRecording) {
                    audioRecorderView.recording();
//                    }
//                    setText(context.getString(R.string.HoldToTalk));
//                    MediaManager.pause();
                    isWantToCancel = false;
                    break;
                case STATE_WANT_TO_CONFIRM:
//                    setText(context.getString(R.string.ReleaseToCancel));
                    audioRecorderView.wantToConfirm();
                    isWantToCancel = false;
                    break;
                case STATE_WANT_TO_CANCEL:
//                    setText(context.getString(R.string.ReleaseToCancel));
                    audioRecorderView.wantToCancel();
                    isWantToCancel = true;
                    break;
            }
        }
    }
}