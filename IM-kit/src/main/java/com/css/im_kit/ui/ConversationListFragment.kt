package com.css.im_kit.ui

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.css.im_kit.R
import com.css.im_kit.databinding.FragmentConversationListBinding
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.ui.adapter.ConversationListAdapter
import com.css.im_kit.ui.base.BaseFragment
import com.css.im_kit.ui.listener.IMListener

class ConversationListFragment(var setListener: IMListener.SetListener) : BaseFragment<FragmentConversationListBinding?>() {

    private var conversationList = arrayListOf<SGConversation>()
    private var conversationListAdapter: ConversationListAdapter? = null

    override fun layoutResource(): Int = R.layout.fragment_conversation_list
    override fun initView() {}
    override fun initData() {
        conversationList = arrayListOf()
        conversationListAdapter = ConversationListAdapter(this@ConversationListFragment.requireContext(), conversationList)
        binding!!.rvConversationList.adapter = conversationListAdapter
    }

    override fun initListeners() {
        //设置事件
        setListener.onSetItemListener()
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
        binding?.tvConnectStatus?.visibility = if (flag) View.VISIBLE else View.GONE
        binding?.tvConnectStatus?.text = showStr
    }

    /**
     * 添加点击事件
     */
    fun addOnClickListener(clickListener: BaseQuickAdapter.OnItemChildClickListener) {
        //点击item
        conversationListAdapter?.onItemChildClickListener = clickListener
    }

    /**
     * 添加长按事件
     */
    fun addOnLongClickListener(clickLongListener: BaseQuickAdapter.OnItemChildLongClickListener) {
        //长按item
        conversationListAdapter?.onItemChildLongClickListener = clickLongListener
    }
}