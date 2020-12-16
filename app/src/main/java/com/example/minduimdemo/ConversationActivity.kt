package com.example.minduimdemo

import android.view.View
import android.widget.Toast
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.ui.ConversationFragment
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.ui.listener.IMListener
import com.example.minduimdemo.databinding.ActivityConversationBinding

class ConversationActivity : BaseActivity<ActivityConversationBinding>(), IMListener.SetDataListener {
    private var conversation: SGConversation? = null
    private var conversationFragment: ConversationFragment? = null

    override fun layoutResource(): Int = R.layout.activity_conversation

    override fun initView() {
        conversation = intent.getSerializableExtra("conversation") as SGConversation?
        if (conversation?.shop != null) {
            binding.nickName = conversation?.shop?.shop_name
        } else {
            binding.nickName = conversation?.chat_account_info?.nickname
        }

    }

    override fun initData() {
        conversationFragment = conversation?.let { ConversationFragment(it, this) }
        val transaction = this@ConversationActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationFragment!!)
        transaction.commit()
    }

    override fun initListeners() {
    }

    /**
     * Fragment初始化好了
     * 开始设置数据\监听等
     */
    override fun onSetFragmentDataListener() {
        //拉去回话数据
        conversationFragment?.updateData()
        //发送商品消息点击事件
        conversationFragment?.addProductOnClickListener(View.OnClickListener {
            //发送商品消息（选择商品等后续逻辑） TODO

        })
        //发送图片消息点击事件
        conversationFragment?.addPicOnClickListener(View.OnClickListener {
            //发送图片消息（选择图片等后续逻辑） TODO
        })
    }

    override fun onDestroy() {
        IMChatRoomManager.dismissSgMessageCallback()
        Toast.makeText(this, "退出会话", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }


}