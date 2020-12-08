package com.css.im_kit.ui

import com.css.im_kit.R
import com.css.im_kit.databinding.FragmentConversationBinding
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.ui.base.BaseFragment

class ConversationFragment : BaseFragment<FragmentConversationBinding?>() {

    private var userInfo: User_Info? = null

    override fun layoutResource(): Int = R.layout.fragment_conversation

    override fun initView() {
        userInfo = User_Info("我是大爷", "")
        binding!!.userInfo = userInfo
    }

    override fun initData() {}
    override fun initListeners() {}
}