package com.css.im_kit.ui.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.css.im_kit.R
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.TextMessageBody
import com.css.im_kit.utils.IMDateUtil
import com.css.im_kit.utils.IMGlideUtil

class ConversationListAdapter(var context: Context, data: List<SGConversation>) : BaseQuickAdapter<SGConversation, BaseViewHolder>(R.layout.adapter_conversation_list_item, data) {
    override fun convert(helper: BaseViewHolder, item: SGConversation) {
        //头像
        IMGlideUtil.loadAvatar(context, item.userInfo?.avatar, helper.getView(R.id.user_avatar))

        //用户名
        helper.setText(R.id.user_name, item.userInfo?.userName)

        //时间
        helper.setGone(R.id.time, !item.newMessage?.messageBody?.receivedTime.isNullOrEmpty())
        item.newMessage?.messageBody?.receivedTime?.let {
            helper.setText(R.id.time, IMDateUtil.getSimpleTimeNew(it.toLong()))
        }

        //最后一条消息（如果是txt,就展示消息内容，反之，类型）
        when (item.newMessage?.type) {
            MessageType.TEXT -> {
                helper.setText(R.id.message_content, (item.newMessage?.messageBody as TextMessageBody).text)
            }
            MessageType.IMAGE -> {
                helper.setText(R.id.message_content, "[图片]")
            }
            MessageType.COMMODITY -> {
                helper.setText(R.id.message_content, "[商品消息]")
            }
        }

        //未读消息条数
        helper.setText(R.id.message_count, item.newsNum.toString())
        helper.setGone(R.id.message_count, item.newsNum > 0)
    }
}