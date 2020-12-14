package com.css.im_kit.ui

import android.app.Activity
import android.graphics.Rect
import android.util.Log
import android.view.View
import com.css.im_kit.R
import com.css.im_kit.callback.ChatRoomCallback
import com.css.im_kit.databinding.FragmentConversationBinding
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.model.message.SGMessage
import com.css.im_kit.ui.base.BaseFragment
import com.css.im_kit.ui.listener.IMListener
import com.scwang.smart.refresh.layout.api.RefreshLayout


class ConversationFragment(private var consavertionId: String, private var userInfo: UserInfo, var setDataListener: IMListener.SetDataListener) : BaseFragment<FragmentConversationBinding?>() {
    private var pic1Tag = false
    private var keyTag = false

    override fun layoutResource(): Int = R.layout.fragment_conversation

    override fun initView() {
    }

    override fun initData() {
        IMChatRoomManager
                .initConversation(consavertionId)
                .addSGConversationListListener(object : ChatRoomCallback {
                    override fun onReceiveMessage(message: SGMessage) {
                        Log.e("111", "1111")
                    }

                    override fun onMessages(message: List<SGMessage>) {
                        Log.e("111", "1111")
                    }

                    override fun onMessageInProgress(message: SGMessage) {

                    }

                })
                .create()
    }

    override fun initListeners() {
        //刷新
        binding!!.refreshView.setOnRefreshListener { refreshLayout: RefreshLayout ->
            refreshLayout.finishRefresh()
        }


        //输入区
        binding?.etContent?.setOnClickListener {
            if (pic1Tag) {
                pic1Tag = false
                binding?.llPicView?.visibility = View.GONE
            }
        }
        //输入区
        binding?.etContent?.setOnFocusChangeListener { _, b ->
            if (b) {
                if (pic1Tag) {
                    pic1Tag = false
                    binding?.llPicView?.visibility = View.GONE
                }
            }
        }
        //表情输入区
        binding?.ivPic1?.setOnClickListener {
            keyTag = isSoftShowing(requireActivity())
            if (keyTag) {
                hideSoftKeyboard()
                pic1Tag = true
                binding?.llPicView?.visibility = View.VISIBLE
            } else {
                pic1Tag = !pic1Tag
                binding?.llPicView?.visibility = if (pic1Tag) View.VISIBLE else View.GONE
            }
        }
        //图片输入区
        binding?.ivPic2?.setOnClickListener {

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

    }
}