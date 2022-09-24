package com.quakoo.im.view;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.utils.StatusBarUtil;
import com.quakoo.im.R;

public class DialogManager {
    private AlertDialog.Builder builder;
    private ImageView mIcon;
    //    private ImageView mVoice;
    private TextView mLable;

    private Context context;

    private AlertDialog dialog;//用于取消AlertDialog.Builder
    private String TAG="DialogManager";

    /**
     * 构造方法 传入上下文
     */
    public DialogManager(Context context) {
        this.context = context;
    }

    // 显示录音的对话框
    public void showRecordingDialog() {
        builder = new AlertDialog.Builder(context, R.style.voice_chat_dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        mIcon = (ImageView) view.findViewById(R.id.id_recorder_dialog_icon);
        mLable = (TextView) view.findViewById(R.id.id_recorder_dialog_label);
        builder.setView(view);
        builder.create();
        dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);//设置点击外部不消失
        StatusBarUtil.setWindowStatusBarColor(dialog);
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//            GlideUtil.loadSquarePicture(R.drawable.gif_record, mIcon);
            mIcon.setImageResource(R.drawable.record_animate);
            mLable.setText("手指上滑，取消发送");
            mLable.setBackgroundResource(R.drawable.bg_transparent);
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.setImageResource(R.drawable.recording_cancel);
            mLable.setText("松开手指，取消发送");
            mLable.setBackgroundResource(R.drawable.bg_recording_hint);
        }
    }

    public void updateTime(int time) {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
//            mLable.setText(time + "s");
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mLable.setText("录音时间过短");
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        Log.d(TAG, "updateVoiceLevel: "+level);
        if (level>=7){
            level = 7;
        }else if (level<=0){
            level=1;
        }
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);
            mIcon.getDrawable().setLevel(level);
        }
    }
}