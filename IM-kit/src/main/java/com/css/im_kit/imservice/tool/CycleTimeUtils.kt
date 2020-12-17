package com.css.im_kit.imservice.tool
import android.os.Handler
/**
 * 定时器
 */
object CycleTimeUtils {
    var autoSaveHandler = Handler()
    private var onBackTimers: onBackTimer? = null
    private val heartBeatRunnable: Runnable = object : Runnable {
        override fun run() {
            onBackTimers?.backRunTimer()
            //每隔一定的时间，对长连接进行一次心跳检测
            autoSaveHandler.postDelayed(this, timerSecond)
        }
    }
    var timerSecond: Long = 0

    /**
     * 开启定时器
     * second 定时的时间
     */
    fun startTimer(second: Long, onBackTimers: onBackTimer?) {
        CycleTimeUtils.onBackTimers = onBackTimers
        timerSecond = second * 1000
        autoSaveHandler.postDelayed(heartBeatRunnable, timerSecond)
    }

    /**
     * 取消定时器
     */
    fun canCelTimer() {
        autoSaveHandler.removeCallbacks(heartBeatRunnable)
    }

    interface onBackTimer {
        fun backRunTimer()
    }
}