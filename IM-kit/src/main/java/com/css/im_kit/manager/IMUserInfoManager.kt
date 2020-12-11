package com.css.im_kit.manager

import com.css.im_kit.callback.SGUserInfoCallback
import com.css.im_kit.db.bean.UserInfo
import com.css.im_kit.db.ioScope
import com.css.im_kit.db.repository.UserInfoRepository
import com.css.im_kit.model.userinfo.SGUserInfo
import kotlinx.coroutines.launch

object IMUserInfoManager {
    var userId: String? = null//会话id


    //回调列表
    private var sgUserInfoCallbacks = arrayListOf<SGUserInfoCallback>()

    fun addUserInfoChangeListener(sgUserInfoCallback: SGUserInfoCallback) {
        sgUserInfoCallbacks.add(sgUserInfoCallback)
    }

    fun removeUserInfoChangeListener(sgUserInfoCallback: SGUserInfoCallback) {
        sgUserInfoCallbacks.remove(sgUserInfoCallback)
    }

    /**
     * 修改本人昵称
     */
    fun changeName(userName: String) {
        userId?.let { changeName(userName, it) }
    }

    /**
     * 修改昵称
     */
    private fun changeName(userName: String, userId: String) {
        ioScope.launch {
            UserInfoRepository.changeName(userId, userName)
            noticeCallBack(userId)
        }
    }

    /**
     * 修改本人头像
     */
    fun changeAvatar(avatar: String) {
        userId?.let { changeAvatar(avatar, it) }
    }

    /**
     * 修改头像
     */
    private fun changeAvatar(avatar: String, userId: String) {
        ioScope.launch {
            UserInfoRepository.changeAvatar(userId, avatar)
            noticeCallBack(userId)
        }
    }

    /**
     * 通知用户userInfo改变
     */
    private fun noticeCallBack(userId: String) {
        ioScope.launch {
            UserInfoRepository.loadById(userId)
                    ?.let { userInfo ->
                        sgUserInfoCallbacks.forEach {
                            it.onUserInfoChange(SGUserInfo.format(userInfo))
                        }
                    }
        }
    }

    /**
     * 更新或添加用户
     */
    fun insertOrUpdateUser(userInfo: UserInfo) {
        ioScope.launch {
            UserInfoRepository.insertOrUpdateUser(userInfo)
        }
    }
}