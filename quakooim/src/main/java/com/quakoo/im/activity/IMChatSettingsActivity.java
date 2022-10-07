package com.quakoo.im.activity;

import android.os.Bundle;
import android.view.View;

import com.base.manager.DialogManager;
import com.quakoo.im.R;
import com.quakoo.im.databinding.ActivityImChatSettingsBinding;

public class IMChatSettingsActivity extends BaseActivity {

    private ActivityImChatSettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_im_chat_settings);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickTopChat(View view) {
        binding.topChatView.setSelected(!binding.topChatView.isSelected());
    }

    public void onClickEditRemarkName(View view) {
        DialogManager.getInstance().editUserNameDialog(IMChatSettingsActivity.this, getUserInfo().getName(),
                new DialogManager.OnClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        if (object instanceof String) {
                            String content = (String) object;
                            binding.remarkNameView.setText(content);
                        }
                    }
                });
    }
}