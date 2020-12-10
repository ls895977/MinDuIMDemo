package com.css.im_kit.model.userinfo

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

}
