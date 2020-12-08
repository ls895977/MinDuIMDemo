package com.css.im_kit.ui.bean

class UserInfoS {
    private var userId = ""
    private var userName = ""
    private var userIcon = ""

    constructor()
    constructor(userId: String, userName: String, userIcon: String) {
        this.userId = userId
        this.userName = userName
        this.userIcon = userIcon
    }
}