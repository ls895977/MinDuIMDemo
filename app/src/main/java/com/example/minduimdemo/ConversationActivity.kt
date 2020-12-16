package com.example.minduimdemo

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.ui.ConversationFragment
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.ui.listener.IMListener
import com.example.minduimdemo.databinding.ActivityConversationBinding
import com.example.minduimdemo.utils.ChooseCameraPicUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig

class ConversationActivity : BaseActivity<ActivityConversationBinding>(), IMListener.SetDataListener {
    private var conversation: SGConversation? = null
    private var conversationFragment: ConversationFragment? = null

    override fun layoutResource(): Int = R.layout.activity_conversation

    override fun initView() {
        conversation = intent.getSerializableExtra("conversation") as SGConversation?
        if (conversation?.shop != null) {
            binding.nickName = conversation?.shop?.shop_name
        } else {
            binding.nickName = conversation?.chat_account_info?.nickname
        }

    }

    override fun initData() {
        conversationFragment = conversation?.let { ConversationFragment(it, this) }
        val transaction = this@ConversationActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationFragment!!)
        transaction.commit()
    }

    override fun initListeners() {
    }

    /**
     * Fragment初始化好了
     * 开始设置数据\监听等
     */
    override fun onSetFragmentDataListener() {
        //拉去回话数据
        conversationFragment?.updateData()
        //发送商品消息点击事件
        conversationFragment?.addProductOnClickListener(View.OnClickListener {
            //发送商品消息（选择商品等后续逻辑） TODO
            conversationFragment?.sendProductMessage(CommodityMessage("sdsa", "测试商品", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "200"))
        })
        conversationFragment?.addImageOnClickListener(View.OnClickListener {
            ChooseCameraPicUtil.choosePic(this)
        })
    }

    /**
     * 图片选择回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    val localMedias = PictureSelector.obtainMultipleResult(data)
                    val imageUrlList = arrayListOf<String>()
                    for (localMedia in localMedias) {
                        var path = ""
                        if (!localMedia.cutPath.isNullOrEmpty()) {
                            path = localMedia.cutPath
                        } else if (!localMedia.compressPath.isNullOrEmpty()) {
                            path = localMedia.compressPath
                        } else if (!localMedia.androidQToPath.isNullOrEmpty()) {
                            path = localMedia.androidQToPath
                        } else if (!localMedia.realPath.isNullOrEmpty()) {
                            path = localMedia.realPath
                        } else if (!localMedia.path.isNullOrEmpty()) {
                            path = localMedia.path
                        }
                        if (path.isNotEmpty()) {
                            imageUrlList.add(path)
                        }
                    }
                    //发送图片消息（选择商品等后续逻辑）
                    if (!imageUrlList.isNullOrEmpty()) {
                        conversationFragment?.sendImageMessage(imageUrlList)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        IMChatRoomManager.dismissSgMessageCallback()
        Toast.makeText(this, "退出会话", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }


}