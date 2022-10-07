package com.yuoxi.android.app.activity.shiwu;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        onSignInState(binding.signIn1Container,binding.signIn1TitleTextView,binding.signIn1ContentTextView,binding.signIn1StateView,3);
        onSignInState(binding.signIn2Container,binding.signIn2TitleTextView,binding.signIn1ContentTextView,binding.signIn2StateView,2);
        onSignInState(binding.signIn3Container,binding.signIn3TitleTextView,binding.signIn3ContentTextView,binding.signIn3StateView,1);
        onSignInState(binding.signIn4Container,binding.signIn4TitleTextView,binding.signIn4ContentTextView,binding.signIn4StateView,0);
        onSignInState(binding.signIn5Container,binding.signIn5TitleTextView,binding.signIn5ContentTextView,binding.signIn5StateView,0);
        onSignInState(binding.signIn6Container,binding.signIn6TitleTextView,binding.signIn6ContentTextView,binding.signIn6StateView,0);
        onSignInState(binding.signIn7Container,binding.signIn7TitleTextView,binding.signIn7ContentTextView,binding.signIn7StateView,0);

    }

    /**
     * @param state 0-未签到 1-当前签到 2-已签到 3-补签 4-七天签到
     */
    public void onSignInState(View container, TextView titleTextView, TextView contentTextView, ImageView stateView, int state) {
        if (state == 0) {
            titleTextView.setTextColor(Color.parseColor("#4e3e51"));
            contentTextView.setTextColor(Color.parseColor("#4e3e51"));
            stateView.setBackgroundResource(R.drawable.bg_transparent);
            container.setBackgroundColor(Color.parseColor("#F6F7FB"));
        }else if (state == 1) {
            titleTextView.setTextColor(Color.parseColor("#ffffff"));
            contentTextView.setTextColor(Color.parseColor("#ffffff"));
            stateView.setBackgroundResource(R.drawable.bg_transparent);
            container.setBackgroundColor(Color.parseColor("#D548F6"));
        }else if (state == 2) {
            titleTextView.setTextColor(Color.parseColor("#4e3e51"));
            contentTextView.setTextColor(Color.parseColor("#ffffff"));
            stateView.setBackgroundResource(R.mipmap.ic_sign_in_s);
            container.setBackgroundColor(Color.parseColor("#F6F7FB"));
        }else if (state == 3) {
            titleTextView.setText("点击补签");
            contentTextView.setText("补签");
            contentTextView.setBackgroundResource(R.drawable.button_mg);
            titleTextView.setTextColor(Color.parseColor("#4e3e51"));
            contentTextView.setTextColor(Color.parseColor("#4e3e51"));
            stateView.setBackgroundResource(R.drawable.bg_transparent);
            container.setBackgroundColor(Color.parseColor("#F6F7FB"));
        }
    }

    public void onClickSignIn(View view) {
        switch (view.getId()) {
            case R.id.signIn1Container:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        1, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn2Container:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn3Container:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn4Container:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn5Container:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn6Container:
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        0, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
                break;
            case R.id.signIn7Container:

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
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isRunning = false;
            }
        });
        bottomSheetDialog.show();

        buyJuBenBinding.choujiangButton1.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    mIsLucky = !mIsLucky;
                    startAnimation(buyJuBenBinding.zhuanpanView);
                }
            }
        });
        buyJuBenBinding.choujiangButton2.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                if (!isRunning) {
                    isRunning = true;
                    mIsLucky = !mIsLucky;
                    startAnimation(buyJuBenBinding.zhuanpanView);
                }
            }
        });
        buyJuBenBinding.gameRuleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity(SWGameRuleActivity.class);
            }
        });
        buyJuBenBinding.closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

    }


    private Animation mStartAnimation;
    private boolean isRunning;
    private float fromDegrees = 0;

    private boolean mIsLucky = false;

    /**
     * 开启动画
     * 5秒旋转5圈+中奖所在位置角度
     */
    private void startAnimation(ImageView mLuckyTurntable) {
        float toDegree;//结束角度（以实际转盘图为准计算角度）
        if (mIsLucky) {
            toDegree = (float) (360 * 5 + (Math.random() * 10 + 1) * 36 + 36 / 2 + fromDegrees);
        } else {
            toDegree = (float) (360 * 5 + (Math.random() * 10 + 1) * 36 + 36 / 2 + fromDegrees);
        }

        if (mStartAnimation != null) {
            mStartAnimation.reset();
        }

        // 按中心点旋转 toDegree度
        // 参数：旋转的开始角度、旋转的结束角度、X轴的伸缩模式、X坐标的伸缩值、Y轴的伸缩模式、Y坐标的伸缩值
        mStartAnimation = new RotateAnimation(fromDegrees, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mStartAnimation.setDuration(5000); // 设置旋转时间
        mStartAnimation.setRepeatCount(0); // 设置重复次数
        mStartAnimation.setFillAfter(true);// 动画执行完后是否停留在执行完的状态
        mStartAnimation.setInterpolator(new AccelerateDecelerateInterpolator()); // 动画播放的速度
        mStartAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isRunning = false;
                fromDegrees = toDegree;
                DialogManager.getInstance().signInDialog(SWHomeActivity.this,
                        3, "", "", "", "",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLuckyTurntable.startAnimation(mStartAnimation);
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickGold(View view) {
        openActivity(SWGoldActivity.class);
    }
}