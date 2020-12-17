package com.example.minduimdemo

import android.content.Intent
import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.model.conversation.SGConversation
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
//            conversationListFragment?.addDataList(getData())

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
        val intent = Intent(this, ConversationActivity::class.java)
        intent.putExtra("conversation", adapter.data[position] as SGConversation)
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