package com.css.im_kit

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * 土司扩展
 */
fun String.toast11(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT)
}

fun List<Any>.log() {
    for (item in this) {
        Log.d("111", item.toString())
    }
}
