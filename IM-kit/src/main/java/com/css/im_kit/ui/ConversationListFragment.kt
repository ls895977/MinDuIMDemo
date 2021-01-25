package com.css.im_kit.ui

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.css.im_kit.R
import com.css.im_kit.callback.SGConversationCallback
import com.css.im_kit.databinding.FragmentConversationListBinding
import com.css.im_kit.db.uiScope
import com.css.im_kit.manager.IMConversationManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.ui.adapter.ConversationListAdapter
import com.css.im_kit.ui.base.BaseFragment
import com.css.im_kit.ui.listener.IMListener
import kotlinx.coroutines.launch

class ConversationListFragment(private var setDataListener: IMListener.SetDataListener) : BaseFragment<FragmentConversationListBinding?>(), SGConversationCallback {

    private var conversationListAdapter: ConversationListAdapter? = null

    //是否拉去过数据
    var hasGetData = false

    override fun layoutResource(): Int = R.layout.fragment_conversation_list
    override fun initView() {}
    override fun initData() {
        conversationListAdapter = ConversationListAdapter(this@ConversationListFragment.requireContext())
        binding!!.rvConversationList.adapter = conversationListAdapter
        //设置事件
        setDataListener.onSetFragmentDataListener()
    }

    override fun initListeners() {
        //注册监听
        IMConversationManager.addSGConversationListListener(this)
    }

    /**
     * @flag：显示隐藏
     * @showStr：显示文字内容
     */
    fun updateContentShowView(flag: Boolean, showStr: String) {
        binding?.llConnect?.visibility = if (flag) View.VISIBLE else View.GONE
        binding?.vLine?.visibility = if (flag) View.VISIBLE else View.GONE
        binding?.tvConnectStatus?.text = showStr
    }

    /**
     * 添加点击事件item
     */
    fun addOnClickListener(clickListener: BaseQuickAdapter.OnItemChildClickListener) {
        conversationListAdapter?.onItemChildClickListener = clickListener
    }

    /**
     * 添加长按事件item
     */
    fun addOnLongClickListener(clickLongListener: BaseQuickAdapter.OnItemChildLongClickListener) {
        conversationListAdapter?.onItemChildLongClickListener = clickLongListener
    }

    /**
     * 拿到数据了
     */
    @Synchronized
    override fun onConversationList(sgConversation: List<SGConversation>) {
        uiScope.launch {
            synchronized(this) {
                conversationListAdapter?.setNewData(sgConversation)
            }
            if (sgConversation.isNullOrEmpty()) {
                binding?.ivNoContent?.visibility = View.VISIBLE
                binding?.tvNoContent?.visibility = View.VISIBLE
            } else {
                binding?.ivNoContent?.visibility = View.GONE
                binding?.tvNoContent?.visibility = View.GONE
            }
        }
        hasGetData = !sgConversation.isNullOrEmpty()
    }

    /**
     * 退出当前界面才摧毁
     */
    override fun onDestroy() {
        IMConversationManager.removeSGConversationListListener()
        super.onDestroy()
    }

    /**
     * 重连点击监听
     */
    fun addLinkOnClickListener(clickListener: View.OnClickListener) {
        binding?.llConnect?.setOnClickListener(clickListener)
    }

}