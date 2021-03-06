package com.css.im_kit.callback

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.css.im_kit.model.conversation.SGConversation

interface SGConversationCallback {
    /**
     * 会话列表监听
     */
    fun onConversationList(sgConversation: List<MultiItemEntity>)
}