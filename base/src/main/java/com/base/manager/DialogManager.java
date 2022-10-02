package com.base.manager;

import android.app.Activity;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;

import android.content.DialogInterface;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.base.R;
import com.base.databinding.ViewAddPermissionDialogBinding;
import com.base.databinding.ViewConfirmDialogAlertBinding;
import com.base.databinding.ViewEditUserNameDialogBinding;
import com.base.databinding.ViewSendJubenDialogBinding;
import com.base.databinding.ViewSignInDialogBinding;
import com.base.utils.CommonUtil;
import com.base.utils.ToastUtils;

/**
 * Created by Administrator on 2017/9/15.
 */

public class DialogManager {

    private static final String TAG = "DialogManager";

    private static DialogManager mInstance;

    private DialogManager() {
    }

    public static DialogManager getInstance() {
        if (mInstance == null) {
            synchronized (DialogManager.class) {
                if (mInstance == null) {
                    mInstance = new DialogManager();
                }
            }
        }
        return mInstance;
    }

    public static AppCompatDialog createLoadingDialog(Activity act) {
        return createLoadingDialog(act, "");
    }

    public static AppCompatDialog createLoadingDialog(Activity act, String content) {
        if (null != act) {
            AppCompatDialog appCompatDialog = new AppCompatDialog(act, R.style.LoadingDialogTheme);
            View view = act.getLayoutInflater().inflate(R.layout.layout_loading, null);
            appCompatDialog.setContentView(view);
            TextView title = view.findViewById(R.id.content);
            ImageView progress = view.findViewById(R.id.progress);
            if (!CommonUtil.isBlank(content)) {
                title.setText("" + content);
            }
            Animation antv = AnimationUtils.loadAnimation(act, R.anim.loading_progressbar);
            LinearInterpolator lin = new LinearInterpolator();
            antv.setInterpolator(lin);
            antv.setRepeatCount(-1);
            progress.startAnimation(antv);
            appCompatDialog.setCanceledOnTouchOutside(false);
            return appCompatDialog;
        } else {
            return null;
        }
    }

    public interface Listener {
        void onItemLeft();

        void onItemRight();
    }

    public interface OnClickListener {
        void onClick(View view, Object object);
    }

    public AlertDialog confirmDialog(Activity act, String content, final Listener listener) {
        return confirmDialog(act, null, content, null, null, listener);
    }

    public AlertDialog confirmDialog(Activity act, String title, String content, final Listener listener) {
        return confirmDialog(act, title, content, null, null, listener);
    }

