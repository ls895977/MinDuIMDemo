package com.css.im_kit.ui.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.css.im_kit.R
import com.css.im_kit.ui.bean.EmojiBean

class EmojiAdapter(var context: Context, data: List<EmojiBean>) : BaseQuickAdapter<EmojiBean, BaseViewHolder>(R.layout.adapter_message_emoji_item, data) {
    override fun convert(helper: BaseViewHolder, item: EmojiBean) {
        item.id?.let { helper.setImageResource(R.id.item_view, it) }
        helper.addOnClickListener(R.id.item_view)
    }
}