package com.yuoxi.android.app.activity.shiwu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.base.manager.DialogManager;
import com.base.view.BaseBottomSheetDialog;
import com.base.view.OnMultiClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.juben.JuBenDetailsActivity;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.databinding.ActivitySwhomeBinding;
import com.yuoxi.android.app.databinding.DialogBuyJubenBinding;
import com.yuoxi.android.app.databinding.DialogChoujiangBinding;
import com.yuoxi.android.app.fragment.SWHomeFragment;

public class SWHomeActivity extends BaseActivity {

    private ActivitySwhomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_swhome);
        setStatusBarHeight();

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("日常任务", SWHomeFragment.getInstance(0));
        mainPagerAdapter.addFragment("新手任务", SWHomeFragment.getInstance(1));

        binding.viewPager.setAdapter(mainPagerAdapter);
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.swGoldView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(SWGoldActivity.class);
            }
        });
    }

    public void onClickSignIn(View view) {
        switch (view.getId()) {
            case R.id.signIn1View:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        1,"", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn2View:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0,"", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn3View:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0,"", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn4View:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0,"", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn5View:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0,"", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn6View:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0,"", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn7View:

                break;
        }

    }

    public void onClickChouJiang(View view) {
        View contentView = LayoutInflater.from(SWHomeActivity.this).inflate(R.layout.dialog_choujiang, null);
        DialogChoujiangBinding buyJuBenBinding = DataBindingUtil.bind(contentView);
        BaseBottomSheetDialog bottomSheetDialog = new BaseBottomSheetDialog(SWHomeActivity.this) {
            @Override
            protected View initContentView() {
                return buyJuBenBinding.getRoot();
            }
        };
        bottomSheetDialog.show();
        buyJuBenBinding.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

    }
}