package com.css.im_kit.ui

import android.app.Activity
import android.graphics.Rect
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.css.im_kit.R
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.databinding.FragmentConversationBinding
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.uiScope
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.ui.adapter.ConversationAdapter
import com.css.im_kit.ui.adapter.EmojiAdapter
import com.css.im_kit.ui.base.BaseFragment
import com.css.im_kit.ui.bean.EmojiBean
import com.css.im_kit.ui.listener.IMListener
import com.css.im_kit.utils.FaceTextUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ConversationFragment(private var conversationId: String, private var userInfo: UserInfo, var setDataListener: IMListener.SetDataListener) : BaseFragment<FragmentConversationBinding?>(), View.OnTouchListener {
    private var editTag = false//输入区域状态
    private var keyboardTag = false//软键盘状态
    private var emojiTag = false//表情区域状态
    private var picTag = false//照相机区域状态
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
        //列表向下滑动，隐藏输入区域
        binding?.rvConversationList?.setOnTouchListener(this)
        //点击事件
        adapter?.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.iv_content -> {//图片，看大图

                }
                R.id.ll_content -> {//商品，进详情

                }
            }
        }
        //长按
        adapter?.setOnItemLongClickListener { adapter, view, position ->
            when (view.id) {
                R.id.item_view -> {

                }
            }
            return@setOnItemLongClickListener false
        }
        //输入区
        binding?.etContent?.addTextChangedListener {
            val str = binding?.etContent?.text.toString()
            if (str.isNotEmpty()) {
                binding?.tvSend?.visibility = View.VISIBLE
            } else {
                binding?.tvSend?.visibility = View.GONE
            }
        }

        //表情点击事件
        adapterEmoji?.setOnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.item_view -> {
                    if (position == adapter.data.size - 1) {
                        //点击  删除 按钮
                        binding?.etContent?.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    } else {
                        //点击了表情,则添加到输入框中
                        var sendText = binding?.etContent?.text.toString()
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
        }
        //发送消息
        binding?.tvSend?.setOnClickListener {
            val sendText = binding?.etContent?.text.toString()
            if (sendText.isEmpty()) {
                return@setOnClickListener
            }
            IMChatRoomManager.sendTextMessage(sendText)
            binding?.etContent?.setText("")
        }


        //输入区 TODO
        binding?.etContent?.setOnClickListener {
            if (emojiTag) {
                emojiTag = false
                binding?.llPicView?.visibility = View.GONE
            }
            scrollToBottom()
        }
        //输入区 TODO
        binding?.etContent?.setOnFocusChangeListener { _, b ->
            if (b) {
                if (emojiTag) {
                    emojiTag = false
                    binding?.llPicView?.visibility = View.GONE
                }
            }
            scrollToBottom()
        }
        //表情输入区 TODO
        binding?.ivPic1?.setOnClickListener {
            keyboardTag = isSoftShowing(requireActivity())
            if (keyboardTag) {
                hideSoftKeyboard()
                uiScope.launch {
                    async {
                        delay(50)
                        emojiTag = true
                        binding?.llPicView?.visibility = View.VISIBLE
                    }
                }
            } else {
                emojiTag = !emojiTag
                binding?.llPicView?.visibility = if (emojiTag) View.VISIBLE else View.GONE
            }
            scrollToBottom()
        }
        //图片输入区  TODO
        binding?.ivPic2?.setOnClickListener {
            scrollToBottom()
        }


    }

    /**
     * 判断软键盘是否打开
     * @param activity
     * @return
     */
    private fun isSoftShowing(activity: Activity): Boolean {
        //获取当前屏幕内容的高度
        val screenHeight = activity.window.decorView.height
        //获取View可见区域的bottom
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect)
        return (screenHeight - rect.bottom) > 0
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
                        scrollToBottom()
                    }

                    override fun onMessages(message: List<SGMessage>) {
                        //拉去数据库没聊天列表
                        messageList.addAll(message)
                        adapter?.notifyDataSetChanged()
                        scrollToBottom()
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
        editTag = false
        //关闭键盘
        keyboardTag = false
        hideSoftKeyboard()
        //隐藏表情区域
        emojiTag = false
        binding?.llPicView?.visibility = View.GONE
        //隐藏发送按钮
        picTag = false
        binding?.tvSend?.visibility = View.GONE
    }

    /**
     * 将消息列表滑动到底部
     */
    private fun scrollToBottom() {
        editTag = keyboardTag || emojiTag || picTag
        uiScope.launch {
            async {
                delay(100)
                binding?.rvConversationList?.layoutManager?.scrollToPosition(messageList.size - 1)
            }
        }
    }

    /**
     * 监听向下滑动隐藏输入区域
     */
    private var sy = 0
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (v?.id) {
            R.id.rv_conversation_list -> when (event!!.action) {
                MotionEvent.ACTION_DOWN -> {
                    sy = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val dy: Int = event.rawY.toInt() - sy
                    if (dy > 0) {
                        if (editTag) {
                            hideView()
                        }
                    }
                }
            }
        }
        return false
    }
}