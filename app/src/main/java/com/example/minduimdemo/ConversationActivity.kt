package com.example.minduimdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.css.im_kit.IMManager
import com.css.im_kit.db.bean.CommodityMessage
import com.css.im_kit.db.uiScope
import com.css.im_kit.manager.HttpCallBack
import com.css.im_kit.manager.HttpManager
import com.css.im_kit.manager.IMChatRoomManager
import com.css.im_kit.manager.IMUserInfoManager
import com.css.im_kit.model.conversation.SGConversation
import com.css.im_kit.model.conversation.Shop
import com.css.im_kit.model.message.CommodityMessageBody
import com.css.im_kit.model.userinfo.SGUserInfo
import com.css.im_kit.ui.ConversationFragment
import com.css.im_kit.ui.base.BaseActivity
import com.css.im_kit.ui.listener.IMListener
import com.example.minduimdemo.databinding.ActivityConversationBinding
import com.example.minduimdemo.utils.ChooseCameraPicUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import kotlinx.coroutines.launch

class ConversationActivity : BaseActivity<ActivityConversationBinding>(), IMListener.SetDataListener, IMListener.SetClickProductListener {

    companion object {
        /**
         * 用户端分配客服跳转
         */
        fun toConversationActivity(context: Context, shopId: String) {
            HttpManager.assignCustomer(shopId, object : HttpCallBack {
                override fun success(shop: Shop, sgUserInfo: SGUserInfo) {
                    val userInfo = sgUserInfo.toDBUserInfo()
                    IMUserInfoManager.insertOrUpdateUser(userInfo)
                    val sgConversation = SGConversation()
                    sgConversation.shop = shop
                    sgConversation.account = IMManager.account
                    sgConversation.chat_account = sgUserInfo.account
                    sgConversation.shop_id = shop.shop_id
                    IMUserInfoManager.insertOrUpdateUser(userInfo)
                    val intent = Intent(context, ConversationActivity::class.java)
                    intent.putExtra("conversation", sgConversation)
                    context.startActivity(intent)
                }

                override fun fail() {
                    uiScope.launch {
                        Toast.makeText(context, "分配客服失败", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }
    }

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
        conversationFragment = conversation?.let { ConversationFragment(it, this, this) }
        val transaction = this@ConversationActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, conversationFragment!!)
        transaction.commit()
    }

    override fun initListeners() {}

    /**
     * Fragment初始化好了
     * 开始设置数据\监听等
     */
    override fun onSetFragmentDataListener() {
        //拉去回话数据
        conversationFragment?.updateData()
        //发送商品消息点击事件
        conversationFragment?.addProductOnClickListener(View.OnClickListener {
            val commodityMessageLists = arrayListOf<CommodityMessage>()
            commodityMessageLists.add(CommodityMessage("sdsa", "测试商品", "http://testimg.supersg.cn/user/773870855045251072.jpeg", "200"))
            conversationFragment?.sendProductMessage(commodityMessageLists)
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

    /**
     * 点击了商品消息》跳转商品详情
     */
    override fun onGoProductDetail(productMessage: CommodityMessageBody?) {
        Toast.makeText(this, "跳转商品详情", Toast.LENGTH_SHORT).show()
    }

    /**
     * 退出回话
     */
    override fun onDestroy() {
        IMChatRoomManager.dismissSgMessageCallback()
        super.onDestroy()
    }
}