package com.css.im_kit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment<V extends ViewDataBinding> extends Fragment {
    protected V binding;
    public final String TAG = this.getClass().getSimpleName();

    /*
     * 返回一个需要展示的View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutResource(), null, false);
        return binding.getRoot();
    }


    /*
     * 当Activity初始化之后可以在这里进行一些数据的初始化操作
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListeners();
    }

    public abstract int layoutResource();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListeners();


}
