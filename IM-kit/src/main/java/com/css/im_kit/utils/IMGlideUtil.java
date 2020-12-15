package com.css.im_kit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.css.im_kit.R;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Glide 帮助类
 */

public class IMGlideUtil {
    //圆形头像图片
    private static RequestOptions avatarOption;
    //4个角为圆角
    private static RequestOptions roundOptions;
    //2个角为圆角
    private static RequestOptions roundOption2;

    //加载圆形头像图片
    public static void loadAvatar(Context context, String url, ImageView img) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context).load(R.mipmap.im_icon_avatar_default).apply(getAvatarOptions()).into(img);
        } else {
            Glide.with(context).load(url).apply(getAvatarOptions()).into(img);
        }
    }

    //加载4个角为radius（单位：dp）的圆角图片
    public static void loadRound4Img(Context context, String url, ImageView img, int radius) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context).load(R.color.color_f3f3f3).apply(getRounOptions(radius)).into(img);
        } else {
            Glide.with(context).load(url).apply(getRounOptions(radius)).into(img);
        }
    }

    //头部圆角2个角
    public static void loadRound2Img(Context context, String url, ImageView img, int radius) {
        if (TextUtils.isEmpty(url)) {
            Glide.with(context).load(R.color.color_f3f3f3).apply(getRounOptions2(radius)).into(img);
        } else {
            Glide.with(context).load(url).apply(getRounOptions2(radius)).into(img);
        }
    }

    //加载4个角为radius（单位：dp）的圆角图片
    public static void loadRound4Img(Context context, Bitmap bitmap, ImageView img, int radius) {
        Glide.with(context).load(bitmap).apply(getRounOptions(radius)).into(img);
    }

    //圆图
    private static RequestOptions getAvatarOptions() {
        if (avatarOption == null) {
            avatarOption = new RequestOptions()
                    .placeholder(R.mipmap.im_icon_avatar_default)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .error(R.mipmap.im_icon_avatar_default);
        }
        return avatarOption;
    }

    //4个角为radius（单位：dp）
    public static RequestOptions getRounOptions(int radius) {
        if (roundOptions == null) {
            roundOptions = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_f3f3f3)
                    .error(R.color.color_f3f3f3)
                    .transform(getRounOption(radius));
        }
        return roundOptions;
    }

    //圆角
    public static MultiTransformation<Bitmap> getRounOption(int radius) {
        // 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
        IMRoundedCornersTransformation transformation = new IMRoundedCornersTransformation(radius, 0, IMRoundedCornersTransformation.CornerType.ALL);
        //组合各种Transformation
        return new MultiTransformation<>(new CenterCrop(), transformation);
    }

    //2个角为radius（单位：dp）
    public static RequestOptions getRounOptions2(int radius) {
        if (roundOption2 == null) {
            roundOption2 = new RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_f3f3f3)
                    .error(R.color.color_f3f3f3)
                    .transform(getRounOption2(radius));
        }
        return roundOption2;
    }

    //top圆角
    private static MultiTransformation<Bitmap> getRounOption2(int radius) {
        //顶部左边圆角
        IMRoundedCornersTransformation transformation = new IMRoundedCornersTransformation
                (radius, 0, IMRoundedCornersTransformation.CornerType.TOP_LEFT);
        //顶部右边圆角
        IMRoundedCornersTransformation transformation1 = new IMRoundedCornersTransformation
                (radius, 0, IMRoundedCornersTransformation.CornerType.TOP_RIGHT);
        //组合各种Transformation,
        return new MultiTransformation<>(new CenterCrop(), transformation, transformation1);
    }
}
