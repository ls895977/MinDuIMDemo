package com.example.minduimdemo

import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.*
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.ui.ConversationListFragment
import com.css.im_kit.ui.base.BaseActivity
import com.example.minduimdemo.databinding.ActivityMainBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.*

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var pageSize = 1
    private var conversationList = arrayListOf<SGConversation>()
    private var conversationListFragment: ConversationListFragment? = null

    override fun layoutResource(): Int = R.layout.activity_main
    override fun initView() {
        conversationListFragment = ConversationListFragment()
        val transaction = this@MainActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationListFragment!!)
        transaction.commit()
    }

    override fun initData() {
        binding.title = resources.getString(R.string.app_name)
        conversationList = arrayListOf()
        binding!!.refreshView.autoRefresh()
    }

    override fun initListeners() {
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            pageSize = 1
            conversationList.clear()
            getData()
            conversationListFragment!!.refreshDataList(conversationList)
            refreshLayout.finishRefresh()
        }
        binding!!.refreshView.setOnLoadMoreListener { refreshLayout: RefreshLayout ->
            pageSize++
            getData()
            conversationListFragment!!.addDataList(conversationList)
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
    }

    private fun getData1(): ArrayList<SGConversation> {
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
        return conversationList
    }

//    fun getK() {
//        GlobalScope.launch() {
//            val avatar: Deferred<ArrayList<SGConversation>> = async { getData1() }    // 获取用户头像
////            val logo: Deferred = async { api.getCompanyLogo(user) } // 获取用户所在公司的 logo
////            show(avatar.await(), logo.await())                     // 更新 UI
//        }
//    }
}