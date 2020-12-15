package com.example.minduimdemo

import android.util.Log
import android.widget.Toast
import com.css.im_kit.IMManager
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.*
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.imservice.`interface`.onLinkStatus
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.manager.IMMessageManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.MessageType
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.ui.base.BaseActivity
import com.example.minduimdemo.databinding.ActivityDbtestBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
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
                val userInfo4 = UserInfo("本人", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "111111")
                val userInfo = UserInfo("昵称1", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100001")
                val userInfo1 = UserInfo("昵称2", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100002")
                val userInfo2 = UserInfo("昵称3", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100003")
                val userInfo3 = UserInfo("昵称4", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100004")
                list.add(userInfo)
                list.add(userInfo1)
                list.add(userInfo2)
                list.add(userInfo3)
                list.add(userInfo4)
                UserInfoRepository.insertDatas(list)
                val stak = async {
                    UserInfoRepository.getAll()
                }
                val result = stak.await()
                result.collect {
                    binding?.addUser?.text = "添加用户" + it.size
                }
            }
        }
        binding?.addMessage?.setOnClickListener {
            ioScope.launch {
                val list = arrayListOf<Message>()
                val commodityMessage = CommodityMessage("commodityId", "commodityName", "commodityImage", "commodityPrice")//2020-11-08 16:14:23
                val message1 = Message("111", "111", 1607415263000, 1607415263000, "1111", "100001", "111111", MessageType.TEXT.str, SendType.SUCCESS.text, true)
                val message2 = Message("222", "222", 1607415263000, 1607415263000, "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100002", "111111", MessageType.IMAGE.str, SendType.SUCCESS.text, true)
                val message3 = Message("333", "333", 1607415263000, 1607415263000, commodityMessage.toJsonString(), "100003", "111111", MessageType.COMMODITY.str, SendType.SUCCESS.text, true)
                val message4 = Message("111", "444", 1607415263000, 1607415263000, "1111", "111111", "100001", MessageType.TEXT.str, SendType.SUCCESS.text, true)
                val message5 = Message("222", "555", 1607415263000, 1607415263000, "http://testimg.supersg.cn/user/773870855045251072.jpeg", "111111", "100002", MessageType.IMAGE.str, SendType.SUCCESS.text, true)
                val message6 = Message("333", "666", 1607415263000, 1607415263000, commodityMessage.toJsonString(), "111111", "100003", MessageType.COMMODITY.str, SendType.SUCCESS.text, true)
                val message7 = Message("111", "777", 1607415263000, 1607415263000, "1111", "100004", "111111", MessageType.TEXT.str, SendType.SUCCESS.text, true)
                list.add(message1)
                list.add(message2)
                list.add(message3)
                list.add(message4)
                list.add(message5)
                list.add(message6)
                list.add(message7)
                MessageRepository.insertDatas(list)
            }
        }

        binding?.addConversation?.setOnClickListener {
            ioScope.launch {
                val list = arrayListOf<Conversation>()
                val conversation1 = Conversation("111", "100001", "111111", System.currentTimeMillis().toString())
                val conversation2 = Conversation("222", "100002", "111111", System.currentTimeMillis().toString())
                val conversation3 = Conversation("333", "100003", "111111", System.currentTimeMillis().toString())
                val conversation4 = Conversation("445", "100004", "111111", System.currentTimeMillis().toString())
                list.add(conversation1)
                list.add(conversation2)
                list.add(conversation3)
                list.add(conversation4)
                IMConversationManager.insertOrUpdateConversations(list)
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
                        .initConversation("111")
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
            val message = when (rewStaue) {
                MessageType.TEXT -> {
                    rewStaue = MessageType.IMAGE
                    Message(IMChatRoomManager.conversationId ?: "",
                            "111",
                            1607415263000,
                            0,
                            "接收到一条消息",
                            IMChatRoomManager.sendUser?.userId ?: "",
                            IMChatRoomManager.receiveUser?.userId ?: "",
                            MessageType.TEXT.str,
                            SendType.SUCCESS.text,
                            false
                    )
                }
                MessageType.IMAGE -> {
                    rewStaue = MessageType.COMMODITY
                    Message(IMChatRoomManager.conversationId ?: "",
                            "111",
                            1607415263000,
                            0,
                            "http://testimg.supersg.cn/user/773870855045251072.jpeg",
                            IMChatRoomManager.sendUser?.userId ?: "",
                            IMChatRoomManager.receiveUser?.userId ?: "",
                            MessageType.IMAGE.str,
                            SendType.SUCCESS.text,
                            false
                    )
                }
                MessageType.COMMODITY -> {
                    rewStaue = MessageType.TEXT
                    val commodityMessage = CommodityMessage("commodityId", "commodityName", "commodityImage", "commodityPrice")//2020-11-08 16:14:23

                    Message(IMChatRoomManager.conversationId ?: "",
                            "111",
                            1607415263000,
                            0,
                            commodityMessage.toJsonString(),
                            IMChatRoomManager.sendUser?.userId ?: "",
                            IMChatRoomManager.receiveUser?.userId ?: "",
                            MessageType.COMMODITY.str,
                            SendType.SUCCESS.text,
                            false
                    )
                }
            }
            IMMessageManager.saveMessage(message, false)
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
                    val commodityMessage = CommodityMessage("commodityId", "commodityName", "commodityImage", "commodityPrice")//2020-11-08 16:14:23
                    IMChatRoomManager.sendCommodityMessage(commodityMessage)
                }
            }
        }
        binding?.initSocket?.setOnClickListener {
            val url = "ws://192.168.0.73:9502"
            val token = "123456"
            val userId = "111111"
            IMManager.connect(url, token, userId, object : onLinkStatus {
                override fun onLinkedSuccess() {
                    Toast.makeText(this@TestActivity, "连接socket成功", Toast.LENGTH_SHORT).show()
                }

                override fun onLinkedClose() {
                    Toast.makeText(this@TestActivity, "连接socket失败", Toast.LENGTH_SHORT).show()
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