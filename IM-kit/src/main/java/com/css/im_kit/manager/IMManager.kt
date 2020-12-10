package com.css.im_kit.manager

import android.content.Context
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository

class IMManager {
    companion object {
        fun build(context: Context) {
            IMConversationManager.build(context)
            ConversationRepository.build(context)
            MessageRepository.build(context)
            UserInfoRepository.build(context)
        }
    }
}