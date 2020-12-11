package com.css.im_kit.ui

import com.css.im_kit.R
import com.css.im_kit.databinding.FragmentConversationBinding
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.ui.base.BaseFragment

class ConversationFragment : BaseFragment<FragmentConversationBinding?>() {

    private var userInfo: UserInfo? = null

    override fun layoutResource(): Int = R.layout.fragment_conversation

    override fun initView() {
        userInfo = UserInfo("我是大爷", "","1001")
        binding!!.userInfo = userInfo
    }

    override fun initData() {}
    override fun initListeners() {}
}