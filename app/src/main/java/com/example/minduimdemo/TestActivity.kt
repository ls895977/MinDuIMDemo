package com.example.minduimdemo

import android.util.Log
import android.widget.Toast
import com.css.im_kit.IMManager
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.db.uiScope
import com.css.im_kit.imservice.`interface`.onLinkStatus
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.ui.base.BaseActivity
import com.example.minduimdemo.databinding.ActivityDbtestBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TestActivity : BaseActivity<ActivityDbtestBinding>(), SGConversationCallback {
    override fun layoutResource(): Int = R.layout.activity_dbtest
    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListeners() {
        IMConversationManager.addSGConversationListListener(this)
        binding?.addUser?.setOnClickListener {
            ioScope.launch {
                val list = arrayListOf<UserInfo>()
                val userInfo4 = UserInfo("183ff3fd37", "2", "崔勇", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
                val userInfo5 = UserInfo("8116f90a21", "1", "夏鹏", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608121391539&di=24b6d407f954abdc1fc874a189f1bfcb&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201510%2F01%2F20151001174653_L3wEF.jpeg")
                val userInfo = UserInfo("100001", "2", "昵称1", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
                val userInfo1 = UserInfo("100002", "2", "昵称2", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
                val userInfo2 = UserInfo("100003", "2", "昵称3", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
                val userInfo3 = UserInfo("100004", "2", "昵称4", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
                list.add(userInfo)
                list.add(userInfo1)
                list.add(userInfo2)
                list.add(userInfo3)
                list.add(userInfo4)
                list.add(userInfo5)
                UserInfoRepository.insertDatas(list)
                val stak = async {
                    UserInfoRepository.getAll()
                }
                val result = stak.await()
                result?.let {
                    binding?.addUser?.text = "添加用户" + it.size
                }
            }
        }

        binding?.getSGConversation?.setOnClickListener {
            IMConversationManager.getConversationList()
        }
        var inChat = false
        binding?.joinChat?.setOnClickListener {
            if (inChat) {
                IMChatRoomManager.dismissSgMessageCallback()
                Toast.makeText(this, "退出会话", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "进入会话", Toast.LENGTH_SHORT).show()
                IMChatRoomManager
                        .initConversation(SGConversation())
                        .addSGConversationListListener(object : ChatRoomCallback {
                            /**
                             * 新消息
                             */
                            override fun onReceiveMessage(message: SGMessage) {
                                Log.e("111", "1111")
                            }

                            /**
                             * 会话所有消息
                             */
                            override fun onMessages(message: List<SGMessage>) {
                                Log.e("111", "1111")
                            }

                            /**
                             * 消息发送成功回调
                             */
                            override fun onMessageInProgress(message: SGMessage) {
                                Log.e("111", "1111")
                            }

                        })
                        .create()


            }
            inChat = !inChat
        }

        /**
         * 接收新消息
         *
        var conversationId: String,//聊天室id
        var messageId: String,//消息id
        var sendTime: String,//发送时间
        var receivedTime: String,//接收时间
        var content: String,//内容
        var sendUserId: String,//发送方id
        var receiveUserId: String,//接收方id
        var type: String
         */
        var rewStaue: MessageType = MessageType.TEXT
        binding?.rewNewMessage?.setOnClickListener {

        }
        /**
         * 发送新消息
         */
        binding?.sendNewMessage?.setOnClickListener {
            when (rewStaue) {
                MessageType.TEXT -> {
                    rewStaue = MessageType.IMAGE
                    IMChatRoomManager.sendTextMessage("发送一条消息")
                }
                MessageType.IMAGE -> {
                    rewStaue = MessageType.COMMODITY
                    IMChatRoomManager.sendImageMessage("http://testimg.supersg.cn/user/773870855045251072.jpeg")
                }
                MessageType.COMMODITY -> {
                    rewStaue = MessageType.TEXT
                    val commodityMessage = CommodityMessage("commodityId", "测试商品敖德萨大所大所大所大所多撒大大大", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "12.00")//2020-11-08 16:14:23
                    IMChatRoomManager.sendCommodityMessage(commodityMessage)
                }
            }
        }
        binding?.initSocket?.setOnClickListener {
            val url = "ws://192.168.0.73:9502"
            val token = "8116f90a21"
            val userId = "8116f90a21"
            IMManager.connect(url, token, userId, object : onLinkStatus {
                override fun onLinkedSuccess() {
                    uiScope.launch {
                        Toast.makeText(this@TestActivity, "连接socket成功", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLinkedClose() {
                    uiScope.launch {
                        Toast.makeText(this@TestActivity, "连接socket失败", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onConversationList(sgConversation: List<SGConversation>) {
        Log.e("111", sgConversation.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        IMChatRoomManager.dismissSgMessageCallback()
        IMConversationManager.removeSGConversationListListener(this)
    }
}