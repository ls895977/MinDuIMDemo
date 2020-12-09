package com.css.im_kit.callback

interface OnCallBack<T> {
    fun onSuccess(models: T)
    fun onError(code: Int, error: String?)
}
