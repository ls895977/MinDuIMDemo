package com.example.minduimdemo.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.css.im_kit.utils.IMDateUtil;
import com.example.minduimdemo.R;
import com.example.minduimdemo.bean.ContextBean;
import java.util.List;

public class TestWebAdapter extends BaseQuickAdapter<ContextBean, BaseViewHolder> {
    private String userToken;

    public TestWebAdapter(List data, String userToken) {
        super(R.layout.item_estdata, data);
        this.userToken = userToken;
    }

    @Override
    protected void convert(BaseViewHolder helper, ContextBean item) {
        helper.setText(R.id.time, IMDateUtil.INSTANCE.getSimpleTime1(item.getTime()));
        if (item.getSend_id()!=null&&item.getSend_id().equals(userToken + "")) {
            helper.setGone(R.id.tvLeftContext, false);
            helper.setGone(R.id.tvRightContext, true);
            helper.setText(R.id.tvRightContext, item.getContent() + " ： " + item.getSend_id());//右
        } else {
            helper.setGone(R.id.tvLeftContext, true);
            helper.setGone(R.id.tvRightContext, false);
            helper.setText(R.id.tvLeftContext, item.getSend_id() + " ： " + item.getContent());//左
        }
    }
}