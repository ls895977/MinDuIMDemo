package com.example.minduimdemo

import android.util.Log
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.callback.SGMessageCallback
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.manager.IMMessageManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.BaseMessageBody
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
                val list = arrayListOf<User_Info>()
                val userInfo4 = User_Info("本人", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "111111")
                val userInfo = User_Info("昵称1", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100001")
                val userInfo1 = User_Info("昵称2", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100002")
                val userInfo2 = User_Info("昵称3", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100003")
                val userInfo3 = User_Info("昵称4", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100004")
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
                val message1 = Message("111", "111", "1607415263000", "1607415263000", "1111", "100001", "111111", MessageType.TEXT.str)
                val message2 = Message("222", "222", "1607415263000", "1607415263000", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "100002", "111111", MessageType.IMAGE.str)
                val message3 = Message("333", "333", "1607415263000", "1607415263000", commodityMessage.toJsonString(), "100003", "111111", MessageType.COMMODITY.str)
                val message4 = Message("111", "444", "1607415263000", "1607415263000", "1111", "111111", "100001", MessageType.TEXT.str)
                val message5 = Message("222", "555", "1607415263000", "1607415263000", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "111111", "100002", MessageType.IMAGE.str)
                val message6 = Message("333", "666", "1607415263000", "1607415263000", commodityMessage.toJsonString(), "111111", "100003", MessageType.COMMODITY.str)
                val message7 = Message("111", "777", "1607415263000", "1607415263000", "1111", "100004", "111111", MessageType.TEXT.str)
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
                val conversation1 = Conversation("111", "100001", "111111")
                val conversation2 = Conversation("222", "100002", "111111")
                val conversation3 = Conversation("333", "100003", "111111")
                val conversation4 = Conversation("445", "100004", "111111")
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
                IMMessageManager.dismissSgMessageCallback()
            } else {
                IMMessageManager
                        .initConversation("111")
                        .addSGConversationListListener(object : SGMessageCallback {
                            override fun onReceiveMessage(message: SGMessage) {
                                Log.e("111", "1111")
                            }

                            override fun onMessages(message: List<SGMessage>) {
                                Log.e("111", "1111")
                            }

                        })
                        .create()


            }
            inChat = !inChat
        }
    }

    override fun onConversationList(sgConversation: List<SGConversation>) {
        Log.e("111", sgConversation.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        IMConversationManager.removeSGConversationListListener(this)
    }
}