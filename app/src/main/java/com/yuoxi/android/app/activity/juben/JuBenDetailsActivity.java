package com.yuoxi.android.app.activity.juben;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.manager.DialogManager;
import com.base.utils.CommonUtil;
import com.base.utils.FileUtils;
import com.base.utils.GlideLoader;
import com.base.view.BaseBottomSheetDialog;
import com.base.view.OnClickListener;
import com.base.view.OnMultiClickListener;
import com.okhttp.utils.APIUrls;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.CommentActivity;
import com.yuoxi.android.app.adapter.CommentAdapter;
import com.yuoxi.android.app.adapter.FocusFansAdapter;
import com.yuoxi.android.app.adapter.JuBenConfigAdapter;
import com.yuoxi.android.app.adapter.JuBenPersonHorizontalAdapter;
import com.yuoxi.android.app.adapter.UserAdapter;
import com.yuoxi.android.app.adapter.UserIconAdapter;
import com.yuoxi.android.app.databinding.ActivityJuBenDetailsBinding;
import com.yuoxi.android.app.databinding.DialogBuyJubenBinding;
import com.yuoxi.android.app.databinding.DialogShareBinding;
import com.yuoxi.android.app.databinding.DialogUserListBinding;
import com.yuoxi.android.app.databinding.DialogYuyueJubenBinding;

import java.util.Arrays;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.L;

public class JuBenDetailsActivity extends BaseActivity {

