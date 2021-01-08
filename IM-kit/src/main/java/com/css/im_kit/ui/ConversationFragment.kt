package com.css.im_kit.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.css.im_kit.R
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.databinding.FragmentConversationBinding
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.bean.SendType
import com.css.im_kit.db.uiScope
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.message.CommodityMessageBody
import com.css.im_kit.model.message.ImageMessageBody
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.ui.activity.BigPicActivity
import com.css.im_kit.ui.adapter.ConversationAdapter
import com.css.im_kit.ui.adapter.EmojiAdapter
import com.css.im_kit.ui.base.BaseFragment
import com.css.im_kit.ui.bean.EmojiBean
import com.css.im_kit.ui.listener.IMListener
import com.css.im_kit.utils.FaceTextUtil
import com.css.im_kit.utils.IMDensityUtils
import com.css.im_kit.utils.IMGlideUtil
import com.css.im_kit.utils.IMSoftKeyBoardListenerUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ConversationFragment(private var conversation: SGConversation, private var setDataListener: IMListener.SetDataListener, var setClickProductListener: IMListener.SetClickProductListener) : BaseFragment<FragmentConversationBinding?>() {
    private var isFirstGetData = false//是否第一次获取数据
    private var keyboardTag = false//软键盘状态
    private var emojiTag = false//表情区域状态
    private var picTag = false//照相机区域状态
    private var nowCheckTag = 0//当前是哪个区域需要展示，1 表情区域，2 照相机区域，0其他区域
    private var adapter: ConversationAdapter? = null
    private var adapterEmoji: EmojiAdapter? = null

    //商品联系客服时需要
    private var productId = ""
    private var productImage = ""
    private var productName = ""
    private var productPrice = ""

    override fun layoutResource(): Int = R.layout.fragment_conversation

    override fun initView() {
//        ClassicsHeader.REFRESH_HEADER_PULLING = "下拉可以加载"
//        ClassicsHeader.REFRESH_HEADER_REFRESHING = "正在加载..."
//        ClassicsHeader.REFRESH_HEADER_RELEASE = "释放立即加载"
//        ClassicsHeader.REFRESH_HEADER_FINISH = "加载完成"
//        ClassicsHeader.REFRESH_HEADER_FAILED = "加载失败"
    }

    override fun initData() {
        adapter = ConversationAdapter(requireActivity())
        binding?.rvConversationList?.adapter = adapter
        val layout = LinearLayoutManager(requireContext())
        layout.stackFromEnd = false //列表再底部开始展示，反转后由上面开始展示
        layout.reverseLayout = true //列表翻转
        binding?.rvConversationList?.layoutManager = layout
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
        binding?.refreshView?.setOnRefreshListener { refreshLayout: RefreshLayout ->
            isFirstGetData = false
            refreshLayout.finishRefresh()
            val messageList = adapter?.data
            if (!messageList.isNullOrEmpty()) {
                IMChatRoomManager.getMessages(messageList.last())
            } else {
                IMChatRoomManager.getMessages(System.currentTimeMillis(), 30)
            }
        }

        /**
         * 软件盘显示、隐藏监听
         */
        IMSoftKeyBoardListenerUtil.setListener(requireActivity(), object : IMSoftKeyBoardListenerUtil.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                keyboardTag = true
                //隐藏表情输入区
                emojiTag = false
                binding?.ivPic1?.setImageResource(R.mipmap.im_icon_send1)
                binding?.llEmojiView?.visibility = View.GONE
                //隐藏图片输入区
                picTag = false
                binding?.ivPic2?.setImageResource(R.mipmap.im_icon_send3)
                binding?.llPicView?.visibility = View.GONE
                //滚动到底部
                binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
                //发送按钮展示与否
                hideSendText()
            }

            override fun keyBoardHide(height: Int) {
                keyboardTag = false
                if (nowCheckTag == 1) {
                    //显示表情输入区
                    binding?.llEmojiView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
                } else if (nowCheckTag == 2) {
                    //显示图片输入区
                    binding?.llPicView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
                }
                //发送按钮展示与否
                hideSendText()
            }
        })
        //点击事件
        adapter!!.setOnItemChildClickListener { adapter, view, position ->
            hideView()
            when (view.id) {
                R.id.iv_content -> {//图片，看大图
                    ((adapter.data[position] as SGMessage).messageBody as ImageMessageBody).imageUrl?.let {
                        val mIntent = Intent(requireContext(), BigPicActivity::class.java)
                        mIntent.putExtra("imageUrl", if (it.contains("sgim")) IMGlideUtil.getAllUrl(it) else it)
                        val compat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), view, getString(R.string.big_image))
                        ActivityCompat.startActivity(requireContext(), mIntent, compat.toBundle())
                    }
                }
                R.id.ll_content -> {//商品，进详情
                    setClickProductListener.onGoProductDetail((adapter.data[position] as SGMessage).messageBody as CommodityMessageBody)
                }
                R.id.loading_image -> {//消息发送失败》重发!!!!!(只有自己的消息可以重发)
                    (adapter.data[position] as SGMessage).let {
                        it.messageBody?.sendType = SendType.SENDING
                        adapter.notifyItemChanged(position)
                        IMChatRoomManager.messageReplay(it.messageId)
                    }
                }
                //整个item
