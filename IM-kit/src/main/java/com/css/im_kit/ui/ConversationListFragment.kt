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

    private var conversationList = arrayListOf<SGConversation>()
    private var conversationListAdapter: ConversationListAdapter? = null

    override fun layoutResource(): Int = R.layout.fragment_conversation_list
    override fun initView() {}
    override fun initData() {
        conversationList = arrayListOf()
        conversationListAdapter = ConversationListAdapter(this@ConversationListFragment.requireContext(), conversationList)
        binding!!.rvConversationList.adapter = conversationListAdapter
        //设置事件
        setDataListener.onSetFragmentDataListener()
    }

    override fun initListeners() {
        //注册监听
        IMConversationManager.addSGConversationListListener(this)
    }

    /**
     * 清空数据
     */
    fun clearDataList(dataList: ArrayList<SGConversation>) {
        this.conversationList.clear()
        conversationListAdapter?.notifyDataSetChanged()
    }

    /**
     * 刷新数据
     */
    fun refreshDataList(dataList: ArrayList<SGConversation>) {
        this.conversationList.clear()
        this.conversationList.addAll(dataList)
        conversationListAdapter?.notifyDataSetChanged()
    }

    /**
     * 添加下一页数据
     */
    fun addDataList(dataList: ArrayList<SGConversation>) {
        this.conversationList.addAll(dataList)
        conversationListAdapter?.notifyDataSetChanged()
    }

    /**
     * 刷新某一项数据
     */
    fun updateOneList(position: Int, dataItem: SGConversation) {
        this.conversationList[position] = dataItem
        conversationListAdapter?.notifyItemChanged(position)
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
    override fun onConversationList(sgConversation: List<SGConversation>) {
        uiScope.launch {
            conversationList.clear()
            conversationList.addAll(sgConversation)
            conversationListAdapter?.notifyDataSetChanged()
            if (conversationList.isNullOrEmpty()) {
                binding?.ivNoContent?.visibility = View.VISIBLE
                binding?.tvNoContent?.visibility = View.VISIBLE
            } else {
                binding?.ivNoContent?.visibility = View.GONE
                binding?.tvNoContent?.visibility = View.GONE
            }
        }
    }

    /**
     * 退出当前界面才摧毁
     */
    override fun onDestroy() {
        IMConversationManager.removeSGConversationListListener(this)
        super.onDestroy()
    }

    /**
     * 重连点击监听
     */
    fun addLinkOnClickListener(clickListener: View.OnClickListener) {
        binding?.llConnect?.setOnClickListener(clickListener)
    }

}