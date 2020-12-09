package com.css.im_kit.callback

import com.css.im_kit.callback.base.EMCallBack
import com.css.im_kit.model.message.SGMessage

abstract class SGMessageCallback : EMCallBack {

    override fun onSuccess() {}
    abstract fun onSuccess(message: SGMessage?, position: Int)
    override fun onError(code: Int, error: String?) {}
    override fun onProgress(progress: Int, status: String?) {}
}
