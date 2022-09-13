package com.base.view;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.widget.TextView;

import com.base.R;
import com.base.manager.LoadingManager;

public class ProgressView extends Dialog {

    private LoadingManager.Listener mListener;

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public ProgressView(Context context, int theme,LoadingManager.Listener listener) {
        super(context, theme);
        this.mListener = listener;
        init(context);
    }

    TextView title;

    private void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.layout_progress);
        title = findViewById(R.id.tv_progress_dialog);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

    }

    public void updateProgress(final String progress) {
        title.post(new Runnable() {
            @Override
            public void run() {
                title.setText(progress);
            }
        });
    }

    @Override
    public void show() {//开启
        super.show();
    }

    @Override
    public void dismiss() {//关闭
        super.dismiss();
    }

}