    private ActivityJuBenDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_ju_ben_details);
        setStatusBarHeight();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.headerLayout.getLayoutParams();
            layoutParams.topMargin = CommonUtil.getStatusBarHeight(getApplication()) + CommonUtil.dip2px(getApplication(), 45);
        }
        initView();
    }

    private void initView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.horizontalRecyclerView.setLayoutManager(layoutManager);
        JuBenPersonHorizontalAdapter personHorizontalAdapter = new JuBenPersonHorizontalAdapter(this);
        binding.horizontalRecyclerView.setAdapter(personHorizontalAdapter);
        personHorizontalAdapter.refreshData(CommonUtil.getTitles());


        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter commentAdapter = new CommentAdapter(this);
        binding.commentRecyclerView.setAdapter(commentAdapter);
        commentAdapter.refreshData(CommonUtil.getTitles());

        binding.scrollView.setVisibility(View.VISIBLE);
        GlideLoader.getInstance().LoaderImage(this, "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF", binding.coverView, 6);
        GlideLoader.getInstance().LoaderBlurImage(this, "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF", binding.backgroundView);
        binding.headerContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.backgroundView.getLayoutParams().height = binding.headerContainer.getHeight();
                if (Build.VERSION.SDK_INT >= 16) {
                    binding.headerContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    binding.headerContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int x, int y, int oldx, int oldy) {
                    float height = CommonUtil.dip2px(getApplication(), 300);
                    float percent = (float) y / height;  //滑动比例
                    binding.topView.setBackgroundColor(Color.parseColor("#2A2737"));
                    if (y > height) {
//                        binding.titleView.setTextColor(getColor(R.color.black));
//                        setStatusBarDarkTheme(true);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            binding.backView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(JuBenDetailsActivity.this, R.color.black)));
//                            binding.shareView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(JuBenDetailsActivity.this, R.color.black)));
//                        }
                    } else {
                        int alpha = (int) (percent * 255);  //渐变变换
                        binding.topView.getBackground().setAlpha(alpha);
//                        binding.titleView.setTextColor(getColor(R.color.white));
//                        setStatusBarDarkTheme(false);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            binding.backView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(JuBenDetailsActivity.this, R.color.white)));
//                            binding.shareView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(JuBenDetailsActivity.this, R.color.white)));
//                        }
                    }
                }
            });
        }

        initVideoView("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");
    }

    public void onClickSendJuBen(View view) {
        View contentView = LayoutInflater.from(JuBenDetailsActivity.this).inflate(R.layout.dialog_user_list, null);
        DialogUserListBinding userListBinding = DataBindingUtil.bind(contentView);
        BaseBottomSheetDialog bottomSheetDialog = new BaseBottomSheetDialog(JuBenDetailsActivity.this) {
            @Override
            protected View initContentView() {
                return userListBinding.getRoot();
            }
        };
        bottomSheetDialog.show();
        userListBinding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        userListBinding.recyclerView.setLayoutManager(new LinearLayoutManager(JuBenDetailsActivity.this));
        userListBinding.recyclerView.setNestedScrollingEnabled(false);
        FocusFansAdapter focusFansAdapter = new FocusFansAdapter(JuBenDetailsActivity.this);
        focusFansAdapter.setType(2);
        userListBinding.recyclerView.setAdapter(focusFansAdapter);
        focusFansAdapter.refreshData(CommonUtil.getTitles());
        focusFansAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                DialogManager.getInstance().sendJuBenDialog(JuBenDetailsActivity.this,
                        "", "", "取消", "确认赠送",
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
            public void onLongClick(View view, Object object) {

            }
        });
    }

    public void onClickBuyJuBen(View view) {
        View contentView = LayoutInflater.from(JuBenDetailsActivity.this).inflate(R.layout.dialog_buy_juben, null);
        DialogBuyJubenBinding buyJuBenBinding = DataBindingUtil.bind(contentView);
        BaseBottomSheetDialog bottomSheetDialog = new BaseBottomSheetDialog(JuBenDetailsActivity.this) {
            @Override
            protected View initContentView() {
                return buyJuBenBinding.getRoot();
            }
        };
        bottomSheetDialog.show();
        buyJuBenBinding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        buyJuBenBinding.buyView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                DialogManager.getInstance().sendJuBenDialog(JuBenDetailsActivity.this,
                        "", "", "取消", "立即购买",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
            }
        });
        buyJuBenBinding.buySuperView.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void OnMultiClick(View view) {
                DialogManager.getInstance().sendJuBenDialog(JuBenDetailsActivity.this,
                        "", "", "取消", "立即购买",
                        new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                            }
                        });
            }
        });
    }

    public void onClickSubscribeJuBen(View view) {
        View contentView = LayoutInflater.from(JuBenDetailsActivity.this).inflate(R.layout.dialog_yuyue_juben, null);
        DialogYuyueJubenBinding yuYueJuBenBinding = DataBindingUtil.bind(contentView);
        BaseBottomSheetDialog bottomSheetDialog = new BaseBottomSheetDialog(JuBenDetailsActivity.this) {
            @Override
            protected View initContentView() {
                return yuYueJuBenBinding.getRoot();
            }
        };
        bottomSheetDialog.show();
        yuYueJuBenBinding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.recyclerView.setLayoutManager(layoutManager);
        JuBenConfigAdapter juBenConfigAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.recyclerView.setAdapter(juBenConfigAdapter);
        juBenConfigAdapter.refreshData(Arrays.asList("是", "否"));

        LinearLayoutManager dengjiLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        dengjiLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.dengjiRecyclerView.setLayoutManager(dengjiLayoutManager);
        JuBenConfigAdapter dengjiAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.dengjiRecyclerView.setAdapter(dengjiAdapter);
        dengjiAdapter.refreshData(Arrays.asList("不限", "5", "10"));

        LinearLayoutManager roomTypeLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        roomTypeLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.roomTypeRecyclerView.setLayoutManager(roomTypeLayoutManager);
        JuBenConfigAdapter roomTypeAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.roomTypeRecyclerView.setAdapter(roomTypeAdapter);
        roomTypeAdapter.refreshData(Arrays.asList("公开房", "密码房"));


        LinearLayoutManager zaiciLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        zaiciLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.zaiciRecyclerView.setLayoutManager(zaiciLayoutManager);
        JuBenConfigAdapter zaiciAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.zaiciRecyclerView.setAdapter(zaiciAdapter);
        zaiciAdapter.refreshData(Arrays.asList("是", "否"));

        LinearLayoutManager xinyuLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        xinyuLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.xinyuRecyclerView.setLayoutManager(xinyuLayoutManager);
        JuBenConfigAdapter xinyuAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.xinyuRecyclerView.setAdapter(xinyuAdapter);
        xinyuAdapter.refreshData(Arrays.asList("不限", ">8", ">90"));

    }

    public void onClickComment(View view) {
        openActivity(CommentActivity.class);
    }

    public void onClickShare(View view) {
        View contentView = View.inflate(this, R.layout.dialog_share, null);
        DialogShareBinding shareBinding = DataBindingUtil.bind(contentView);
        BaseBottomSheetDialog shareBottomSheetDialog = new BaseBottomSheetDialog(this) {
            @Override
            protected View initContentView() {
                return shareBinding.getRoot();
            }
        };
        shareBottomSheetDialog.show();

        LinearLayoutManager userLayoutManager = new LinearLayoutManager(this);
        userLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        shareBinding.userRecyclerView.setLayoutManager(userLayoutManager);
        UserAdapter userAdapter = new UserAdapter(this);
        shareBinding.userRecyclerView.setAdapter(userAdapter);
        userAdapter.refreshData(CommonUtil.getTitles());


//        shareUrl = APIUrls.URL_DOMAIN + "share_view/invite.html?uid=";
//        shareTitle = "新的好友邀请！";
//        shareSummary = "我一起领养专属宠物体验全新的萌宠社交吧～";
//        thumbnailUrl = null;

//        String finalShareUrl = shareUrl;
//        String finalShareTitle = shareTitle;
//        String finalShareSummary = shareSummary;
//        String finalThumbnailUrl = thumbnailUrl;
        shareBinding.shareWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // scene 0代表好友   1代表朋友圈
//                WXManager.send(activity, 0, finalShareUrl, finalShareTitle, finalShareSummary, finalThumbnailUrl);
            }
        });
        shareBinding.shareWxMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // scene 0代表好友   1代表朋友圈
