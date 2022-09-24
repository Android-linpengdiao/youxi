package com.quakoo.im.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.utils.CommonUtil;
import com.quakoo.im.R;


/**
 * Created by Administrator on 2017/9/22.
 */

public class AudioRecorderView extends RelativeLayout {


    private ImageView confirmImageView;
    private TextView confirmHintTextView;
    private ImageView cancelImageView;
    private TextView cancelHintTextView;
    private AudioRecorderButton audioRecorderButton;

    private LinearLayout recorderTimeContainer;
    private TextView recorderHintTextView;
//    private AudioRecorderDialogViewBinding binding;


    public AudioRecorderView(Context context) {
        super(context);
        initView(context);
    }

    public AudioRecorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AudioRecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        int LayoutId = R.layout.audio_recorder_dialog_view;
        View view = LayoutInflater.from(context).inflate(LayoutId, this);
//        binding = DataBindingUtil.bind(view);//绑定布局
        cancelImageView = view.findViewById(R.id.cancelImageView);
        cancelHintTextView = view.findViewById(R.id.cancelHintTextView);
        confirmImageView = view.findViewById(R.id.confirmImageView);
        confirmHintTextView = view.findViewById(R.id.confirmHintTextView);
        audioRecorderButton = view.findViewById(R.id.audioRecorderButton);

        recorderTimeContainer = view.findViewById(R.id.recorderTimeContainer);
        recorderHintTextView = view.findViewById(R.id.recorderHintTextView);
        audioRecorderButton.setAudioRecorderView(this);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void init() {
        recorderTimeContainer.setVisibility(INVISIBLE);
        cancelImageView.setVisibility(INVISIBLE);
        confirmImageView.setVisibility(INVISIBLE);
        recorderHintTextView.setVisibility(VISIBLE);
        setVisibility(View.VISIBLE);

        confirmHintTextView.setText("00:00");
        confirmHintTextView.setTextColor(Color.parseColor("#ffffff"));
        audioRecorderButton.setBackgroundResource(R.drawable.ic_im_audio_record_n);
    }

    public void recording() {

        recorderTimeContainer.setVisibility(VISIBLE);
        cancelImageView.setVisibility(VISIBLE);
        confirmImageView.setVisibility(VISIBLE);
        recorderHintTextView.setVisibility(GONE);

        audioRecorderButton.setBackgroundResource(R.drawable.ic_im_audio_record_s);
        cancelImageView.setBackgroundResource(R.drawable.bg_transparent);
        confirmImageView.setBackgroundResource(R.drawable.bg_transparent);
        confirmHintTextView.setText("松手发送");
        confirmHintTextView.setTextColor(Color.parseColor("#ffffff"));

    }

    public void wantToConfirm() {
        cancelImageView.setBackgroundResource(R.drawable.bg_transparent);
        confirmImageView.setBackgroundResource(R.drawable.button_confirm);
        confirmHintTextView.setText("松手发送");
        confirmHintTextView.setTextColor(Color.parseColor("#51C2FF"));
    }

    public void wantToCancel() {
        cancelImageView.setBackgroundResource(R.drawable.button_cancel);
        confirmImageView.setBackgroundResource(R.drawable.bg_transparent);
        confirmHintTextView.setText("松手取消发送");
        confirmHintTextView.setTextColor(Color.parseColor("#F16572"));
    }

    public void updateTime(int time) {
        if (audioRecorderButton.mCurrentState == AudioRecorderButton.STATE_RECORDING) {
            confirmHintTextView.setText(CommonUtil.stringForTime(time));
        }
//        if ((30 - time) <= 10) {
//            confirmHintTextView.setText("你还可以录" + (30 - time) + "秒");
//        } else {
//            confirmHintTextView.setText(context.getString(R.string.ReleaseToCancel));
//        }
    }


}
