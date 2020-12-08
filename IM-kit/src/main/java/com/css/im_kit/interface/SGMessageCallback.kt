package com.css.im_kit.`interface`

import com.css.im_kit.`interface`.base.EMCallBack
import com.css.im_kit.message.SGMessage

abstract class SGMessageCallback : EMCallBack {

    override fun onSuccess() {}
    abstract fun onSuccess(message: SGMessage?, position: Int)
    override fun onError(code: Int, error: String?) {}
    override fun onProgress(progress: Int, status: String?) {}
}
