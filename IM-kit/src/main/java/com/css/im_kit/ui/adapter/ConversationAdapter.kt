package com.css.im_kit.ui.adapter

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.css.im_kit.R
import com.css.im_kit.db.uiScope
import com.css.im_kit.model.message.CommodityMessageBody
import com.css.im_kit.model.message.ImageMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.model.message.TextMessageBody
import com.css.im_kit.utils.IMDateUtil
import com.css.im_kit.utils.IMDensityUtils
import com.css.im_kit.utils.IMGlideUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ConversationAdapter(private var activity: Activity, data: ArrayList<SGMessage>) : BaseMultiItemQuickAdapter<SGMessage, BaseViewHolder>(data) {
    init {
        //消息type（文本、图片、商品）我发送的1/2/3；别人的4/5/6。
        addItemType(1, R.layout.adapter_message_send_txt)
        addItemType(2, R.layout.adapter_message_send_img)
        addItemType(3, R.layout.adapter_message_send_product)
        addItemType(4, R.layout.adapter_message_receiver_txt)
        addItemType(5, R.layout.adapter_message_receiver_img)
        addItemType(6, R.layout.adapter_message_receiver_product)
        addItemType(7, R.layout.adapter_message_send_local_product)
    }

    override fun convert(helper: BaseViewHolder, item: SGMessage) {
        //时间
        helper.setGone(R.id.tv_time, false)
        val time = if (item.messageBody?.isSelf!!) item.messageBody?.sendTime?.toLong() else item.messageBody?.receivedTime?.toLong()
        time?.let {
            helper.setText(R.id.tv_time, IMDateUtil.getSimpleTime1(time))
            if (data.size == 1) {
                helper.setGone(R.id.tv_time, true)
            } else {
                val timeNext = if (data[helper.adapterPosition - 1].messageBody?.isSelf!!) data[helper.adapterPosition].messageBody?.sendTime?.toLong() else data[helper.adapterPosition - 1].messageBody?.receivedTime?.toLong()
                timeNext?.let {
                    if ((time - timeNext) > 5 * 60 * 1000) {//大于5分钟显示时间
                        helper.setGone(R.id.tv_time, true)
                    }
                }
            }
        }

        //头像
        IMGlideUtil.loadAvatar(activity, item.userInfo?.avatar, helper.getView(R.id.user_avatar))

        when (item.itemType) {
            //send
            1 -> {//txt
                helper.setText(R.id.tv_content, (item.messageBody as TextMessageBody).text)
            }
            2 -> {//img
                (item.messageBody as ImageMessageBody).imageUrl?.let { setImageMessage(activity, it, helper.getView(R.id.iv_content)) }
            }
            3 -> {//product
                val message = item.messageBody as CommodityMessageBody
                helper.setText(R.id.tv_product_price, "￥${message.commodityPrice}")
                helper.setText(R.id.tv_product_name, message.commodityName)
                IMGlideUtil.loadRoundTop2Img(activity, message.commodityPrice, helper.getView(R.id.iv_product_image), IMDensityUtils.dp2px(activity, 8f))
                //点击事件
                helper.addOnClickListener(R.id.ll_content)
            }

            //receiver
            4 -> {//txt
                helper.setText(R.id.tv_content, (item.messageBody as TextMessageBody).text)
            }
            5 -> {//img
                (item.messageBody as ImageMessageBody).imageUrl?.let { setImageMessage(activity, it, helper.getView(R.id.iv_content)) }
            }
            6 -> {//product
                val message = item.messageBody as CommodityMessageBody
                helper.setText(R.id.tv_product_price, "￥${message.commodityPrice}")
                helper.setText(R.id.tv_product_name, message.commodityName)
                IMGlideUtil.loadRoundTop2Img(activity, message.commodityPrice, helper.getView(R.id.iv_product_image), IMDensityUtils.dp2px(activity, 8f))
                //点击事件
                helper.addOnClickListener(R.id.ll_content)
            }

            //本地消息（发送在本地的）
            7 -> {//product
//                helper.getView<ImageView>(R.id.iv_product_image)
//                helper.getView<TextView>(R.id.tv_product_name)
//                helper.getView<TextView>(R.id.tv_product_price)
//                helper.getView<ImageView>(R.id.iv_close)
//                helper.getView<TextView>(R.id.tv_send)
            }
        }
    }

    /**
     * 设置图片消息（200,200）
     */
    private fun setImageMessage(context: Context, url: String, img: ImageView) {
        uiScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    Glide.with(context).asBitmap().load(url).submit().get()
                } catch (e: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                if (bitmap.width > IMDensityUtils.dp2px(context, 200f) || bitmap.height > IMDensityUtils.dp2px(context, 200f)) {
                    if (bitmap.width >= bitmap.height) {
                        img.layoutParams = LinearLayout.LayoutParams(IMDensityUtils.dp2px(context, 200f), IMDensityUtils.dp2px(context, 200f) * bitmap.height / bitmap.width)
                    } else {
                        img.layoutParams = LinearLayout.LayoutParams(IMDensityUtils.dp2px(context, 200f) * bitmap.width / bitmap.height, IMDensityUtils.dp2px(context, 200f))
                    }
                }
                IMGlideUtil.loadRound4Img(context, bitmap, img, IMDensityUtils.dp2px(context, 8f))
            } else {
                IMGlideUtil.loadRound4Img(context, url, img, IMDensityUtils.dp2px(context, 8f))
            }

        }

    }
}