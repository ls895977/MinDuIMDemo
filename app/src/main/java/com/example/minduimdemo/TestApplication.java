package com.example.minduimdemo;

import android.app.Application;
import android.content.Context;

import androidx.core.content.ContextCompat;

import com.css.im_kit.IMManager;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class TestApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        IMManager.INSTANCE.build(this, "1607505722", "uN29TNVRilVmwqHw7aIMQfDZyw2ltVZO");
        IMManager.INSTANCE.setIMURL("http://devchatapi.supersg.cn", "/chat/listC");
        IMManager.INSTANCE.setQiuNiuTokenUrl("http://devcappapi.supersg.cn/api/customer_app/qiniu/getConfig");
//        IMManager.INSTANCE.setBusiness(true);
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.color_transparent, R.color.color_333333);//全局设置主题颜色
            ClassicsHeader classicsHeader = new ClassicsHeader(context);
            classicsHeader.setAccentColor(ContextCompat.getColor(context, R.color.color_999999));
            classicsHeader.setTextSizeTitle(14.0f);
            classicsHeader.setEnableLastTime(false);
            return classicsHeader;//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter.REFRESH_FOOTER_NOTHING = "到底啦~";
            return new ClassicsFooter(context).setDrawableSize(20)
                    .setAccentColor(ContextCompat.getColor(context, R.color.color_999999))
                    .setTextSizeTitle(12);
        });
    }
}
