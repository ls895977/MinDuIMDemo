package com.css.im_kit.model.userinfo

import com.css.im_kit.db.bean.User_Info

class SGUserInfo {
    var userId: String? = null
    var userName: String? = null
    var avatar: String? = null

    constructor(userId: String?, userName: String?, avatar: String?) {
        this.userId = userId
        this.userName = userName
        this.avatar = avatar
    }

    constructor()

    companion object {
        fun format(userInfo: User_Info?): SGUserInfo {
            return SGUserInfo(userInfo?.userId, userInfo?.nickName, userInfo?.avatar)
        }
    }
}
