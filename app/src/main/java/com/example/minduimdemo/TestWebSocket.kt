package com.example.minduimdemo

import android.util.Log
import com.css.im_kit.socket.SocketService
import com.css.im_kit.socket.SocketService.initSocket
import com.css.im_kit.socket.TestBean
import com.css.im_kit.ui.base.BaseActivity
import com.example.minduimdemo.databinding.TestwebsocketBinding
import com.google.gson.Gson

class TestWebSocket : BaseActivity<TestwebsocketBinding?>() {
    private val Url = "ws://192.168.0.73:9502?token=2"
    override fun layoutResource(): Int {
        return R.layout.testwebsocket
    }

    override fun initView() {
        initSocket(Url)

    }

    override fun initData() {}
    override fun initListeners() {
        binding?.tvContext?.setOnClickListener {
            val context: String
            val testbean = TestBean(binding?.etContext?.text.toString(), "2", "1", "2", "0")
            context = Gson().toJson(testbean)
            Log.e("aa", "--------context==$context")
            SocketService.sendNewMsg(context)
        }
        binding?.tvLianjie?.setOnClickListener {
//            SocketService.connect()

        }
    }
}