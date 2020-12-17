package com.css.im_kit.http

data class BaseData<T>(
        val code: String,
        val msg: String,
        val data: T
)