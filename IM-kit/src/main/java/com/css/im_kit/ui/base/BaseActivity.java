package com.css.im_kit.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.css.im_kit.R;
import com.gyf.immersionbar.ImmersionBar;

public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {
    public V binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, layoutResource());
        ImmersionBar.with(this).statusBarDarkFont(statusBarDark()).keyboardEnable(true).navigationBarColor(R.color.white).init();
        initView();
        initData();
        initListeners();
    }

    public abstract int layoutResource();

    //状态栏字体深色或亮色
    protected boolean statusBarDark() {
        return true;
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initListeners();
}
