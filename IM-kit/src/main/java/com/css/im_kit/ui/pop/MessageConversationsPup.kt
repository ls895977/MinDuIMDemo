package com.css.im_kit.ui.pop

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import com.css.im_kit.R
import com.lxj.xpopup.animator.PopupAnimator
import com.lxj.xpopup.animator.ScrollScaleAnimator
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.enums.PopupAnimation

/**
 * 会话列表
 */
@SuppressLint("ViewConstructor")
class MessageConversationsPup(context: Context, private val isSticky: Boolean = false, val position: Int, private val clickListener: MessageConversationsCallBack) : AttachPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.pup_message_conversation
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.sticky_text).text = if (isSticky) "置顶" else "取消置顶"
        findViewById<View>(R.id.sticky).setOnClickListener {    //置顶/取消置顶
            clickListener.sticky(position, isSticky)
            dismiss()
        }
        findViewById<View>(R.id.delete).setOnClickListener {    //删除
            clickListener.delete(position)
            dismiss()
        }
    }

    override fun getPopupAnimator(): PopupAnimator? {
        return ScrollScaleAnimator(popupContentView,  PopupAnimation.ScaleAlphaFromCenter)
    }
}

interface MessageConversationsCallBack {
    fun delete(position: Int)
    fun sticky(position: Int, isSticky: Boolean)
}