package com.example.minduimdemo

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.*
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.ui.ConversationListFragment
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.ui.listener.IMListener
import com.example.minduimdemo.databinding.ActivityMainBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout

class MainActivity : BaseActivity<ActivityMainBinding>(), IMListener.SetDataListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemChildLongClickListener {
    private var pageSize = 1
    private var conversationList = arrayListOf<SGConversation>()
    private var conversationListFragment: ConversationListFragment? = null

    override fun layoutResource(): Int = R.layout.activity_main
    override fun initView() {
        conversationList = arrayListOf()
        binding.title = resources.getString(R.string.app_name)
    }

    override fun initData() {
        conversationListFragment = ConversationListFragment(this)
        val transaction = this@MainActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationListFragment!!)
        transaction.commit()
    }

    override fun initListeners() {
        //刷新
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            pageSize = 1
            IMConversationManager.getConversationList()//数据库room拿数据
            refreshLayout.finishRefresh()
//            conversationListFragment?.refreshDataList(getData())
//            //连接状态展示
//            conversationListFragment?.updateContentShowView(true, "连接状态展示>连接失败（假的）")
        }
        //加载
        binding!!.refreshView.setOnLoadMoreListener { refreshLayout: RefreshLayout ->
            pageSize++
            conversationListFragment?.addDataList(getData())
            //连接状态展示
            conversationListFragment?.updateContentShowView(false, "连接状态展示>连接成功（假的）")
            refreshLayout.finishLoadMore()
        }

        binding?.addData?.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * 加载更多，假数据
     */
    private fun getData(): ArrayList<SGConversation> {
        conversationList.clear()
        for (i in 1..10) {
            val userInfo = SGUserInfo("conversationId${pageSize}${i}", "name${pageSize}${i}", "http://testimg.supersg.cn/user/773870855045251072.jpeg")
            val messageBody: BaseMessageBody
            when {
                i.rem(2) == 0 -> {
                    messageBody = ImageMessageBody(false, "1607415263000", "1607415263000", false, "http://testimg.supersg.cn/user/773870855045251072.jpeg")//2020-12-08 16:14:23
                    conversationList.add(SGConversation("conversationId${pageSize}${i}", userInfo, SGMessage("conversationId${pageSize}${i}", "111", MessageType.IMAGE, userInfo, messageBody), pageSize))
                }
                i.rem(3) == 0 -> {
                    messageBody = CommodityMessageBody(false, "1604823263000", "1604823263000", false, "commodityId", "commodityName", "commodityImage", "commodityPrice")//2020-11-08 16:14:23
                    conversationList.add(SGConversation("conversationId${pageSize}${i}", userInfo, SGMessage("conversationId${pageSize}${i}", "111", MessageType.COMMODITY, userInfo, messageBody), pageSize))
                }
                else -> {
                    messageBody = TextMessageBody(false, "1607501663000", "1607501663000", false, "我是你大爷")//2020-12-09 17:14:23
                    conversationList.add(SGConversation("conversationId${pageSize}${i}", userInfo, SGMessage("conversationId${pageSize}${i}", "111", MessageType.TEXT, userInfo, messageBody), pageSize))
                }
            }
        }
        return conversationList
    }

    /**
     * Fragment 初始化好了，可以设置值和监听
     */
    override fun onSetFragmentDataListener() {
        //获取第一次数据
        IMConversationManager.getConversationList()//数据库room拿数据
        //连接状态展示
        conversationListFragment?.updateContentShowView(true, "连接状态展示>连接失败（假的ssss）")
        //item点击
        conversationListFragment?.addOnClickListener(this)
        //item长按
        conversationListFragment?.addOnLongClickListener(this)
    }


    /**
     * 消息点击事件
     */
    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        Toast.makeText(this, "点击了", Toast.LENGTH_SHORT).show()
        val sgConversation = adapter.data[position] as SGConversation
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtra("consavertionId", sgConversation.conversationId)
        intent.putExtra("userId", sgConversation.userInfo?.userId)
        intent.putExtra("userName", sgConversation.userInfo?.userName)
        intent.putExtra("userAvatar", sgConversation.userInfo?.avatar)
        startActivity(intent)
    }

    /**
     * 消息长按事件
     */
    override fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        Toast.makeText(this, "长按了", Toast.LENGTH_SHORT).show()
        return false
    }


}