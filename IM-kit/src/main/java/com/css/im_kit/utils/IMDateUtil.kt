package com.css.im_kit.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object IMDateUtil {
    private const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    private const val YY_MM_DD_HH_MM0 = "yyyy/MM/dd HH:mm"
    private const val YY_MM_DD_HH_MM1 = "yyyy年MM月dd日 HH:mm"
    private const val HH_MM = "HH:mm"

    fun getSimpleTime0(time: Long): String {
        return when {
            isToday(time) -> format(time, HH_MM)
            isYesterday(time) -> "昨天"
            else -> format(time, YY_MM_DD_HH_MM0)
        }
    }

    fun getSimpleTime1(time: Long): String {
        return when {
            isToday(time) -> format(time, HH_MM)
            isYesterday(time) -> "昨天  ${format(time, HH_MM)}"
            else -> format(time, YY_MM_DD_HH_MM1)
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun format(time: Long): String {
        return SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(Date(time))
    }

    private fun format(time: Long, type: String): String {
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
        return System.currentTimeMillis() / 1000
    }

    /**
     * 是否是今天
     */
    private fun isToday(time: Long): Boolean {
        //当前时间
        val nowT = Calendar.getInstance()
        nowT.time = Date(System.currentTimeMillis())

        //传入时间
        var showT = Calendar.getInstance()
        showT.time = Date(time)

        //判断
        if (showT.get(Calendar.YEAR) == nowT.get(Calendar.YEAR)) {//年份相同
            return (showT.get(Calendar.DAY_OF_YEAR) - nowT.get(Calendar.DAY_OF_YEAR)) == 0
        }
        return false
    }

    /**
     * 是否是昨天
     */
    private fun isYesterday(time: Long): Boolean {
        //当前时间
        val nowT = Calendar.getInstance()
        nowT.time = Date(System.currentTimeMillis())

        //传入时间
        var showT = Calendar.getInstance()
        showT.time = Date(time)

        //判断
        if (showT.get(Calendar.YEAR) == nowT.get(Calendar.YEAR)) {//年份相同
            return (showT.get(Calendar.DAY_OF_YEAR) - nowT.get(Calendar.DAY_OF_YEAR)) == -1
        }
        return false
    }
}