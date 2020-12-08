package com.css.im_kit.ui

import com.css.im_kit.R
import com.css.im_kit.databinding.FragmentConversationListBinding
import com.css.im_kit.db.bean.User_Info
import com.css.im_kit.ui.base.BaseFragment

class ConversationListFragment : BaseFragment<FragmentConversationListBinding?>() {


    override fun layoutResource(): Int = R.layout.fragment_conversation_list

    override fun initView() {
    }

    override fun initData() {}
    override fun initListeners() {}
}