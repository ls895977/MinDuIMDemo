package com.css.im_kit.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 * 常用单位转换的辅助类
 */
class IMDensityUtils private constructor() {
    companion object {
        /**
         * dp转px
         *
         * @param context
         * @param dpVal
         * @return
         */
        fun dp2px(context: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dpVal, context.resources.displayMetrics).toInt()
        }

        /**
         * sp转px
         *
         * @param context
         * @param spVal
         * @return
         */
        fun sp2px(context: Context, spVal: Float): Int {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    spVal, context.resources.displayMetrics).toInt()
        }

        /**
         * px转dp
         *
         * @param context
         * @param pxVal
         * @return
         */
        fun px2dp(context: Context, pxVal: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxVal / scale
        }

        /**
         * px转sp
         *
         * @param context
         * @param pxVal
         * @return
         */
        fun px2sp(context: Context, pxVal: Float): Float {
            return pxVal / context.resources.displayMetrics.scaledDensity
        }

        /**
         * 获取view高度
         */
        fun getViewHeight(v: View): Int {
            val w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED)
            val h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED)
            v.measure(w, h)
            val height = v.measuredHeight
            val width = v.measuredWidth
            return height
        }

        /**
         * 获取屏幕宽度
         *
         * @param context
         * @return
         */
        fun getScreenWidth(context: Context): Float {
            return getDisplayMetrics(context).widthPixels.toFloat()
        }

        /**
         * 获取屏幕高度
         *
         * @param context
         * @return
         */
        fun getScreenHeight(context: Context): Float {
            return getDisplayMetrics(context).heightPixels.toFloat()
        }

        /**
         * 获取屏幕密度
         *
         * @param context
         * @return
         */
        fun getDensity(context: Context): Float {
            return getDisplayMetrics(context).density
        }

        fun getDisplayMetrics(context: Context): DisplayMetrics {
            val displaymetrics = DisplayMetrics()
            (context.getSystemService(
                    Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
                    displaymetrics)
            return displaymetrics
        }

        fun timedate(time: Int): String {
            val sdr = SimpleDateFormat("yyyy.MM.dd")
            return sdr.format(Date(time * 1000L))
        }

        /**
         * 提供精确的小数位四舍五入处理
         *
         * @param v     需要四舍五入的数字
         * @param scale 小数点后保留几位
         * @return 四舍五入后的结果
         */
        fun round(v: String?, scale: Int): String {
            require(scale >= 0) { "The scale must be a positive integer or zero" }
            val b = BigDecimal(v)
            return b.setScale(scale, BigDecimal.ROUND_HALF_UP).toString()
        }
    }

    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }
}