package com.css.im_kit.ui.listener

import com.css.im_kit.model.message.CommodityMessageBody

interface IMListener {
    /**
     * Fragment初始化好了，设置数据
     */
    interface SetDataListener {
        fun onSetFragmentDataListener()
    }

    /**
     * 点击商品messageItem回调
     */
    interface SetClickProductListener {
        fun onGoProductDetail(productMessage: CommodityMessageBody?)
    }
}