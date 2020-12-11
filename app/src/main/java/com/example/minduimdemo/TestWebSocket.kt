package com.example.minduimdemo

import android.util.Log
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.MessageServiceUtils.initService
import com.example.minduimdemo.bean.TestBean
import com.css.im_kit.imservice.`interface`.onLinkStatus
import com.css.im_kit.imservice.`interface`.onResultMessage
import com.css.im_kit.ui.base.BaseActivity
import com.example.minduimdemo.databinding.TestwebsocketBinding
import com.google.gson.Gson

class TestWebSocket : BaseActivity<TestwebsocketBinding?>() {
    private val url = "ws://192.168.0.73:9502?token=2"
    override fun layoutResource(): Int {
        return R.layout.testwebsocket
    }

    override fun initView() {
        initService(url,object : onLinkStatus {
            override fun onLinkedSuccess() {
                Log.e("aa", "----------------链接成功")
            }

            override fun onLinkedClose() {
                Log.e("aa", "----------------已关闭链接")
            }
        })
    }

    override fun initData() {}
    override fun initListeners() {
        binding?.tvContext?.setOnClickListener {
            val context: String
            val testbean = TestBean(binding?.etContext?.text.toString(), "2", "1", "2", "0")
            context = Gson().toJson(testbean)
            Log.e("aa", "--------context==$context")
            MessageServiceUtils.sendNewMsg(context)
        }
        binding?.tvLianjie?.setOnClickListener {
            MessageServiceUtils.retryService()
        }
        MessageServiceUtils.setOnResultMessage(object : onResultMessage {
            override fun onMessage(context: String) {
                Log.e("aa", "----------------消息内容==" + context)
            }
        })
    }
}