package com.css.im_kit.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object IMDateUtil {
    private const val ONE_MINUTE: Long = 60
    private const val ONE_HOUR: Long = 3600
    private const val ONE_DAY: Long = 86400
    private const val ONE_MONTH: Long = 2592000
    private const val ONE_YEAR: Long = 31104000
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val CN_MM_DD_HH_MM = "MM月dd日 HH:mm"
    const val CHN_YYYY_MM_DD = "yyyy年MM月dd日"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val CN_MM_DD = "MM月dd日"
    const val HH_MM = "HH:mm"
    const val HH_MM_SS = "HH:mm:ss"
    fun getSimpleTimeNew(time: Long): String {
        //接收到的时间（单位：秒）
        val oldTime = time.div(1000)
        //当前的时间（单位：秒）
        val nowTime = System.currentTimeMillis().div(1000)
        //时间差（单位：秒）
        val agoTime = nowTime - oldTime
        return when {
            agoTime < 60 -> "刚刚"
            agoTime <= ONE_HOUR -> "${agoTime / ONE_MINUTE}分钟前"
            agoTime <= ONE_DAY -> format(agoTime.times(1000), HH_MM)
            agoTime <= ONE_DAY * 2 -> "昨天"
            agoTime <= ONE_DAY * 3 -> "前天"
            agoTime <= ONE_MONTH -> format(agoTime.times(1000), CN_MM_DD)
            else -> format(agoTime.times(1000), CHN_YYYY_MM_DD)
        }
    }

    fun getSimpleTime(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val time = date.time / 1000
        val now = Date().time / 1000
        val ago = now - time
        return if (ago < 0) {
            "刚刚"
        } else if (ago <= ONE_HOUR) {
            if (ago / ONE_MINUTE == 0L) "刚刚" else "${ago / ONE_MINUTE}分钟前"
        } else if (ago <= ONE_DAY) {
            "${ago / ONE_HOUR}小时前"
        } else if (ago <= ONE_DAY * 2) {
            "昨天"
        } else if (ago <= ONE_DAY * 3) {
            "前天"
        } else if (ago <= ONE_MONTH) {
            "${ago / ONE_DAY}天前"
        } else if (ago <= ONE_YEAR) {
            "${ago / ONE_MONTH}个月前"
        } else {
            "${ago / ONE_YEAR}年前"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun format(time: Long): String {
        return SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(Date(time))
    }

    @SuppressLint("SimpleDateFormat")
    fun format(time: Long, type: String): String {
        return SimpleDateFormat(type).format(Date(time))
    }

    /**
     * 将时间转换为时间戳
     */
    @SuppressLint("SimpleDateFormat")
    fun dateToStamp(s: String?): Long {
        var date: Date? = null
        return try {
            date = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(s)
            date.time
        } catch (e: ParseException) {
            e.printStackTrace()
            -1
        }
    }
}