package com.css.im_kit.callback.base

/**
 * \~chinese
 * 通用的回调函数接口
 *
 * \~english
 * The general callback interface
 */
interface EMCallBack {
    /**
     * \~chinese
     * 程序执行成功时执行回调函数。
     *
     * \~english
     *
     */
    fun onSuccess()

    /**
     * \~chinese
     * 发生错误时调用的回调函数  @see EMError
     *
     * @param code           错误代码
     * @param error          包含文本类型的错误描述。
     *
     * \~english
     * Callback when encounter error @see EMError
     *
     * @param code           error code
     * @param error          contain description of error
     */
    fun onError(code: Int, error: String?)

    /**
     * \~chinese
     * 刷新进度的回调函数
     *
     * @param progress       进度信息
     * @param status         包含文件描述的进度信息, 如果SDK没有提供，结果可能是"", 或者null。
     *
     * \~english
     * callback for progress update
     *
     * @param progress
     * @param status         contain progress description. May be empty string "" or null.
     */
    fun onProgress(progress: Int, status: String?)
}