//                R.id.item_view -> {}
            }
        }
//        //长按删除
//        adapter!!.setOnItemLongClickListener { adapter, view, position ->
//            when (view.id) {
//                R.id.item_view -> {}
//            }
//            return@setOnItemLongClickListener false
//        }

        //输入区（发送按钮展示与否）
        binding!!.etContent.addTextChangedListener {
            hideSendText()
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
        //输入区(点击)
        binding?.etContent?.setOnClickListener {
            binding?.rlMessage7?.visibility = View.GONE
            nowCheckTag = 0
            //获取当前软键盘的状态，true打开，false隐藏
            if (!keyboardTag) {
                showSoftKeyboard(binding?.etContent)
            }
        }
        //输入区（获取焦点）
        binding!!.etContent.setOnFocusChangeListener { _, b ->
            if (b) {
                binding?.rlMessage7?.visibility = View.GONE
            }
        }
        //表情输入区
        binding?.ivPic1?.setOnClickListener {
            binding?.rlMessage7?.visibility = View.GONE
            nowCheckTag = 0
            emojiTag = !emojiTag
            if (emojiTag) {//先关闭展开布局》再显示表情输入区
                binding?.ivPic1?.setImageResource(R.mipmap.im_icon_send2)
                //隐藏图片输入区(只有显示的时候才隐藏，你面重复)
                if (picTag) {
                    picTag = false
                    binding?.ivPic2?.setImageResource(R.mipmap.im_icon_send3)
                    binding?.llPicView?.visibility = View.GONE
                    //显示表情输入区
                    binding?.llEmojiView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
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
                binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
            } else {
                binding?.ivPic1?.setImageResource(R.mipmap.im_icon_send1)
                //展示软件盘区
                showSoftKeyboard(binding?.etContent)
                uiScope.launch {
                    async {
                        delay(100)
                        //隐藏表情输入区
                        binding?.llEmojiView?.visibility = View.GONE
                    }
                }
            }
        }
        //图片输入区
        binding?.ivPic2?.setOnClickListener {
            binding?.rlMessage7?.visibility = View.GONE
            nowCheckTag = 0
            picTag = !picTag
            if (picTag) {//先关闭展开布局》再显示图片输入区
                binding?.ivPic2?.setImageResource(R.mipmap.im_icon_send4)
                //隐藏表情输入区(只有显示的时候才隐藏，你面重复)
                if (emojiTag) {
                    emojiTag = false
                    binding?.llEmojiView?.visibility = View.GONE
                    binding?.ivPic1?.setImageResource(R.mipmap.im_icon_send1)
                    //显示图片输入区
                    binding?.llPicView?.visibility = View.VISIBLE
                    //滚动到底部
                    binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
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
                binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
            } else {
                binding?.ivPic2?.setImageResource(R.mipmap.im_icon_send3)
                //展示软件盘区
                showSoftKeyboard(binding?.etContent)
                uiScope.launch {
                    async {
                        delay(100)
                        //隐藏图片输入区
                        binding?.llPicView?.visibility = View.GONE
                    }
                }
            }
        }
        //发送消息
        binding!!.tvTextSend.setOnClickListener {
            val sendText = binding?.etContent?.text.toString()
            if (sendText.isEmpty()) {
                return@setOnClickListener
            }
            IMChatRoomManager.sendTextMessage(sendText)
            binding?.etContent?.setText("")
        }
        //关闭发送商品消息（type=7）
        binding!!.ivClose.setOnClickListener {
            binding?.rlMessage7?.visibility = View.GONE
        }
        //发送商品（type=7）
        binding!!.tvProductSend.setOnClickListener {
            binding?.rlMessage7?.visibility = View.GONE
            IMChatRoomManager.sendCommodityMessage(arrayListOf(CommodityMessage(productId, productName, productImage, productPrice)))
        }
    }


    /**
     * Activity
     * 调用
     * 更新当前界面数据
     * isAddSend7Message:type=7的特殊消息(只有第一次》商品详情》进入（并且之前没发过）才会发送)
     */
    fun updateData(isAddSend7Message: Boolean, productId: String, productImage: String, productName: String, productPrice: String) {
        this.productId = productId
        this.productName = productName
        this.productImage = productImage
        this.productPrice = productPrice
        isFirstGetData = true
        IMChatRoomManager
                .initConversation(conversation)
                .addSGConversationListListener(object : ChatRoomCallback {
                    @Synchronized
                    override fun onReceiveMessage(message: List<SGMessage>) {
                        //发送成功，接收到消息，插入当前返回的这一条Message
                        message.forEach {
                            adapter?.addData(0, it)
                        }
                        binding?.rvConversationList?.layoutManager?.scrollToPosition(0)
                    }

                    @SuppressLint("SetTextI18n")
                    @Synchronized
                    override fun onMessages(lastItemTime: Long, message: List<SGMessage>) {
                        if (lastItemTime == Long.MAX_VALUE) {
                            adapter?.setNewData(arrayListOf())
                        }
                        //拉去数据库没聊天列表
                        adapter?.addData(message)

                        //type=7的特殊消息
                        if (isAddSend7Message && isFirstGetData) {
                            binding?.rlMessage7?.visibility = View.VISIBLE
                            binding?.tvProductPrice?.text = "￥${productPrice}"
                            binding?.tvProductName?.text = productName
                            binding?.ivProductImage?.let { IMGlideUtil.loadRound4Img(requireContext(), productImage, it, IMDensityUtils.dp2px(requireContext(), 4f)) }
                        } else {
                            binding?.rlMessage7?.visibility = View.GONE
                        }
                    }

                    @Synchronized
                    override suspend fun onMessageInProgress(message: SGMessage) {
                        //消息发送进度
                        adapter?.data?.forEachIndexed { index, sgMessage ->
                            if (sgMessage.messageId == message.messageId) {
                                sgMessage.messageBody?.sendType = message.messageBody?.sendType
                                        ?: SendType.FAIL
                                adapter?.refreshNotifyItemChanged(index)
                                return@forEachIndexed
                            }
                        }
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
        binding?.ivPic1?.setImageResource(R.mipmap.im_icon_send1)
        binding?.llEmojiView?.visibility = View.GONE
        //图片区域
        picTag = false
        binding?.ivPic2?.setImageResource(R.mipmap.im_icon_send3)
        binding?.llPicView?.visibility = View.GONE
    }

    /**
     * 控制发送按钮展示与否
     */
    private fun hideSendText() {
        val str = binding?.etContent?.text.toString()
        if (str.trim().isNotEmpty()) {
            binding?.tvTextSend?.visibility = View.VISIBLE
        } else {
            binding?.tvTextSend?.visibility = View.GONE
        }
    }

    /**
     * 退出回话
     * 摧毁时关闭软件盘
     */
    override fun onDestroy() {
        IMChatRoomManager.dismissSgMessageCallback()
        hideSoftKeyboard(binding?.etContent)
        super.onDestroy()
    }

    /**
     * 设置添加输入区（商品按钮）显示与隐藏
     */
    fun setProductMessageVisibility(tag: Boolean) {
        binding!!.llSendProduct.visibility = if (tag) View.VISIBLE else View.GONE
    }

    /**
     * 添加输入区（商品按钮）点击事件
     */
    fun addProductOnClickListener(clickListener: View.OnClickListener) {
        binding!!.llSendProduct.setOnClickListener(clickListener)
    }

    /**
     * 选择商品后，发送商品消息
     */
    fun sendProductMessage(commodityMessageS: ArrayList<CommodityMessage>) {
        IMChatRoomManager.sendCommodityMessage(commodityMessageS)
    }

    /**
     * 展示商品类型
     */
    fun sendShowProductMessage(commodityMessageS: ArrayList<CommodityMessage>) {
        IMChatRoomManager.sendCommodityMessage(commodityMessageS)
    }

    /**
     * 添加输入区（图片按钮）点击事件
     */
    fun addImageOnClickListener(clickListener: View.OnClickListener) {
        binding!!.llSendPic.setOnClickListener(clickListener)
    }

    /**
     * 选择图片后，发送图片消息
     */
    fun sendImageMessage(images: ArrayList<String>) {
        IMChatRoomManager.sendImageMessages(images)
    }
}