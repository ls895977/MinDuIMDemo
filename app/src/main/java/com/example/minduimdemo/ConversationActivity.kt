package com.example.minduimdemo

import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.ui.ConversationFragment
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.ui.listener.IMListener
import com.example.minduimdemo.databinding.ActivityConversationBinding

class ConversationActivity : BaseActivity<ActivityConversationBinding>(), IMListener.SetDataListener {
    private var consavertionId = ""
    private var userInfo: UserInfo? = null
    private var conversationFragment: ConversationFragment? = null

    override fun layoutResource(): Int = R.layout.activity_conversation

    override fun initView() {
        consavertionId = intent.getStringExtra("consavertionId") ?: ""
        val userId = intent.getStringExtra("userId") ?: ""
        val userName = intent.getStringExtra("userName") ?: ""
        val userAvatar = intent.getStringExtra("userAvatar") ?: ""
        userInfo = UserInfo(userName, userAvatar, userId)
        binding.nickName = userName
    }

    override fun initData() {
        conversationFragment = userInfo?.let { ConversationFragment(consavertionId, it, this) }
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

    }


}