package com.css.im_kit.ui.pop

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.TextView
import com.css.im_kit.R
import com.lxj.xpopup.core.CenterPopupView

/**
 * 没有标题，内容颜色可以不同，包含否、是两个按钮
 * noticeContent 所有提示content
 * otherColorCotent 特殊提示content
 */
class DeleteDialog(context: Context, private val noticeContent: String, private val sureString: String, private val cancleString: String, private val callBack: (Boolean) -> Unit) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_pop_notice2
    }

    override fun onCreate() {
        super.onCreate()
        val tcContent = findViewById<View>(R.id.tv_content) as TextView
        tcContent.text = Html.fromHtml(noticeContent)
        (findViewById<View>(R.id.tv_cancel) as TextView).text = cancleString
        (findViewById<View>(R.id.tv_sure) as TextView).text = sureString
        findViewById<View>(R.id.tv_cancel).setOnClickListener { v: View? ->
            dismiss()
        }
        findViewById<View>(R.id.tv_sure).setOnClickListener { v: View? ->
            callBack(true)
            dismiss()
        }
    }
}