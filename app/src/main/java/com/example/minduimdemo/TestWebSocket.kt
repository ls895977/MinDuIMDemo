package com.example.minduimdemo

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.css.im_kit.imservice.MessageServiceUtils
import com.css.im_kit.imservice.MessageServiceUtils.initService
import com.css.im_kit.imservice.`interface`.onLinkStatus
import com.css.im_kit.imservice.`interface`.onResultMessage
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.utils.IMDateUtil
import com.example.minduimdemo.adapter.TestWebAdapter
import com.example.minduimdemo.bean.ContextBean
import com.example.minduimdemo.bean.TestBean
import com.example.minduimdemo.databinding.TestwebsocketBinding
import com.google.gson.Gson
import java.util.*


class TestWebSocket : BaseActivity<TestwebsocketBinding?>() {
//        private var userToken=2////客服1
    private var userToken = 3////客服2
    private val url = "ws://192.168.0.73:9502?token=$userToken"//客服2
    private var myAdapter: TestWebAdapter? = null
    override fun layoutResource(): Int {
        return R.layout.testwebsocket
    }

    private var myData: MutableList<ContextBean> = ArrayList()
    override fun initView() {
        myAdapter = TestWebAdapter(myData, userToken)
        binding?.myData?.adapter = myAdapter
        initService(url, object : onLinkStatus {
            override fun onLinkedSuccess() {//链接成功
                binding?.tvReLink?.visibility = View.GONE
            }

            override fun onLinkedClose() {//已关闭链接
                binding?.tvReLink?.visibility = View.VISIBLE
            }
        })
    }

    override fun initData() {

    }
    override fun initListeners() {
        binding?.tvContext?.setOnClickListener {
            val context: String
            if (TextUtils.isEmpty(binding?.etContext?.text.toString())) {
                Toast.makeText(this, "请输入发送消息内容！", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //客服1
//            val testbean = TestBean(binding?.etContext?.text.toString(),
//                    "2", "3", "2", "0")
            //客服2
            val testbean = TestBean(binding?.etContext?.text.toString(),
                    "3", "2", "3", "0")
            context = Gson().toJson(testbean)
            MessageServiceUtils.sendNewMsg(context)
            //客服1
//            val contextBean=ContextBean("", "", binding?.etContext?.text.toString(), "3", "2", IMDateUtil.getTime(), "", 2000)
            //客服2
            val contextBean = ContextBean("", "", binding?.etContext?.text.toString(), "2", "3", IMDateUtil.getTime(), "", 2000)
            myData.add(0, contextBean)
            myAdapter?.notifyDataSetChanged()
            binding?.etContext?.setText("")
        }
//        binding?.tvLianjie?.setOnClickListener {
//            MessageServiceUtils.retryService()//重新链接
//        }
        MessageServiceUtils.setOnResultMessage(object : onResultMessage {
            override fun onMessage(context: String) {
                val contextBean = Gson().fromJson(context, ContextBean::class.java)
                Log.e("aa", "----------------消息内容==" + context)
                if (contextBean.send_id != "0") {
                    myData.add(0, contextBean)
                }
                binding?.myData?.postDelayed({
                    myAdapter?.notifyDataSetChanged()
                    binding?.myData?.scrollToPosition(0)
                }, 10)
            }
        })
        binding?.tvReLink?.setOnClickListener {
            MessageServiceUtils.retryService()//重新链接
        }
    }
}


