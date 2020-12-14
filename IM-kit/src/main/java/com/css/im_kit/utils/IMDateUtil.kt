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
    const val YY_MM_DD_HH_MM = "yyyy年MM月dd日 HH:mm"
    const val YY_MM_DD_HH_MM1 = "yyyy/MM/dd HH:mm"
    const val CHN_YYYY_MM_DD = "yyyy年MM月dd日"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val CN_MM_DD = "MM月dd日"
    const val HH_MM = "HH:mm"
    const val HH_MM_SS = "HH:mm:ss"

    fun getSimpleTime0(time: Long): String {
        //接收到的时间（单位：秒）
        val oldTime = time.div(1000)
        //当前的时间（单位：秒）
        val nowTime = System.currentTimeMillis().div(1000)
        //时间差（单位：秒）
        val agoTime = nowTime - oldTime
        return when {
            agoTime <= ONE_DAY -> format(oldTime.times(1000), HH_MM)
            agoTime <= ONE_DAY * 2 -> "昨天"
            else -> format(oldTime.times(1000), YY_MM_DD_HH_MM1)
        }
    }

    fun getSimpleTime1(time: Long): String {
        //接收到的时间（单位：秒）
        val oldTime = time.div(1000)
        //当前的时间（单位：秒）
        val nowTime = System.currentTimeMillis().div(1000)
        //时间差（单位：秒）
        val agoTime = nowTime - oldTime
        return when {
            agoTime <= ONE_DAY -> format(oldTime.times(1000), HH_MM)
            agoTime <= ONE_DAY * 2 -> "昨天  ${format(oldTime.times(1000), HH_MM)}"
            else -> format(oldTime.times(1000), YY_MM_DD_HH_MM)
        }
    }

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
            agoTime <= ONE_DAY -> format(oldTime.times(1000), HH_MM)
            agoTime <= ONE_DAY * 2 -> "昨天"
            agoTime <= ONE_DAY * 3 -> "前天"
            agoTime <= ONE_MONTH -> format(oldTime.times(1000), CN_MM_DD)
            else -> format(oldTime.times(1000), CHN_YYYY_MM_DD)
        }
    }

    fun getSimpleTime(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val time = date.time / 1000
        val now = System.currentTimeMillis() / 1000
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
        return SimpleDateFormat(type, Locale.getDefault()).format(Date(time))
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
    /**
     * 获取当前系统时间戳
     */
    fun getTime(): Long? {
        val time: Long = System.currentTimeMillis() / 1000 //获取系统时间的10位的时间戳
        return time.toLong()
    }
}