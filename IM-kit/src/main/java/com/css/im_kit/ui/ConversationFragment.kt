package com.css.im_kit.ui

import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.css.im_kit.R
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.databinding.FragmentConversationBinding
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.ui.adapter.ConversationAdapter
import com.css.im_kit.ui.adapter.EmojiAdapter
import com.css.im_kit.ui.base.BaseFragment
import com.css.im_kit.ui.bean.EmojiBean
import com.css.im_kit.ui.listener.IMListener
import com.css.im_kit.utils.FaceTextUtil
import com.css.im_kit.utils.IMSoftKeyBoardListenerUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout


class ConversationFragment(private var conversationId: String, private var userInfo: UserInfo, var setDataListener: IMListener.SetDataListener) : BaseFragment<FragmentConversationBinding?>() {
    private var keyboardTag = false//软键盘状态
    private var emojiTag = false//表情区域状态
    private var picTag = false//照相机区域状态
    private var nowCheckTag = 0//当前是哪个区域需要展示，1 表情区域，2 照相机区域，0其他区域
    private var messageList = arrayListOf<SGMessage>()
    private var adapter: ConversationAdapter? = null
    private var adapterEmoji: EmojiAdapter? = null

    override fun layoutResource(): Int = R.layout.fragment_conversation

    override fun initView() {
    }

    override fun initData() {
        messageList = arrayListOf()
        adapter = ConversationAdapter(requireActivity(), messageList)
        binding?.rvConversationList?.adapter = adapter
        //设置事件
        setDataListener.onSetFragmentDataListener()

        //表情
        val gridLayoutManager = GridLayoutManager(this.context, 7)
        binding!!.rvEmojiList.layoutManager = gridLayoutManager
        adapterEmoji = EmojiAdapter(requireContext(), FaceTextUtil.faceTexts)
        binding!!.rvEmojiList.adapter = adapterEmoji
    }

    override fun initListeners() {
        //刷新
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            refreshLayout.finishRefresh()
        }

        /**
         * 软件盘显示、隐藏监听
         */
        IMSoftKeyBoardListenerUtil.setListener(requireActivity(), object : IMSoftKeyBoardListenerUtil.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                keyboardTag = true
                //滚动到底部
                binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
            }

