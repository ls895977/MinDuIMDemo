package com.example.minduimdemo

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.css.im_kit.IMManager
import com.css.im_kit.TokenCallBack
import com.css.im_kit.db.uiScope
import com.css.im_kit.imservice.interfacelinsterner.onLinkStatus
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.ui.ConversationListFragment
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.ui.listener.IMListener
import com.example.minduimdemo.databinding.ActivityMainBinding
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(), IMListener.SetDataListener, BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemChildLongClickListener {
    //    private var conversationList = arrayListOf<SGConversation>()
    private var conversationListFragment: ConversationListFragment? = null

    override fun layoutResource(): Int = R.layout.activity_main
    override fun initView() {
//        conversationList = arrayListOf()
        binding.title = resources.getString(R.string.app_name)
        conversationListFragment = ConversationListFragment(this)
        val transaction = this@MainActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationListFragment!!)
        transaction.commit()
    }

    override fun initData() {
        //连接服务器
        connectionIMService()
    }

    override fun initListeners() {
        //小鹏测试类
        binding?.addData?.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }
        //刷新
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            if (isConnected) {
                IMConversationManager.getConversationList()//数据库room拿数据
            } else {
                IMManager.retryService()
            }
            refreshLayout.finishRefresh()
        }
    }

    /**
     * Fragment 初始化好了，可以设置值和监听
     */
    override fun onSetFragmentDataListener() {
        //item点击
        conversationListFragment?.addOnClickListener(this)
        //重连点击监听
        conversationListFragment?.addLinkOnClickListener(View.OnClickListener {
            isConnected = false
            conversationListFragment?.updateContentShowView(true, "客服系统连接中...")
            IMManager.retryService()
        })
    }
    /**
     * 消息点击事件
     */
    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        //分配客服跳转
//        val comeFromTag = 0
//        val shopId = "1"
//        val productId = "sdsa"
//        val productImage = "http://testimg.supersg.cn/user/773870855045251072.jpeg"
//        val productName = "测试商品"
//        val productPrice = "200"
//        ConversationActivity.toConversationActivity(this, comeFromTag, shopId, productId, productImage, productName, productPrice)
        //普通跳转
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtra("conversation", adapter.data[position] as SGConversation)
        startActivity(intent)
    }

    /**
     * 消息长按事件
     */
    override fun onItemChildLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        Toast.makeText(this, "长按了", Toast.LENGTH_SHORT).show()
        return true
    }

    /**
     * 连接IM服务器
     */

    private var isConnected = false
    private fun connectionIMService() {
        val url = "ws://testchatws.supersg.cn"
        val token = "207188408521527296"
        val userId = "207188408521527296"
        IMManager.connect(url, token, userId, object : TokenCallBack {
            override fun getToken(): String {
                return "CF77CF8F35CC4716C02FD3D20778D8E4"
            }

            override fun getImageBaseUrl(): String {
                return "http://testimg.supersg.cn/"
            }

        }, object : onLinkStatus {
            override fun onLinkedSuccess() {
                uiScope.launch {
                    isConnected = true
                    //连接状态展示
                    conversationListFragment?.updateContentShowView(false, "客服系统连接成功")
                    //获取第一次数据
                    IMConversationManager.getConversationList()//数据库room拿数据
                }
            }

            override fun onLinkedClose() {
                uiScope.launch {
                    isConnected = false
                    //连接状态展示
                    conversationListFragment?.updateContentShowView(true, "客服系统连接失败")
                }
            }
        })
    }
}