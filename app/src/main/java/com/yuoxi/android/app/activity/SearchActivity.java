package com.yuoxi.android.app.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.yuoxi.android.app.adapter.AJJuBenVerticalAdapter;
import com.yuoxi.android.app.adapter.SearchHistoryAdapter;
import com.yuoxi.android.app.databinding.ActivitySearchBinding;
import com.yuoxi.android.app.db.DBManager;
import com.yuoxi.android.app.view.FlowLayoutManager;
import com.yuoxi.android.app.view.SpaceItemDecoration;

import java.util.List;

public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding binding;
    private SearchHistoryAdapter searchHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewData(R.layout.activity_search);

        searchHistoryAdapter = new SearchHistoryAdapter(this);
        binding.searchHistoryRecyclerView.addItemDecoration(new SpaceItemDecoration(CommonUtil.dip2px(this, 12)));
        binding.searchHistoryRecyclerView.setLayoutManager(new FlowLayoutManager());
        binding.searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);
        List<String> searchHistory = DBManager.getInstance().querySearchHistory(0);
        if (searchHistory.size() > 5) {
            searchHistory = searchHistory.subList(0, 5);
        }
        searchHistoryAdapter.refreshData(searchHistory);
        binding.searchHistoryView.setVisibility(searchHistoryAdapter.getList().size() > 0 ? View.VISIBLE : View.GONE);
        binding.searchHistoryRecyclerView.setVisibility(searchHistoryAdapter.getList().size() > 0 ? View.VISIBLE : View.GONE);
        searchHistoryAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view, Object object) {
                String content = (String) object;
                if (!CommonUtil.isBlank(content)) {
                    binding.editText.setText(content);
                    binding.editText.setSelection(content.length());
                    binding.searchView.callOnClick();
                }
            }

            @Override
            public void onLongClick(View view, Object object) {

            }
        });
        binding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    binding.contentRecyclerView.setVisibility(View.GONE);
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

        binding.contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AJJuBenVerticalAdapter juBenVerticalAdapter = new AJJuBenVerticalAdapter(this);
        binding.contentRecyclerView.setAdapter(juBenVerticalAdapter);
        juBenVerticalAdapter.refreshData(CommonUtil.getTitles());
    }

    public void onClickSearch(View view) {
        String content = binding.editText.getText().toString().trim();
        CommonUtil.hideSoftInput(SearchActivity.this, binding.editText);
        if (!TextUtils.isEmpty(content)) {
            DBManager.getInstance().insertSearchContent(0, content);
            searchHistoryAdapter.refreshData(DBManager.getInstance().querySearchHistory(0));
            binding.searchHistoryView.setVisibility(searchHistoryAdapter.getList().size() > 0 ? View.VISIBLE : View.GONE);
            binding.searchHistoryRecyclerView.setVisibility(searchHistoryAdapter.getList().size() > 0 ? View.VISIBLE : View.GONE);

            binding.contentRecyclerView.setVisibility(View.VISIBLE);


        } else {
            binding.contentRecyclerView.setVisibility(View.GONE);
            ToastUtils.showShort(SearchActivity.this, "请输入搜索内容");

        }
    }

    public void onClickClearText(View view) {
        binding.editText.setText("");
    }

    public void onClickBack(View view) {
        finish();
    }

    public void onClickDeleteSearchHistory(View view) {
        DialogManager.getInstance().confirmDialog(SearchActivity.this,
                "删除搜索历史", "确定要删除全部搜索历史记录吗啊？",
                new DialogManager.Listener() {
                    @Override
                    public void onItemLeft() {

                    }

                    @Override
                    public void onItemRight() {
                        DBManager.getInstance().deleteSearchHistory(0);
                        searchHistoryAdapter.refreshData(DBManager.getInstance().querySearchHistory(0));
                        binding.searchHistoryView.setVisibility(searchHistoryAdapter.getList().size() > 0 ? View.VISIBLE : View.GONE);
                        binding.searchHistoryRecyclerView.setVisibility(searchHistoryAdapter.getList().size() > 0 ? View.VISIBLE : View.GONE);
                    }
                });

    }

}