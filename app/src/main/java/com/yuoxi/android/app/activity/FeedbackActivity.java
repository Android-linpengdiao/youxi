package com.yuoxi.android.app.activity;

import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.base.manager.LoadingManager;
import com.base.utils.CommonUtil;
import com.base.utils.FileUtils;
import com.base.utils.ToastUtils;
import com.base.view.GridItemDecoration;
import com.base.view.OnClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.okhttp.callbacks.StringCallback;
import com.okhttp.utils.APIUrls;
import com.okhttp.utils.OkHttpUtils;
import com.quakoo.im.media.MediaFile;
import com.quakoo.im.media.MediaSelectActivity;
import com.quakoo.im.media.MediaUtils;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.adapter.ImageAdapter;
import com.yuoxi.android.app.databinding.ActivityFeedbackBinding;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class FeedbackActivity extends BaseActivity {

    private ActivityFeedbackBinding binding;
    private ImageAdapter imageAdapter;
    private List<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_feedback);

        GridItemDecoration.Builder builder = new GridItemDecoration.Builder(this);
        builder.color(R.color.transparent);
        builder.size(CommonUtil.dip2px(this, 10));

        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.addItemDecoration(new GridItemDecoration(builder));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageAdapter = new ImageAdapter(this);
        binding.recyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                if (object instanceof Integer) {
                    int position = (int) object;
                    if (position == (imageList.size() - 1)) {
                        if (imageList.size() < 10) {

                            Bundle bundle = new Bundle();
                            bundle.putInt("mediaType", MediaUtils.MEDIA_TYPE_PHOTO);
                            bundle.putInt("maxNumber", 10 - imageAdapter.getList().size());
                            openActivity(MediaSelectActivity.class, bundle, 100);

//                            if (checkPermissions(PermissionUtils.STORAGE, 100)) {
//                                Bundle bundle = new Bundle();
//                                bundle.putInt("mediaType", MediaUtils.MEDIA_TYPE_PHOTO);
//                                bundle.putInt("maxNumber", 10 - imageAdapter.getList().size());
//                                openActivity(MediaSelectActivity.class, bundle, 100);
//                            }
                        } else {
                            ToastUtils.showShort(FeedbackActivity.this, "最多上传9张图片");
                        }
                    }
                }
            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });
        imageList.add("add");
        imageAdapter.refreshData(imageList);

    }

    public void onClickSubmit(View view) {
        String content = binding.contentView.getText().toString().trim();
        if (CommonUtil.isBlank(content)) {
            ToastUtils.showShort(getApplicationContext(), "请填写您要反馈的内容");
            return;
        }

        List<String> imgsJson = new ArrayList<>();
        if (imageList.size() > 0) {
            for (String url : imageList) {
                if (!CommonUtil.isBlank(url) && !url.equals("add")) {
                    imgsJson.add(url);
                }
            }
        }

//        int typeId = 0;
//        if (tagAdapter.getPosition() < tagAdapter.getList().size()) {
//            typeId = tagAdapter.getList().get(tagAdapter.getPosition()).getId();
//        }
//
//        SendRequest.suggestion_add(content, GsonUtils.toJson(imgsJson), typeId, new GenericsCallback<BaseData>(new JsonGenericsSerializator()) {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                ToastUtils.showShort(getApplicationContext(), "请求失败");
//            }
//
//            @Override
//            public void onResponse(BaseData response, int id) {
//                if (response.isSuccess()) {
//                    ToastUtils.showShort(getApplicationContext(), "已提交");
//                    finish();
//                } else {
//                    ToastUtils.showShort(getApplicationContext(), response.getMsg());
//
//                }
//            }
//        });

    }

    public void onClickBack(View view) {
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 100:
                    compressImage(data);

                    break;
            }
        }
    }

    private AtomicInteger progress;
    private List<MediaFile> mediaFiles;

    private void compressImage(Intent data) {
        progress = new AtomicInteger(0);
        progress.addAndGet(0);
        if (mediaFiles != null)
            mediaFiles.clear();
        try {
            if (data != null) {
                String imageJson = data.getStringExtra("imageJson");
                if (!TextUtils.isEmpty(imageJson)) {
                    Gson gson = new Gson();
                    mediaFiles = gson.fromJson(imageJson, new TypeToken<List<MediaFile>>() {
                    }.getType());
//                    LoadingManager.showLoadingDialog(FeedbackActivity.this);
                    if (mediaFiles != null && mediaFiles.size() > 0) {
                        for (int i = 0; i < mediaFiles.size(); i++) {
                            String path = mediaFiles.get(i).getPath();
                            Luban.with(this)
                                    .load(path)// 传人要压缩的图片列表
                                    .ignoreBy(500)// 忽略不压缩图片的大小
                                    .setTargetDir(FileUtils.getTempPath())// 设置压缩后文件存储位置
                                    .setCompressListener(new OnCompressListener() { //设置回调
                                        @Override
                                        public void onStart() {
                                        }

                                        @Override
                                        public void onSuccess(File file) {
//                                            uploadFile(file.getAbsolutePath());

                                            try {
                                                imageList.add(imageAdapter.getList().size() - 1, file.getAbsolutePath());
                                                imageAdapter.notifyDataSetChanged();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onError(Throwable e) {
//                                            uploadFile(path);
                                        }
                                    }).launch();//启动压缩

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void uploadFile(String filePath) {
        String filename = filePath.substring(filePath.lastIndexOf("/") + 1);
        OkHttpUtils.post()
                .addFile("file", filename, new File(filePath))
                .url(APIUrls.storage_handle)
                .build()
                .execute(new StringCallback() {

//                    @Override
//                    public void onBefore(Request request, int id) {
//                        LoadingManager.showLoadingDialog(FeedbackActivity.this);
//                    }
//
//                    @Override
//                    public void onAfter(int id) {
//                        LoadingManager.hideLoadingDialog(FeedbackActivity.this);
//                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progress.addAndGet(1);
                        imageList.add(imageAdapter.getList().size() - 1, "");
                        imageAdapter.notifyDataSetChanged();
                        if (mediaFiles != null && mediaFiles.size() == progress.get()) {
                            LoadingManager.hideLoadingDialog(FeedbackActivity.this);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progress.addAndGet(1);
                        try {
                            JSONObject object = new JSONObject(response);
                            String icon = object.getString("ok");
                            imageList.add(imageAdapter.getList().size() - 1, icon);
                            imageAdapter.notifyDataSetChanged();
                            if (mediaFiles != null && mediaFiles.size() == progress.get()) {
                                LoadingManager.hideLoadingDialog(FeedbackActivity.this);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }
                });
    }
}