            override fun keyBoardHide(height: Int) {
                keyboardTag = false
                if (nowCheckTag == 1) {
                    //显示表情输入区
                    binding?.llEmojiView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
                } else if (nowCheckTag == 2) {
                    //显示图片输入区
                    binding?.llPicView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
                }
            }
        })
        //点击事件
        adapter!!.setOnItemChildClickListener { adapter, view, position ->
            hideView()
            when (view.id) {
                R.id.iv_content -> {//图片，看大图

                }
                R.id.ll_content -> {//商品，进详情

                }
                R.id.item_view -> {
                }
            }
        }
        //长按删除
        adapter!!.setOnItemLongClickListener { adapter, view, position ->
            when (view.id) {
                R.id.item_view -> {

                }
            }
            return@setOnItemLongClickListener false
        }

        //输入区
        binding!!.etContent.addTextChangedListener {
            val str = binding?.etContent?.text.toString()
            if (str.isNotEmpty()) {
                binding?.tvSend?.visibility = View.VISIBLE
            } else {
                binding?.tvSend?.visibility = View.GONE
            }
        }

        //表情点击事件
        adapterEmoji!!.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.item_view -> {
                    //点击了表情,则添加到输入框中
                    val sendText = binding?.etContent?.text.toString()
                    val name: EmojiBean = adapter.getItem(position) as EmojiBean
                    val key: String = name.text.toString()
                    val start: Int = binding?.etContent?.selectionStart!!
                    val aps: SpannableString = FaceTextUtil.toSpannableString(requireActivity(), "${sendText.substring(0, start)}${key}${sendText.substring(start)}")
                    binding?.etContent?.setText(aps)
                    // 定位光标位置
                    val info: CharSequence = binding?.etContent?.text!!
                    if (info is Spannable) {
                        Selection.setSelection(info, start + key.length)
                    }
                }
            }
        }
        //删除表情点击事件
        binding!!.ivDeleceEmoji.setOnClickListener {
            binding?.etContent?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
        }
        //发送消息
        binding!!.tvSend.setOnClickListener {
            val sendText = binding?.etContent?.text.toString()
            if (sendText.isEmpty()) {
                return@setOnClickListener
            }
            IMChatRoomManager.sendTextMessage(sendText)
            binding?.etContent?.setText("")
        }
        //输入区
        binding?.etContent?.setOnClickListener {
            nowCheckTag = 0
            //获取当前软键盘的状态，true打开，false隐藏
            if (!keyboardTag) {//先隐藏显示区域》再显示软件盘》滚到底部
                //隐藏表情输入区(只有显示的时候才隐藏，你面重复)
                if (emojiTag) {
                    emojiTag = false
                    binding?.llEmojiView?.visibility = View.GONE
                }
                //隐藏图片输入区(只有显示的时候才隐藏，你面重复)
                if (picTag) {
                    picTag = false
                    binding?.llPicView?.visibility = View.GONE
                }
                showSoftKeyboard(binding?.etContent)
            }
        }
        //表情输入区
        binding?.ivPic1?.setOnClickListener {
            nowCheckTag = 0
            emojiTag = !emojiTag
            if (emojiTag) {//先关闭展开布局》再显示表情输入区
                //隐藏图片输入区(只有显示的时候才隐藏，你面重复)
                if (picTag) {
                    picTag = false
                    binding?.llPicView?.visibility = View.GONE
                    //显示表情输入区
                    binding?.llEmojiView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
                    return@setOnClickListener
                }
                //隐藏输入区（软键盘）(只有显示的时候才隐藏，你面重复)
                if (keyboardTag) {
                    nowCheckTag = 1
                    hideSoftKeyboard(binding?.etContent)
                    return@setOnClickListener
                }
                //显示表情输入区
                binding?.llEmojiView?.visibility = View.VISIBLE
                //滚动到底部
                binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
            } else {
                //隐藏表情输入区
                binding?.llEmojiView?.visibility = View.GONE
                //展示软件盘区
                if (!keyboardTag) {
                    showSoftKeyboard(binding?.etContent)
                }
            }

        }
        //图片输入区
        binding?.ivPic2?.setOnClickListener {
            nowCheckTag = 0
            picTag = !picTag
            if (picTag) {//先关闭展开布局》再显示图片输入区
                //隐藏表情输入区(只有显示的时候才隐藏，你面重复)
                if (emojiTag) {
                    emojiTag = false
                    binding?.llEmojiView?.visibility = View.GONE
                    //显示图片输入区
                    binding?.llPicView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
                    return@setOnClickListener
                }
                //隐藏输入区（软键盘）(只有显示的时候才隐藏，你面重复)
                if (keyboardTag) {
                    nowCheckTag = 2
                    hideSoftKeyboard(binding?.etContent)
                    return@setOnClickListener
                }
                //显示图片输入区
                binding?.llPicView?.visibility = View.VISIBLE
                //滚动到底部
                binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
            } else {
                //隐藏图片输入区
                binding?.llPicView?.visibility = View.GONE
                //展示软件盘区
                if (!keyboardTag) {
                    showSoftKeyboard(binding?.etContent)
                }
            }
        }
    }

    /**
     * Activity
     * 调用
     * 更新当前界面数据
     */
    fun updateData() {
        Toast.makeText(requireContext(), "进入会话", Toast.LENGTH_SHORT).show()
        IMChatRoomManager
                .initConversation(conversationId)
                .addSGConversationListListener(object : ChatRoomCallback {
                    override fun onReceiveMessage(message: SGMessage) {
                        //发送成功，接收到消息，插入当前返回的这一条Message
                        messageList.add(message)
                        adapter?.notifyItemChanged(messageList.size)
                        binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
                    }

                    override fun onMessages(message: List<SGMessage>) {
                        //拉去数据库没聊天列表
                        messageList.addAll(message)
                        adapter?.notifyDataSetChanged()
                        binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
                    }

                    override fun onMessageInProgress(message: SGMessage) {
                        //消息发送进度
                    }

                })
                .create()
    }

    /**
     * 隐藏输入区域
     */
    private fun hideView() {
        //关闭键盘
        keyboardTag = false
        hideSoftKeyboard(binding?.etContent)
        //隐藏表情区域
        emojiTag = false
        binding?.llEmojiView?.visibility = View.GONE
        //图片区域
        picTag = false
        binding?.llPicView?.visibility = View.GONE
        //隐藏发送按钮
        binding?.tvSend?.visibility = View.GONE
    }

    /**
     * 摧毁时关闭软件盘
     */
    override fun onDestroy() {
        hideSoftKeyboard(binding?.etContent)
        super.onDestroy()
    }

    /**
     * 添加输入区（商品按钮）点击事件
     */
    fun addProductOnClickListener(clickListener: View.OnClickListener) {
        binding!!.llSendProduct.setOnClickListener(clickListener)

    }

    /**
     * 添加输入区（图片按钮）点击事件
     */
    fun addPicOnClickListener(clickListener: View.OnClickListener) {
        binding!!.llSendPic.setOnClickListener(clickListener)
    }
}