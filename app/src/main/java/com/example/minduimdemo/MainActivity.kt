package com.example.minduimdemo

import android.content.Intent
import androidx.lifecycle.lifecycleScope
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
        getK()
    }

    /**
     * Kotlin 协程获取数据，避免Fragment还没初始化view成功
     */
    private fun getK() {
        this.lifecycleScope.launch(Dispatchers.Main) {
            val list: Deferred<ArrayList<SGConversation>> = async { getData() }
            conversationListFragment?.refreshDataList(list.await())
            //连接状态展示
            conversationListFragment?.updateContentShowView(true, "连接状态展示>连接失败（假的）")
        }
    }

    override fun initListeners() {
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            pageSize = 1
            conversationListFragment?.refreshDataList(getData())
            //连接状态展示
            conversationListFragment?.updateContentShowView(true, "连接状态展示>连接失败（假的）")
            refreshLayout.finishRefresh()
        }
        binding!!.refreshView.setOnLoadMoreListener { refreshLayout: RefreshLayout ->
            pageSize++
            conversationListFragment?.addDataList(getData())
            //连接状态展示
            conversationListFragment?.updateContentShowView(false, "连接状态展示>连接成功（假的）")
            refreshLayout.finishLoadMore()
        }
        binding?.addData?.setOnClickListener {
            val intent = Intent(this,TestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getData(): ArrayList<SGConversation> {
        conversationList.clear()
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
}