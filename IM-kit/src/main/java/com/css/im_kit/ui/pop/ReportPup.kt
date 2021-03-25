package com.css.im_kit.ui.pop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.css.im_kit.R
import com.css.im_kit.utils.toast
import com.lxj.xpopup.animator.PopupAnimator
import com.lxj.xpopup.animator.ScrollScaleAnimator
import com.lxj.xpopup.core.AttachPopupView
import com.lxj.xpopup.enums.PopupAnimation

/**
 * 会话列表
 */
@SuppressLint("ViewConstructor")
class ReportPup(context: Context) : AttachPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.pup_report
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.sticky_text).text = "举报"
        findViewById<View>(R.id.sticky).setOnClickListener {    //举报
            "我们已收到你的举报，感谢反馈".toast()
            dismiss()
        }

    }

    override fun getPopupAnimator(): PopupAnimator? {
        return ScrollScaleAnimator(popupContentView, PopupAnimation.ScaleAlphaFromCenter)
    }
}