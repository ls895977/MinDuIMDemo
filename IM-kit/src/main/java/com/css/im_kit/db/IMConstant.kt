package com.css.im_kit.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * 配置类型
 */
// dispatches execution into Android main thread
val uiScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

// represent a pool of shared thread as coroutine dispatcher
val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)