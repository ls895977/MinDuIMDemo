package com.css.im_kit.imservice.interfacelinsterner

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