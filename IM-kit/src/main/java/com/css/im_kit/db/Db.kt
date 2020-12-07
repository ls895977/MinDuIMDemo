package com.css.im_kit.db

import android.content.Context

fun Context.imDb(): AppDatabase {
    return AppDatabase.getInstance(this)
}