    public AlertDialog confirmDialog(Activity act, String title, String content, String cancelText, String confirmText, final Listener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(act, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(act.getResources().getColor(R.color.transparent));

        View contentView = LayoutInflater.from(act).inflate(R.layout.view_confirm_dialog_alert, null);
        window.setContentView(contentView);
        ViewConfirmDialogAlertBinding binding = DataBindingUtil.bind(contentView);

        binding.contentView.setText(content);
        if (!CommonUtil.isBlank(title)) {
            binding.titleView.setText(title);
        }
        if (!CommonUtil.isBlank(cancelText)) {
            binding.cancelView.setText(cancelText);
        }
        if (!CommonUtil.isBlank(confirmText)) {
            binding.confirmView.setText(confirmText);
        }
        binding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLeft();
                }
                dialog.cancel();
            }
        });
        binding.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemRight();
                }
                dialog.cancel();
            }
        });
        return dialog;
    }

    /**
     * @param type 0-签到成功  1-补签成功 2-领取金币 3-抽奖领取金币
     * @return
     */
    public AlertDialog signInDialog(Activity act, int type, String title, String content, String cancelText, String confirmText, final Listener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(act, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(act.getResources().getColor(R.color.transparent));

        View contentView = LayoutInflater.from(act).inflate(R.layout.view_sign_in_dialog, null);
        window.setContentView(contentView);
        ViewSignInDialogBinding binding = DataBindingUtil.bind(contentView);

        binding.signInContainer.setVisibility(type == 0 || type == 2 || type == 3 ? View.VISIBLE : View.GONE);
        binding.signInPatchContainer.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        binding.cancelView.setVisibility(type == 3 ? View.VISIBLE : View.GONE);
        binding.container.setBackgroundResource(
                type == 1 ? R.mipmap.ic_sw_sign_in_bu :
                        R.mipmap.ic_sw_sign_in);

        if (!CommonUtil.isBlank(confirmText)) {
            binding.confirmView.setText(confirmText);
        }
        binding.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        binding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLeft();
                }
                dialog.cancel();
            }
        });
        binding.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemRight();
                }
                dialog.cancel();
            }
        });
        return dialog;
    }

    public AlertDialog sendJuBenDialog(Activity act, String title, String content, String cancelText, String confirmText, final Listener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(act, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(act.getResources().getColor(R.color.transparent));

        View contentView = LayoutInflater.from(act).inflate(R.layout.view_send_juben_dialog, null);
        window.setContentView(contentView);
        ViewSendJubenDialogBinding binding = DataBindingUtil.bind(contentView);

        if (!CommonUtil.isBlank(cancelText)) {
            binding.cancelView.setText(cancelText);
        }
        if (!CommonUtil.isBlank(confirmText)) {
            binding.confirmView.setText(confirmText);
        }
        binding.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLeft();
                }
                dialog.cancel();
            }
        });
        binding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLeft();
                }
                dialog.cancel();
            }
        });
        binding.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemRight();
                }
                dialog.cancel();
            }
        });
        return dialog;
    }

    public AlertDialog addPermissionDialog(Activity activity, String title, Listener listener) {
        return addPermissionDialog(activity, title, null, null, listener);
    }

    public AlertDialog addPermissionDialog(Activity activity, String title, String cancelText, String confirmText, final Listener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        View contentView = LayoutInflater.from(activity).inflate(R.layout.view_add_permission_dialog, null);
        window.setContentView(contentView);
        ViewAddPermissionDialogBinding binding = DataBindingUtil.bind(contentView);

        if (!CommonUtil.isBlank(title)) {
            binding.titleView.setText(title);
        }
        if (!CommonUtil.isBlank(cancelText)) {
            binding.cancelView.setText(cancelText);
        }
        if (!CommonUtil.isBlank(confirmText)) {
            binding.confirmView.setText(confirmText);
        }
        binding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLeft();
                }
                dialog.cancel();
            }
        });
        binding.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemRight();
                }
                dialog.cancel();
            }
        });
        return dialog;
    }

    public AlertDialog logoutConfirmDialog(Activity act, String title, String content, String cancelText, String confirmText, final Listener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(act, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(act.getResources().getColor(R.color.transparent));

        View contentView = LayoutInflater.from(act).inflate(R.layout.view_confirm_dialog_alert, null);
        window.setContentView(contentView);
        ViewConfirmDialogAlertBinding binding = DataBindingUtil.bind(contentView);

        binding.contentView.setText(content);
        if (!CommonUtil.isBlank(title)) {
            binding.titleView.setText(title);
        }
        if (!CommonUtil.isBlank(cancelText)) {
            binding.cancelView.setText(cancelText);
        }
        if (!CommonUtil.isBlank(confirmText)) {
            binding.confirmView.setText(confirmText);
        }
        binding.cancelView.setTextColor(Color.parseColor("#BB1111"));
        binding.confirmView.setTextColor(Color.parseColor("#BB1111"));

        binding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemLeft();
                }
                dialog.cancel();
            }
        });
        binding.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemRight();
                }
                dialog.cancel();
            }
        });
        return dialog;
    }

    /**
     * @param activity
     * @param userName
     * @param listener
     */
    public void editUserNameDialog(final Activity activity, String userName, final OnClickListener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(activity, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(true);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.view_edit_user_name_dialog, null);
        window.setContentView(contentView);
        final ViewEditUserNameDialogBinding binding = DataBindingUtil.bind(contentView);
        binding.userNameView.setText(userName);
        if (!CommonUtil.isBlank(userName) && userName.length() > 0) {
            binding.userNameView.setSelection(userName.length());
        }
        binding.confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = binding.userNameView.getText().toString().trim();
                if (CommonUtil.isBlank(content)) {
                    ToastUtils.showShort(activity, "请输入昵称");
                    return;
                }
                listener.onClick(view, content);
                dialog.cancel();
            }
        });
        binding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    public static void showServiceDialog(Activity act, final OnClickListener onClickListener, final Listener listener) {
        final AlertDialog dialog = new AlertDialog.Builder(act, AlertDialog.THEME_HOLO_DARK).create();
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.getDecorView().setBackgroundColor(act.getResources().getColor(R.color.transparent));
        window.setContentView(R.layout.view_service_dialog_alert);
        TextView titleView = window.findViewById(R.id.titleView);
        TextView tvDesc = window.findViewById(R.id.tv_desc);
        TextView tvConfirm = window.findViewById(R.id.tv_confirm);
        TextView tvCancel = window.findViewById(R.id.tv_cancel);

        setTypeface(act, titleView);
        setTypeface(act, tvConfirm);
        setTypeface(act, tvCancel);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                onClickListener.onClick(v, null);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                onClickListener.onClick(v, null);
            }
        });

        String userText = "《服务协议》";
        String yinsiText = "《隐私政策》";
        String content = tvDesc.getText().toString();
        SpannableString spannableString = new SpannableString(content);
        // 设置字体颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#489DFA")), content.indexOf(userText), content.indexOf(userText) + userText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#489DFA")), content.indexOf(yinsiText), content.indexOf(yinsiText) + yinsiText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置字体大小
//        spannableString.setSpan(new AbsoluteSizeSpan(CommonUtil.sp2px(mContext, 14)), content.indexOf(stateText), content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置点击
        spannableString.setSpan(new MyClickableSpan(act, userText, listener), content.indexOf(userText), content.indexOf(userText) + userText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(act, yinsiText, listener), content.indexOf(yinsiText), content.indexOf(yinsiText) + yinsiText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvDesc.setText(spannableString);
        tvDesc.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件

    }

    public static class MyClickableSpan extends ClickableSpan {

        private Activity activity;
        private String msg;
        private Listener listener;

        public MyClickableSpan(Activity activity, String cm, Listener listener) {
            this.activity = activity;
            this.msg = cm;
            this.listener = listener;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View view) {
            if (msg.equals("《服务协议》")) {
                listener.onItemLeft();
            } else if (msg.equals("《隐私政策》")) {
                listener.onItemRight();
            }
        }
    }

    public static void setTypeface(Activity activity, TextView textView) {
//        Typeface typeface = BaseApplication.getInstance().getTypeface();
//        textView.setTypeface(typeface);
    }

}

