package com.css.im_kit.imservice.tool

import android.util.Log
import java.util.*

/**
 * 定时器
 */
object CycleTimeUtils {
    private var onBackTimers: onBackTimer? = null
    private var timerSecond: Long = 0
    var timer: Timer? = null
    var task: TimerTask? = null
    /**
     * 开启定时器
     * second 定时的时间
     */
    fun startTimer(second: Long, onBackTimers: onBackTimer?) {
        CycleTimeUtils.onBackTimers = onBackTimers
        timerSecond = second * 1000
        task = object : TimerTask() {
            override fun run() {
                // 在此处添加执行的代码
                CycleTimeUtils.onBackTimers?.backRunTimer()
            }
        }
        if (timer == null) {
            timer = Timer()
            timer?.schedule(task, 0, timerSecond)
        }
    }
    /**
     * 取消定时器
     */
    fun canCelTimer() {
        task?.cancel()
        timer?.cancel()//销毁定时器
        timer = null
        task = null
    }

    interface onBackTimer {
        fun backRunTimer()
    }
}