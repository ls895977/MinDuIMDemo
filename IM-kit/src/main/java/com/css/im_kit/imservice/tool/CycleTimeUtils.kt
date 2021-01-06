package com.css.im_kit.imservice.tool

import android.os.CountDownTimer
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
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

    private var mTimer: CountDownTimer? = null

    private const val interval = 6000

    /**
     * 开始倒计时
     *
     * @param startTime      开始时间（时间戳）
     * @param minuteInterval 时间间隔（单位：分）
     * @param callBack
     */
    fun start(startTime: Long, minuteInterval: Int, callBack: OnCountDownCallBack?) {
        var startTime = startTime
        val lengthTime = minuteInterval * 60 * interval.toLong()
        //查看是否为毫秒的时间戳
        val isMillSecond = startTime.toString().length == 13
        startTime = startTime * if (isMillSecond) 1 else interval
        val endTime = startTime + lengthTime
        val curTime = System.currentTimeMillis()
        mTimer = getTimer(endTime - curTime, interval.toLong(), callBack)
        if (Math.abs(curTime - startTime) > lengthTime) {
            callBack?.onFinish()
        } else {
            mTimer!!.start()
        }
    }

    private fun getTimer(millisInFuture: Long, interval: Long, callBack: OnCountDownCallBack?): CountDownTimer? {
        return object : CountDownTimer(millisInFuture, interval) {
            override fun onTick(millisUntilFinished: Long) {
                var day = 0
                var hour = 0
                var minute = (millisUntilFinished / interval / 60).toInt()
                val second = (millisUntilFinished / interval % 60).toInt()
                if (minute > 60) {
                    hour = minute / 60
                    minute = minute % 60
                }
                if (hour > 24) {
                    day = hour / 24
                    hour = hour % 24
                }
                callBack?.onProcess(day, hour, minute, second)
            }

            override fun onFinish() {
                callBack?.onFinish()
            }
        }
    }

    /**
     * 开始倒计时
     *
     * @param endTime  结束时间（时间戳）
     * @param callBack
     */
    fun start(endTime: Long, callBack: OnCountDownCallBack?) {
//        long curTime = System.currentTimeMillis();
        val curTime = getTimeStamp().toLong()
        val startTime = endTime - curTime
        Log.e("aa", "//////$endTime-$curTime=$startTime")
        if (startTime < 0) {
            return
        }
        mTimer = getTimer(startTime, interval.toLong(), callBack)
        if (endTime < curTime) {
            callBack?.onFinish()
        } else {
            mTimer!!.start()
        }
    }

    /**
     * second设置循环的次数
     */
    fun startTime(second: Long, callBack: OnCountDownCallBack?) {
        val curTime = Calendar.getInstance().timeInMillis
        start(curTime, curTime + (second * 1000), callBack)
    }

    /**
     * 开始倒计时
     *
     * @param curTime  当前时间（时间戳）
     * @param endTime  结束时间（时间戳）
     * @param callBack
     */
    fun start(curTime: Long, endTime: Long, callBack: OnCountDownCallBack?) {
        mTimer = getTimer(endTime - curTime, interval.toLong(), callBack)
        if (endTime < curTime) {
            callBack?.onFinish()
        } else {
            mTimer!!.start()
        }
    }

    /**
     * 必用
     */
    fun onDestroy() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    /**
     * 自定义倒计时
     * seconds 秒
     */
    fun onStartTime(seconds: Int, callBack: OnCountDownCallBack?) {
        val endTime = System.currentTimeMillis() + seconds * 1000
        val curTime = System.currentTimeMillis()
        if (endTime - curTime < 0) {
            return
        }
        mTimer = getTimer(endTime - curTime, interval.toLong(), callBack)
        if (endTime < curTime) {
            callBack?.onFinish()
        } else {
            mTimer!!.start()
        }
    }

    /**
     * 自定义倒计时
     * seconds 毫秒
     */
    fun onStartTimeSeconds(seconds: Long, callBack: OnCountDownCallBack?) {
        val endTime = System.currentTimeMillis() + (seconds*6000)
        val curTime = System.currentTimeMillis()
        if (endTime - curTime < 0) {
            return
        }
        mTimer = getTimer(endTime - curTime, interval.toLong(), callBack)
        if (endTime < curTime) {
            callBack?.onFinish()
        } else {
            mTimer!!.start()
        }
    }

    interface OnCountDownCallBack {
        fun onProcess(day: Int, hour: Int, minute: Int, second: Int)
        fun onFinish()
    }

    fun startStEd(startTime: String?, endTime: String?, callBack: OnCountDownCallBack?) {
        startSt(startTime, endTime, callBack)
    }

    fun startSt(endTime: String?, callBack: OnCountDownCallBack?) {
        startSt("", endTime, callBack)
    }

    /**
     * 开始倒计时
     * @param endTime  结束时间（时间戳）
     * @param startTime  开始时间（时间戳）
     * @param callBack
     */
    fun startSt(startTime: String?, endTime: String?, callBack: OnCountDownCallBack?) {
        val end: Long = TimeUtils.backTimeLong(endTime)
        val cur = if (TextUtil.isNullOrEmpty(startTime)) System.currentTimeMillis() else TimeUtils.backTimeLong(startTime)
        mTimer = getTimer(end - cur, interval.toLong(), callBack)
        if (end < cur) {
            callBack?.onFinish()
        } else {
            mTimer!!.start()
        }
    }

    /**
     * 直接获取时间戳
     */
    fun getTimeStamp(): String {
        val currentDate = getCurrentDate()
        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = Date()
        try {
            date = sf.parse(currentDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date.time.toString()
    }

    /**
     * 获取系统时间
     */
    fun getCurrentDate(): String {
        val d = Date()
        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sf.format(d)
    }
}