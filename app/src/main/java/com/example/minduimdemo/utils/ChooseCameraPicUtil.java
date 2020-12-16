package com.example.minduimdemo.utils;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;

/**
 * Created by zb on 2019/12/24.
 * 描述：选择图片或者视频工具类
 */
public class ChooseCameraPicUtil {

    public static void choosePic(Activity activity) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(3)// 最大图片选择数量 int
                .minSelectNum(1)// 最小选择数量 int
                .imageSpanCount(4)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isEnableCrop(false)// 是否裁剪 true or false
                .isCompress(true)// 是否压缩 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }
}
