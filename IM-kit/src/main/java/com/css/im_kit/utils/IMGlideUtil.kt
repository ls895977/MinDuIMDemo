package com.css.im_kit.utils

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.css.im_kit.IMManager
import com.css.im_kit.R
import com.css.im_kit.utils.IMRoundedCornersTransformation

/**
 * Glide 帮助类
 */
object IMGlideUtil {
    //圆形头像图片
    private var avatarOption: RequestOptions? = null

    //4个角为圆角
    private var roundOptions: RequestOptions? = null

    //2个角为圆角
    private var roundOption2: RequestOptions? = null

    //加载圆形头像图片
    fun loadAvatar(context: Context?, url: String?, img: ImageView?) {
        if (url.isNullOrEmpty()) {
            Glide.with(context!!).load(R.mipmap.im_icon_avatar_default).apply(avatarOptions!!).into(img!!)
        } else {
            Glide.with(context!!).load(getAllUrl(url)).apply(avatarOptions!!).into(img!!)
        }
    }

    //加载4个角为radius（单位：dp）的圆角图片
    fun loadRound4Img(context: Context?, url: String?, img: ImageView?, radius: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(context!!).load(R.color.color_f3f3f3).apply(getRounOptions(radius)!!).into(img!!)
        } else {
            Glide.with(context!!).load(getAllUrl(url)).apply(getRounOptions(radius)!!).into(img!!)
        }
    }

    //头部圆角2个角
    fun loadRound2Img(context: Context?, url: String?, img: ImageView?, radius: Int) {
        if (url.isNullOrEmpty()) {
            Glide.with(context!!).load(R.color.color_f3f3f3).apply(getRounOptions2(radius)!!).into(img!!)
        } else {
            Glide.with(context!!).load(getAllUrl(url)).apply(getRounOptions2(radius)!!).into(img!!)
        }
    }

    //加载4个角为radius（单位：dp）的圆角图片
    fun loadRound4Img(context: Context?, bitmap: Bitmap?, img: ImageView?, radius: Int) {
        Glide.with(context!!).load(bitmap).apply(getRounOptions(radius)!!).into(img!!)
    }

    //圆图
    private val avatarOptions: RequestOptions?
        get() {
            if (avatarOption == null) {
                avatarOption = RequestOptions()
                        .placeholder(R.mipmap.im_icon_avatar_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .error(R.mipmap.im_icon_avatar_default)
            }
            return avatarOption
        }

    //4个角为radius（单位：dp）
    fun getRounOptions(radius: Int): RequestOptions? {
        if (roundOptions == null) {
            roundOptions = RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_f3f3f3)
                    .error(R.color.color_f3f3f3)
                    .transform(getRounOption(radius))
        }
        return roundOptions
    }

    //圆角
    fun getRounOption(radius: Int): MultiTransformation<Bitmap> {
        // 圆角图片 new RoundedCornersTransformation 参数为 ：半径 , 外边距 , 圆角方向(ALL,BOTTOM,TOP,RIGHT,LEFT,BOTTOM_LEFT等等)
        val transformation = IMRoundedCornersTransformation(radius, 0, IMRoundedCornersTransformation.CornerType.ALL)
        //组合各种Transformation
        return MultiTransformation(CenterCrop(), transformation)
    }

    //2个角为radius（单位：dp）
    fun getRounOptions2(radius: Int): RequestOptions? {
        if (roundOption2 == null) {
            roundOption2 = RequestOptions()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.color_f3f3f3)
                    .error(R.color.color_f3f3f3)
                    .transform(getRounOption2(radius))
        }
        return roundOption2
    }

    //top圆角
    private fun getRounOption2(radius: Int): MultiTransformation<Bitmap> {
        //顶部左边圆角
        val transformation = IMRoundedCornersTransformation(radius, 0, IMRoundedCornersTransformation.CornerType.TOP_LEFT)
        //顶部右边圆角
        val transformation1 = IMRoundedCornersTransformation(radius, 0, IMRoundedCornersTransformation.CornerType.TOP_RIGHT)
        //组合各种Transformation,
        return MultiTransformation(CenterCrop(), transformation, transformation1)
    }

    fun getAllUrl(url: String): String {
        return if (url.contains("http")) url else IMManager.getImageBaseUrl() + url
    }
}