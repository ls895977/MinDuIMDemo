package com.css.im_kit.model.userinfo

import com.css.im_kit.db.bean.UserInfo
import java.io.Serializable

class SGUserInfo : Serializable {
    var account: String? = null
    var nickname: String? = null
    var user_type: String? = null
    var avatar: String? = null


    constructor()
    constructor(account: String?, nickname: String?, user_type: String?, avatar: String?) {
        this.account = account
        this.nickname = nickname
        this.user_type = user_type
        this.avatar = avatar
    }
    fun toDBUserInfo(): UserInfo {
        return UserInfo(
                account = account ?: "",
                nickname = nickname ?: "",
                avatar = avatar ?: "",
                user_type = user_type ?: ""
        )
    }
    companion object {
        fun format(userInfo: UserInfo?): SGUserInfo {
            return SGUserInfo(userInfo?.account, userInfo?.nickname, userInfo?.user_type, userInfo?.avatar)
        }


    }
}
