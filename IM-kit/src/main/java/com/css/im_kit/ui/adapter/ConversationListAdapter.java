package com.css.im_kit.ui.adapter;

import android.content.Context;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.css.im_kit.R;
import com.css.im_kit.ui.bean.MessageBean;

import java.util.List;

public class ConversationListAdapter extends BaseQuickAdapter<MessageBean, BaseViewHolder> {

    public ConversationListAdapter(List<MessageBean> data, Context context) {
        super(R.layout.adapter_conversation_list_item, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, MessageBean item) {
    }
}
