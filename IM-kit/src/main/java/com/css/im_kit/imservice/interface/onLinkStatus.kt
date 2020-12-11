package com.css.im_kit.imservice.`interface`

interface onLinkStatus {
    /**
     * 已链接成功
     */
    fun onLinkedSuccess()

    /**
     * 链接已关闭
     */
    fun onLinkedClose()
}