package com.yuoxi.android.app.activity.juben;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.base.manager.DialogManager;
import com.base.utils.CommonUtil;
import com.base.utils.ToastUtils;
import com.base.view.OnClickListener;
import com.yuoxi.android.app.R;
import com.yuoxi.android.app.activity.BaseActivity;
import com.yuoxi.android.app.activity.SearchActivity;
import com.yuoxi.android.app.adapter.AJJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.MainPagerAdapter;
import com.yuoxi.android.app.adapter.SearchHistoryAdapter;
import com.yuoxi.android.app.databinding.ActivityJBSearchBinding;
import com.yuoxi.android.app.databinding.ActivitySearchBinding;
import com.yuoxi.android.app.db.DBManager;
import com.yuoxi.android.app.fragment.JBHomeChildFragment;
import com.yuoxi.android.app.fragment.JBHomeFragment;
import com.yuoxi.android.app.view.FlowLayoutManager;
import com.yuoxi.android.app.view.SpaceItemDecoration;

import java.util.List;

public class JBSearchActivity extends BaseActivity {

    private ActivityJBSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_j_b_search);

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment("全部", JBHomeChildFragment.getInstance(0, 0));
        mainPagerAdapter.addFragment("等待中", JBHomeChildFragment.getInstance(1, 1));
        mainPagerAdapter.addFragment("游戏中", JBHomeChildFragment.getInstance(2, 1));

        binding.viewPager.setAdapter(mainPagerAdapter);
        binding.viewPager.setCurrentItem(0);
        binding.viewPager.setOffscreenPageLimit(mainPagerAdapter.getCount());
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    binding.contentContainer.setVisibility(View.GONE);
                    binding.searchView.setVisibility(View.GONE);
                    binding.backView.setVisibility(View.VISIBLE);
                } else {
                    binding.searchView.setVisibility(View.VISIBLE);
                    binding.backView.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onClickSearch(binding.searchView);

                }
                return false;
            }
        });
    }

    public void onClickSearch(View view) {
        String content = binding.editText.getText().toString().trim();
        CommonUtil.hideSoftInput(JBSearchActivity.this, binding.editText);
        if (!TextUtils.isEmpty(content)) {
            binding.contentContainer.setVisibility(View.VISIBLE);


        } else {
            binding.contentContainer.setVisibility(View.GONE);
            ToastUtils.showShort(JBSearchActivity.this, "请输入搜索内容");

        }
    }

    public void onClickClearText(View view) {
        binding.editText.setText("");
    }

    public void onClickBack(View view) {
        finish();
    }

}