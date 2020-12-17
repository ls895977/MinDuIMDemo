package com.css.im_kit.ui.listener;

import com.css.im_kit.model.message.CommodityMessageBody;

public interface IMListener {
    /**
     * Fragment初始化好了，设置数据
     */
    interface SetDataListener {
        void onSetFragmentDataListener();
    }

    /**
     * 点击商品messageItem回调
     */
    interface SetClickProductListener {
        void onGoProductDetail(CommodityMessageBody productMessage);
    }
}
