package com.ruanmeng.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.lzy.okgo.OkGo;
import com.maning.mndialoglibrary.MToast;
import com.ruanmeng.utils.PreferencesUtils;

import net.idik.lib.slimadapter.SlimAdapter;

public class BaseFragment extends Fragment implements
        TextWatcher,
        View.OnClickListener,
        RadioGroup.OnCheckedChangeListener,
        CompoundButton.OnCheckedChangeListener {

    /**
     * 上下文context
     */
    public Activity baseContext;
    /**
     * RecyclerView数据管理的LayoutManager
     */
    public LinearLayoutManager linearLayoutManager;
    public GridLayoutManager gridLayoutManager;
    public StaggeredGridLayoutManager staggeredGridLayoutManager;
    /**
     * SlimAdapter的adapter
     */
    public SlimAdapter mAdapter;
    /**
     * 分页加载页数
     */
    public int pageNum = 1;
    /**
     * 是否正在上拉加载中
     */
    public boolean isLoadingMore;

    public int mPosition;

    //网络数据请求方法
    public void getData() { }

    public void getData(int pindex) { }

    public void getData(int pindex, boolean isLoading) { }

    public void showToask(String tip) {
        MToast.makeTextShort(baseContext, tip).show();
    }

    public String getString(String key) {
        return PreferencesUtils.getString(getActivity(), key, "");
    }

    public String getString(String key, String defaultValue) {
        return PreferencesUtils.getString(getActivity(), key, defaultValue);
    }

    public void putString(String key, String vaule) {
        PreferencesUtils.putString(getActivity(), key, vaule);
    }

    public boolean getBoolean(String key) {
        return PreferencesUtils.getBoolean(getActivity(), key);
    }

    public void putBoolean(String key, boolean vaule) {
        PreferencesUtils.putBoolean(getActivity(), key, vaule);
    }

    /**
     * 切换Activity
     *
     * @param activity 需要切换到的Activity
     */
    public void startActivity(Class<?> activity) {
        Intent intent = new Intent(getActivity(), activity);
        this.startActivity(intent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        baseContext = getActivity();
    }

    //初始化控件
    public void init_title() { }

    @Override
    public void onDestroy() {
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void onClick(View v) { }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) { }

}
