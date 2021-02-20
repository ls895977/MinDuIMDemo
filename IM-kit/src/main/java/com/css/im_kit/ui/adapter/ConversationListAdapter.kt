package com.css.im_kit.ui.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.IMManager
import com.css.im_kit.R
import com.css.im_kit.db.gson
import com.css.im_kit.http.bean.SysBeanBack
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.TextMessageBody
import com.css.im_kit.utils.FaceTextUtil
import com.css.im_kit.utils.IMDateUtil
import com.css.im_kit.utils.IMGlideUtil
import com.css.im_kit.utils.long13

class ConversationListAdapter(var context: Context) : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(null) {
    init {
        addItemType(1, R.layout.adapter_conversation_list_item)
        addItemType(2, R.layout.adapter_conversation_list_item)
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity) {
        when (item.itemType) {
            1 -> {
                message(helper, item)
            }
            2 -> {
                sysMessage(helper, item)
            }
        }
    }

    private fun sysMessage(helper: BaseViewHolder, item: MultiItemEntity) {
        item as SysBeanBack
        //头像

        //用户名
        when (item.sys_type) {//14订单消息11系统消息12互动消息
            14 -> {
                helper.getView<ImageView>(R.id.user_avatar).setImageResource(R.drawable.ic_message_order)
                helper.setText(R.id.user_name, "订单消息")
            }
            11 -> {
                helper.getView<ImageView>(R.id.user_avatar).setImageResource(R.drawable.ic_message_system)
                helper.setText(R.id.user_name, "系统消息")
            }
            12 -> {
                helper.getView<ImageView>(R.id.user_avatar).setImageResource(R.drawable.ic_message_hudong)
                helper.setText(R.id.user_name, "互动")
            }
        }
        //时间
        helper.setGone(R.id.time, item.created_time.isNotEmpty())
        item.created_time.let {
            val time = IMDateUtil.dateToStamp(it).long13()
            try {
                helper.setText(R.id.time, IMDateUtil.getSimpleTime0(time))
            } catch (e: Exception) {
                helper.setText(R.id.time, IMDateUtil.getSimpleTime0(IMDateUtil.dateToStamp(time.toString())))
            }
        }
        //未读消息条数
        helper.setText(R.id.message_count, if (item.unread_number > 99) "99+" else item.unread_number.toString())
        helper.setGone(R.id.message_count, item.unread_number > 0)
        if (item.content.isEmpty()) {
            helper.setText(R.id.message_content, "没有消息")
        } else {
            val json = gson.fromJson(item.content, HashMap::class.java)
            helper.setText(R.id.message_content, json["title"]?.toString() ?: "")
        }
    }

    private fun message(helper: BaseViewHolder, item: MultiItemEntity) {
        item as SGConversation
        helper.getView<RelativeLayout>(R.id.item_view).setBackgroundResource(if (item.sort == 0) R.color.white else R.color.color_f6f6f6)
        if (IMManager.isBusiness) {
            //头像
            IMGlideUtil.loadAvatar(context, item.chat_account_info?.avatar, helper.getView(R.id.user_avatar))
            //用户名
            helper.setText(R.id.user_name, item.chat_account_info?.nickname)
        } else {
            //头像
            IMGlideUtil.loadAvatar(context, item.shop?.log, helper.getView(R.id.user_avatar))
            //用户名
            helper.setText(R.id.user_name, item.shop?.shop_name)
        }


        //时间
        helper.setGone(R.id.time, !item.newMessage?.messageBody?.receivedTime.isNullOrEmpty())
        item.newMessage?.messageBody?.receivedTime?.let {
            val time = it.toLong().long13()
            try {
                helper.setText(R.id.time, IMDateUtil.getSimpleTime0(time))
            } catch (e: Exception) {
                helper.setText(R.id.time, IMDateUtil.getSimpleTime0(IMDateUtil.dateToStamp(time.toString())))
            }

        }

        //最后一条消息（如果是txt,就展示消息内容，反之，类型）
        when (item.newMessage?.type) {
            MessageType.TEXT -> {
                helper.setText(R.id.message_content, (item.newMessage?.messageBody as TextMessageBody).text?.let { FaceTextUtil.toSpannableStringList(context, it) })
            }
            MessageType.IMAGE -> {
                helper.setText(R.id.message_content, "[图片]")
            }
            MessageType.COMMODITY -> {
                helper.setText(R.id.message_content, "[商品消息]")
            }
        }

        //未读消息条数
        helper.setText(R.id.message_count, if (item.unread_account > 99) "99+" else item.unread_account.toString())
        helper.setGone(R.id.message_count, item.unread_account > 0)

        //点击事件
        helper.addOnClickListener(R.id.item_view)
        helper.addOnLongClickListener(R.id.item_view)
    }
}