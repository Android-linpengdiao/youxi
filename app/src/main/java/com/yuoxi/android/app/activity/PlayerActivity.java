package com.yuoxi.android.app.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.base.utils.GlideLoader;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.databinding.ActivityPlayerBinding;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.component.CompleteView;
import xyz.doikki.videocontroller.component.ErrorView;
import xyz.doikki.videocontroller.component.GestureView;
import xyz.doikki.videocontroller.component.PrepareView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.player.VideoView;
import xyz.doikki.videoplayer.util.L;

public class PlayerActivity extends BaseActivity {

    private ActivityPlayerBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_player);

        initVideoView("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");

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

        binding.videoView.start();

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