//                WXManager.send(activity, 1, finalShareUrl, finalShareTitle, finalShareSummary, finalThumbnailUrl);
            }
        });
        shareBinding.shareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TencentHelper.shareToQQ(activity, finalShareUrl, finalShareTitle, finalShareSummary, finalThumbnailUrl, uiListener);
            }
        });
//        shareBinding.shareQzone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                TencentHelper.shareToQzone(activity, finalShareUrl, finalShareTitle, finalShareSummary, finalThumbnailUrl, uiListener);
//            }
//        });
        shareBinding.shareWeiBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                WbManager.sendMultiMessage(shareHandler, finalShareTitle, finalShareSummary, finalThumbnailUrl, finalShareUrl);
            }
        });
        shareBinding.sharePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FileUtils.shareMultiImg(activity, "分享图片", "/storage/emulated/0/DCIM/Camera/IMG_20220216_182347.jpg");
//                shareMultiImg(activity, "分享图片", "/storage/emulated/0/练琴帮/1643017039586127.jpeg");

            }
        });


        shareBinding.cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareBottomSheetDialog.cancel();
            }
        });
    }


    protected void initVideoView(String url) {
        binding.videoView.release();
        StandardVideoController controller = new StandardVideoController(this);
        //根据屏幕方向自动进入/退出全屏
        controller.setEnableOrientation(false);

        //准备播放界面
        PrepareView prepareView = new PrepareView(this);
        prepareView.setClickStart();
        ImageView thumbView = prepareView.findViewById(R.id.thumb);
        String thumbUrl = "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF";
        GlideLoader.getInstance().LoaderVideoCover(this, thumbUrl, thumbView);
        controller.addControlComponent(prepareView);

        //自动完成播放界面
        controller.addControlComponent(new CompleteView(this));
        //错误界面
        controller.addControlComponent(new ErrorView(this));
        //标题栏
//        TitleView titleView = new TitleView(this);
//        controller.addControlComponent(titleView);
//        //设置标题
//        titleView.setTitle(title);

        //点播控制条
        VodControlView vodControlView = new VodControlView(this);
        //是否显示底部进度条。默认显示
        vodControlView.showBottomProgress(true);
        controller.addControlComponent(vodControlView);
        binding.videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);

        //滑动控制视图
        GestureView gestureControlView = new GestureView(this);
        controller.addControlComponent(gestureControlView);
