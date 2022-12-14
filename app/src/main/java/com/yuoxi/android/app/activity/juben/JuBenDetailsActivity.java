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
                    float percent = (float) y / height;  //????????????
                    binding.topView.setBackgroundColor(Color.parseColor("#2A2737"));
                    if (y > height) {
//                        binding.titleView.setTextColor(getColor(R.color.black));
//                        setStatusBarDarkTheme(true);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            binding.backView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(JuBenDetailsActivity.this, R.color.black)));
//                            binding.shareView.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(JuBenDetailsActivity.this, R.color.black)));
//                        }
                    } else {
                        int alpha = (int) (percent * 255);  //????????????
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
                        "", "", "??????", "????????????",
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
                        "", "", "??????", "????????????",
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
                        "", "", "??????", "????????????",
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
        juBenConfigAdapter.refreshData(Arrays.asList("???", "???"));

        LinearLayoutManager dengjiLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        dengjiLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.dengjiRecyclerView.setLayoutManager(dengjiLayoutManager);
        JuBenConfigAdapter dengjiAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.dengjiRecyclerView.setAdapter(dengjiAdapter);
        dengjiAdapter.refreshData(Arrays.asList("??????", "5", "10"));

        LinearLayoutManager roomTypeLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        roomTypeLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.roomTypeRecyclerView.setLayoutManager(roomTypeLayoutManager);
        JuBenConfigAdapter roomTypeAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.roomTypeRecyclerView.setAdapter(roomTypeAdapter);
        roomTypeAdapter.refreshData(Arrays.asList("?????????", "?????????"));


        LinearLayoutManager zaiciLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        zaiciLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.zaiciRecyclerView.setLayoutManager(zaiciLayoutManager);
        JuBenConfigAdapter zaiciAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.zaiciRecyclerView.setAdapter(zaiciAdapter);
        zaiciAdapter.refreshData(Arrays.asList("???", "???"));

        LinearLayoutManager xinyuLayoutManager = new LinearLayoutManager(JuBenDetailsActivity.this);
        xinyuLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        yuYueJuBenBinding.xinyuRecyclerView.setLayoutManager(xinyuLayoutManager);
        JuBenConfigAdapter xinyuAdapter = new JuBenConfigAdapter(JuBenDetailsActivity.this);
        yuYueJuBenBinding.xinyuRecyclerView.setAdapter(xinyuAdapter);
        xinyuAdapter.refreshData(Arrays.asList("??????", ">8", ">90"));

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
//        shareTitle = "?????????????????????";
//        shareSummary = "????????????????????????????????????????????????????????????";
//        thumbnailUrl = null;

//        String finalShareUrl = shareUrl;
//        String finalShareTitle = shareTitle;
//        String finalShareSummary = shareSummary;
//        String finalThumbnailUrl = thumbnailUrl;
        shareBinding.shareWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // scene 0????????????   1???????????????
//                WXManager.send(activity, 0, finalShareUrl, finalShareTitle, finalShareSummary, finalThumbnailUrl);
            }
        });
        shareBinding.shareWxMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // scene 0????????????   1???????????????
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
//                FileUtils.shareMultiImg(activity, "????????????", "/storage/emulated/0/DCIM/Camera/IMG_20220216_182347.jpg");
//                shareMultiImg(activity, "????????????", "/storage/emulated/0/?????????/1643017039586127.jpeg");

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
        //??????????????????????????????/????????????
        controller.setEnableOrientation(false);

        //??????????????????
        PrepareView prepareView = new PrepareView(this);
        prepareView.setClickStart();
        ImageView thumbView = prepareView.findViewById(R.id.thumb);
        String thumbUrl = "https://t7.baidu.com/it/u=2371362259,3988640650&fm=193&f=GIF";
        GlideLoader.getInstance().LoaderVideoCover(this, thumbUrl, thumbView);
        controller.addControlComponent(prepareView);

        //????????????????????????
        controller.addControlComponent(new CompleteView(this));
        //????????????
        controller.addControlComponent(new ErrorView(this));
        //?????????
//        TitleView titleView = new TitleView(this);
//        controller.addControlComponent(titleView);
//        //????????????
//        titleView.setTitle(title);

        //???????????????
        VodControlView vodControlView = new VodControlView(this);
        //??????????????????????????????????????????
        vodControlView.showBottomProgress(true);
        controller.addControlComponent(vodControlView);
        binding.videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);

        //??????????????????
        GestureView gestureControlView = new GestureView(this);
        controller.addControlComponent(gestureControlView);
//        //?????????????????????????????????????????????????????????
//        controller.setCanChangePosition(!isLive);

        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //?????????????????????addControlComponent????????????
        //??????????????????addControlComponent?????????????????????????????????????????????????????????????????????????????????
        //????????????????????????View???????????????????????????????

        //????????????????????????????????????????????????????????????????????????????????????????????????
//            controller.addDefaultControlComponent(title, isLive);

        //??????????????????????????????????????????
        controller.setEnableInNormal(false);
        //???????????????????????????????????????????????????
        controller.setGestureEnabled(true);
        //??????????????????????????????
        controller.setAdaptCutout(true);
        //?????????????????????????????????
        controller.setDoubleTapTogglePlayEnabled(true);

        //?????????????????????????????????
//            controller.addControlComponent(new DebugInfoView(this));
        //???LogCat??????????????????
//            controller.addControlComponent(new PlayerMonitor());

        //??????????????????UI??????????????????????????????
        binding.videoView.setVideoController(controller);

        binding.videoView.setUrl(url);

        //??????????????????
//        binding.videoView.setProgressManager(new ProgressManagerImpl());
        //??????????????????
        binding.videoView.addOnStateChangeListener(mOnStateChangeListener);

        //????????????????????????????????????????????????VideoConfig???????????????MyApplication
        //??????IjkPlayer??????
//            mVideoView.setPlayerFactory(IjkPlayerFactory.create());
        //??????ExoPlayer??????
//            mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        //??????MediaPlayer??????
//            mVideoView.setPlayerFactory(AndroidMediaPlayerFactory.create());

        //??????????????????
//            mVideoView.setMute(true);

        //????????????position????????????
//            mVideoView.skipPositionWhenPlay(10000);
        // ??????????????????????????????????????????????????????????????????VideoView???
//        binding.videoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);

//        binding.videoView.start();

    }

    private VideoView.OnStateChangeListener mOnStateChangeListener = new VideoView.SimpleOnStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                case VideoView.PLAYER_NORMAL://??????
                    break;
                case VideoView.PLAYER_FULL_SCREEN://??????
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
                    //??????????????????????????????
                    int[] videoSize = binding.videoView.getVideoSize();
                    L.d("????????????" + videoSize[0]);
                    L.d("????????????" + videoSize[1]);
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
        //??????????????????????????? activity ???????????????????????????????????? VideoView release
        //???????????????????????????????????????
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