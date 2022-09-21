package com.quakoo.im.activity;


import android.os.Bundle;

import com.quakoo.im.R;
import com.quakoo.im.databinding.ActivityImchatBinding;

public class IMChatActivity extends BaseActivity {

    private ActivityImchatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_imchat);
    }
}