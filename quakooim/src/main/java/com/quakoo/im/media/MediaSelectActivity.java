package com.quakoo.im.media;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.base.manager.DialogManager;
import com.base.utils.CommonUtil;
import com.base.utils.GsonUtils;
import com.base.utils.ToastUtils;
import com.base.view.GridItemDecoration;
import com.base.view.OnClickListener;
import com.quakoo.im.R;
import com.quakoo.im.activity.BaseActivity;
import com.quakoo.im.databinding.ActivityMediaSelectBinding;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

public class MediaSelectActivity extends BaseActivity {

    private ActivityMediaSelectBinding binding;
    private MediaFileAdapter adapter;

    private int maxNumber = 1;
    private int mediaType = MediaUtils.MEDIA_TYPE_ALL;

    private List<MediaFile> selects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_media_select);
        addActivity(this);

        maxNumber = getIntent().getIntExtra("maxNumber", 1);
        mediaType = getIntent().getIntExtra("mediaType", MediaUtils.MEDIA_TYPE_PHOTO);

        GridItemDecoration.Builder builder = new GridItemDecoration.Builder(this);
        builder.color(R.color.transparent);
        builder.size(CommonUtil.dip2px(this, 1));
        binding.recyclerView.addItemDecoration(new GridItemDecoration(builder));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        binding.recyclerView.setNestedScrollingEnabled(false);
        adapter = new MediaFileAdapter(getApplication());
        binding.recyclerView.setAdapter(adapter);
        adapter.setMaxNumber(maxNumber);
        adapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                MediaFile mediaFile = (MediaFile) object;
                int id = view.getId();
                if (id == R.id.selectView) {
                    if (mediaFile.getStatus() == 1) {
                        selects.add(mediaFile);
                    } else {
                        selects.remove(mediaFile);
                    }
                    binding.submitView.setText("发送" + (selects.size() > 0 ? "(" + selects.size() + "/" + maxNumber + ")" : ""));
                    adapter.setComplete(maxNumber > selects.size() ? false : true);
                } else if (id == R.id.coverView) {//                        DialogUtil.getInstance().showMoreImageView(MediaSelectActivity.this, Arrays.asList(mediaFile), 0, null);
                }


            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });

        binding.submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selects.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("imageJson", GsonUtils.toJson(selects));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtils.showShort(MediaSelectActivity.this, "请选择照片");
                }
            }
        });


        getLocalMedia();


    }

    @SuppressLint("WrongConstant")
    private void getLocalMedia() {
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                // 准备方法，在请求权限之前先运行改方法，已经拥有权限不会触发该方法
                .rationale((context, permissions, executor) -> {
                    // 此处可以选择显示提示弹窗
                    String title = "你已禁止授予" + getString(R.string.app_name) + " 读写手机存储 权限,可能会造成功能不可用,如需使用请到设置里授予权限";
                    DialogManager.getInstance().addPermissionDialog(MediaSelectActivity.this, title, new DialogManager.Listener() {
                        @Override
                        public void onItemLeft() {
                            String title = "你已禁止授予" + getString(R.string.app_name) + " 读写手机存储 权限";
                            ToastUtils.showShort(getApplicationContext(), title);
                        }

                        @Override
                        public void onItemRight() {
                            executor.execute();
                        }
                    });
                })
                // 用户给权限了
                .onGranted(permissions -> {
                    MediaUtils.getLocalMedia(getApplicationContext(), mediaType, new MediaUtils.LocalMediaCallback() {
                        @Override
                        public void onLocalMediaFileUpdate(final List<MediaFile> mediaFiles) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter != null) {
                                        adapter.loadMoreData(mediaFiles);
                                    }
                                }
                            });
                        }
                    });

                })
                // 用户拒绝权限，包括不再显示权限弹窗也在此列
                .onDenied(permissions -> {
                    /**
                     * 判断用户是否点击了禁止后不再询问
                     */
                    if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                        //true，弹窗再次向用户索取权限
                        String title = "你已禁止授予" + getString(R.string.app_name) + " 读写手机存储 权限,可能会造成功能不可用,如需使用请到设置里授予权限";
                        DialogManager.getInstance().addPermissionDialog(MediaSelectActivity.this, title, "取消", "前往设置", new DialogManager.Listener() {
                            @Override
                            public void onItemLeft() {

                            }

                            @Override
                            public void onItemRight() {
                                CommonUtil.toSetting(getApplicationContext());
                            }
                        });
                    } else {
                        String title = "你已禁止授予" + getString(R.string.app_name) + " 读写手机存储 权限";
                        ToastUtils.showShort(getApplicationContext(), title);
                    }

                })
                .start();

    }

    public void onClickPreview(View view) {
//        DialogUtil.getInstance().showMoreImageView(MediaSelectActivity.this, selects, 0, new OnClickListener() {
//            @Override
//            public void onClick(View view, Object object) {
//                if (object instanceof MediaFile) {
//                    MediaFile mediaFile = (MediaFile) object;
//                    switch (view.getId()) {
//                        case R.id.selectView:
//                            if (adapter.getList().indexOf(mediaFile) != -1) {
//                                adapter.notifyItemInserted(adapter.getList().indexOf(mediaFile));
//                                selects.remove(mediaFile);
//                                adapter.setComplete(maxNumber > selects.size() ? false : true);
//                            }
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onLongClick(View view, Object object) {
//
//            }
//        });

    }

    public void onClickBack(View view) {
        finish();

    }
}