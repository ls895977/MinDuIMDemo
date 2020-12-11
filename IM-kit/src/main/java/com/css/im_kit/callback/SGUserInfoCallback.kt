package com.css.im_kit.callback

import com.css.im_kit.model.userinfo.SGUserInfo

interface SGUserInfoCallback {

    /**
     * 个人信息改变
     */
    fun onUserInfoChange(message: SGUserInfo)
}