//        //根据是否为直播决定是否需要滑动调节进度
//        controller.setCanChangePosition(!isLive);

        //注意：以上组件如果你想单独定制，我推荐你把源码复制一份出来，然后改成你想要的样子。
        //改完之后再通过addControlComponent添加上去
        //你也可以通过addControlComponent添加一些你自己的组件，具体实现方式参考现有组件的实现。
        //这个组件不一定是View，请发挥你的想象力😃

        //如果你不需要单独配置各个组件，可以直接调用此方法快速添加以上组件
//            controller.addDefaultControlComponent(title, isLive);

        //竖屏也开启手势操作，默认关闭
        controller.setEnableInNormal(false);
        //滑动调节亮度，音量，进度，默认开启
        controller.setGestureEnabled(true);
        //适配刘海屏，默认开启
        controller.setAdaptCutout(true);
        //双击播放暂停，默认开启
        controller.setDoubleTapTogglePlayEnabled(true);

        //在控制器上显示调试信息
//            controller.addControlComponent(new DebugInfoView(this));
        //在LogCat显示调试信息
//            controller.addControlComponent(new PlayerMonitor());

        //如果你不想要UI，不要设置控制器即可
        binding.videoView.setVideoController(controller);

        binding.videoView.setUrl(url);

        //保存播放进度
//        binding.videoView.setProgressManager(new ProgressManagerImpl());
        //播放状态监听
        binding.videoView.addOnStateChangeListener(mOnStateChangeListener);

        //临时切换播放核心，如需全局请通过VideoConfig配置，详见MyApplication
        //使用IjkPlayer解码
//            mVideoView.setPlayerFactory(IjkPlayerFactory.create());
        //使用ExoPlayer解码
//            mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        //使用MediaPlayer解码
//            mVideoView.setPlayerFactory(AndroidMediaPlayerFactory.create());

        //设置静音播放
//            mVideoView.setMute(true);

        //从设置的position开始播放
//            mVideoView.skipPositionWhenPlay(10000);
        // 视频画面缩放模式，默认按视频宽高比居中显示在VideoView中
//        binding.videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);

//        binding.videoView.start();

    }

    private VideoView.OnStateChangeListener mOnStateChangeListener = new VideoView.SimpleOnStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                case VideoView.PLAYER_NORMAL://小屏
                    break;
                case VideoView.PLAYER_FULL_SCREEN://全屏
                    break;
            }
        }

        @Override
        public void onPlayStateChanged(int playState) {
            switch (playState) {
                case VideoView.STATE_IDLE:
                    break;
                case VideoView.STATE_PREPARING:
                    break;
                case VideoView.STATE_PREPARED:
                    break;
                case VideoView.STATE_PLAYING:
                    //需在此时获取视频宽高
                    int[] videoSize = binding.videoView.getVideoSize();
                    L.d("视频宽：" + videoSize[0]);
                    L.d("视频高：" + videoSize[1]);
                    break;
                case VideoView.STATE_PAUSED:
                    break;
                case VideoView.STATE_BUFFERING:
                    break;
                case VideoView.STATE_BUFFERED:
                    break;
                case VideoView.STATE_PLAYBACK_COMPLETED:
                    break;
                case VideoView.STATE_ERROR:
                    break;
            }
        }
    };

    public void onClickBack(View view) {
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        //如果视频还在准备就 activity 就进入了后台，建议直接将 VideoView release
        //防止进入后台后视频还在播放
        if (binding.videoView != null) {
            binding.videoView.pause();
            if (binding.videoView.getCurrentPlayState() == VideoView.STATE_PREPARING) {
                binding.videoView.release();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding.videoView != null) {
            binding.videoView.release();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.videoView != null && binding.videoView.isFullScreen()) {
                binding.videoView.onBackPressed();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}