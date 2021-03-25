/*
 * Copyright(c) 2017, 2021,supersg.cn. All Rights Reserved.
 * 版权说明：本软件属于四川神龟科技有限公司所有，在未获得四川神龟科技有限公司正式授权下，
 * 任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何知识产权保护的内容。
 */

package com.css.im_kit.utils

import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import com.css.im_kit.IMManager
import com.css.im_kit.R


/**
 * 描述：mToast 工具类
 */
object ToastUtil {
    private var mToast: Toast? = null
    private var textView: TextView? = null

    fun show(strId: Int) {
        show(IMManager.context?.applicationContext?.getString(strId))
    }

    fun show(msg: String?) {
        IMManager.context?.applicationContext?.let { context ->
            try {
                if (mToast == null) {
                    mToast = Toast(context)
                    textView = TextView(context)
                    textView?.apply {
                        setBackgroundResource(R.drawable.shape_80000000_8)
                        setTextColor(Color.WHITE)
                        textSize = 15f
                        val dp12 = IMDensityUtils.dp2px(context, 12f)
                        text = msg
                        setPadding(dp12, dp12, dp12, dp12)
                    }
                    mToast?.view = textView
                    mToast?.setGravity(Gravity.BOTTOM, 0, IMDensityUtils.dp2px(context, 75f))
                } else {
                    textView?.text = msg
                }
                mToast?.show()
            } catch (e: Exception) {
            }
        }
    }
}