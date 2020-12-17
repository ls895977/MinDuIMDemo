package com.css.im_kit.imservice.tool

import java.util.*

/**
 * 定时器
 */
object CycleTimeUtils {
    private var onBackTimers: onBackTimer? = null
    private var timerSecond: Long = 0
    private var timer: Timer? = null
    private var task: TimerTask = object : TimerTask() {
        override fun run() {
            // 在此处添加执行的代码
            onBackTimers?.backRunTimer()
        }
    }

    /**
     * 开启定时器
     * second 定时的时间
     */
    fun startTimer(second: Long, onBackTimers: onBackTimer?) {
        CycleTimeUtils.onBackTimers = onBackTimers
        timer?.cancel()
        timerSecond = second * 1000
        timer = Timer()
        timer?.schedule(task, timerSecond)
    }

    /**
     * 取消定时器
     */
    fun canCelTimer() {
        timer?.cancel()//销毁定时器
    }

    interface onBackTimer {
        fun backRunTimer()
    }
}