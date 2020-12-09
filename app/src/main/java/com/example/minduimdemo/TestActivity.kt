package com.example.minduimdemo

import android.util.Log
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.db.bean.Conversation
import com.css.im_kit.db.bean.Message
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.ConversationRepository
import com.css.im_kit.db.repository.MessageRepository
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.manager.IMManager
import com.css.im_kit.model.conversation.SGConversation
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
        binding.title = resources.getString(R.string.app_name)
    }

    override fun initListeners() {
        IMManager.getInstance(this).addSGConversationListListener(this)
        binding?.addUser?.setOnClickListener {
            ioScope.launch {
                val userInfo = User_Info("昵称", "头像链接", "10001")
                UserInfoRepository.getInstance(this@TestActivity).insert(userInfo)
                val stak = async {
                    UserInfoRepository.getInstance(this@TestActivity).getAll()
                }
                val result = stak.await()
                result.collect {
                    binding?.addUser?.text = "添加用户" + it.size
                }
            }
        }
        binding?.addMessage?.setOnClickListener {
            ioScope.launch {
                val message = Message("111", "1111", "1111", "1111", "1111", "1111", "1111")
                MessageRepository.getInstance(this@TestActivity).insert(message)
                val stak = async {
                    MessageRepository.getInstance(this@TestActivity).getAll()
                }
                val result = stak.await()
                result.collect {
                    binding?.addMessage?.text = "添加消息" + it.size
                }
            }
        }

        binding?.addConversation?.setOnClickListener {
            ioScope.launch {
                val conversation = Conversation("111", "111", "11111")
                ConversationRepository.getInstance(this@TestActivity).insert(conversation)
                val stak = async {
                    ConversationRepository.getInstance(this@TestActivity).getAll()
                }
                val result = stak.await()
                result.collect {
                    binding?.addConversation?.text = "添加会话" + it.size
                }
            }
        }
        binding?.getSGConversation?.setOnClickListener {
            IMManager.getInstance(this).getConversationList()
        }
    }

    override fun onConversationList(sgConversation: List<SGConversation>) {
        Log.e("111", sgConversation.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        IMManager.getInstance(this).removeSGConversationListListener(this)
    }
}