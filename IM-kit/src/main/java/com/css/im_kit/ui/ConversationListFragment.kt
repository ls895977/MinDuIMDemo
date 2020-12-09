package com.css.im_kit.ui

import com.css.im_kit.R
import com.css.im_kit.databinding.FragmentConversationListBinding
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.*
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.ui.adapter.ConversationListAdapter
import com.css.im_kit.ui.base.BaseFragment
import com.scwang.smartrefresh.layout.api.RefreshLayout

class ConversationListFragment : BaseFragment<FragmentConversationListBinding?>() {
    private var pageSize = 1
    private var conversationList = arrayListOf<SGConversation>()
    private var conversationListAdapter: ConversationListAdapter? = null

    override fun layoutResource(): Int = R.layout.fragment_conversation_list
    override fun initView() {}
    override fun initData() {
        conversationList = arrayListOf()
        conversationListAdapter = ConversationListAdapter(this@ConversationListFragment.requireContext(), conversationList)
        binding!!.rvConversationList.adapter = conversationListAdapter
        getData()
    }

    override fun initListeners() {
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            pageSize = 1
            conversationList.clear()
            getData()
            refreshLayout.finishRefresh()
        }
        binding!!.refreshView.setOnLoadMoreListener { refreshLayout: RefreshLayout ->
            pageSize++
            getData()
            refreshLayout.finishLoadMore()
        }
    }

    private fun getData() {
        for (i in 1..10) {
            val userInfo = SGUserInfo("conversationId${pageSize}${i}", "name${pageSize}${i}", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
            val messageBody: BaseMessageBody
            when {
                i.rem(2) == 0 -> {
                    messageBody = ImageMessageBody(false, "1607415263000", "1607415263000", false, "http://testimg.supersg.cn/user/773870855045251072.jpeg")//2020-12-08 16:14:23
                    conversationList.add(SGConversation("conversationId${pageSize}${i}", userInfo, SGMessage(MessageType.IMAGE, userInfo, messageBody), pageSize))
                }
                i.rem(3) == 0 -> {
                    messageBody = CommodityMessageBody(false, "1604823263000", "1604823263000", false, "commodityId", "commodityName", "commodityImage", "commodityPrice")//2020-11-08 16:14:23
                    conversationList.add(SGConversation("conversationId${pageSize}${i}", userInfo, SGMessage(MessageType.COMMODITY, userInfo, messageBody), pageSize))
                }
                else -> {
                    messageBody = TextMessageBody(false, "1607501663000", "1607501663000", false, "我是你大爷")//2020-12-09 17:14:23
                    conversationList.add(SGConversation("conversationId${pageSize}${i}", userInfo, SGMessage(MessageType.TEXT, userInfo, messageBody), pageSize))
                }
            }
        }
        conversationListAdapter?.notifyDataSetChanged()
    }
}