//package com.css.im_kit.ui.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import androidx.annotation.NonNull;
//
//import com.bingfor.cxs.Config;
//import com.bingfor.cxs.R;
//import com.bingfor.cxs.ui.main.home.bean.CheckCouponsBean;
//import com.bingfor.cxs.utils.GlideUtil;
//import com.bingfor.cxs.utils.MyUtils;
//import com.bingfor.cxs.utils.StringUtil;
//import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
//import com.chad.library.adapter.base.BaseViewHolder;
//
//import java.util.List;
//
//public class ConversationAdapter extends BaseMultiItemQuickAdapter<CheckCouponsBean, BaseViewHolder> {
//    private Context context;
//    private String storeName, storeLogo;
//
//    public ConversationAdapter(Context context, List<CheckCouponsBean> data, String storeName, String storeLogo) {
//        super(data);
//        this.context = context;
//        this.storeName = storeName;
//        this.storeLogo = storeLogo;
//        //优惠券类型  1平台优惠券, 2 店铺优惠券
//        addItemType(Config.COUPON_TYPE1, R.layout.adapter_item_my_coupon1);
//        addItemType(Config.COUPON_TYPE2, R.layout.adapter_item_my_coupon2);
//        if (data == null || data.size() == 0) {
//            View emptyView = LayoutInflater.from(context).inflate(R.layout.empty_layout_coupon, null, false);
//            setEmptyView(emptyView);
//        }
//    }
//
//    @Override
//    protected void convert(@NonNull BaseViewHolder helper, CheckCouponsBean item) {
//        helper.setGone(R.id.btn1, false);
//        helper.setGone(R.id.iv_gz_k, false);
//        switch (item.getItemType()) {
//            case Config.COUPON_TYPE1:// 平台优惠券
//                helper.setText(R.id.title, item.getCouponBean().getCoupon_conditions() + "");
//                helper.setText(R.id.use_content, "全平台可用");
//                break;
//            case Config.COUPON_TYPE2:// 店铺优惠券
//                GlideUtil.loadRound4Img(GlideUtil.getImgPath(storeLogo), helper.getView(R.id.header1), 30);
//                helper.setText(R.id.tv_name, storeName);
//
//                break;
//        }
//        helper.setText(R.id.title, item.getCouponBean().getCoupon_name());
//        helper.setText(R.id.price1, item.getCouponBean().getDiscount_amount() + "");
//        //满价格
//        helper.setText(R.id.price2, "满" + StringUtil.fmt_prt_double(item.getCouponBean().getCoupon_conditions() + "") + "可用");
//        helper.setText(R.id.tv_gz_k, "有效期至" + item.getCouponBean().getValidity_end_time());
//        helper.getView(R.id.tv_gz_k).setPadding(0, 0, 0, MyUtils.dip2px(context, 10));
//        helper.setGone(R.id.select, true);
//        helper.setImageResource(R.id.select, item.isChecked() == true ? R.mipmap.icon_red_yes : R.drawable.shape_circular_frame_cccccc);
//    }
//}
