package com.css.im_kit.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ImageSpan
import com.css.im_kit.R
import com.css.im_kit.ui.bean.EmojiBean
import java.util.*
import java.util.regex.Pattern

/**
 * Glide 帮助类
 */
object FaceTextUtil {
    var faceTexts: MutableList<EmojiBean> = ArrayList()

    /**
     * toSpannableString
     * 聊天列表使用
     */
    fun toSpannableStringList(context: Context, text: String): SpannableString {
        return if (!TextUtils.isEmpty(text)) {
            val spannableString = SpannableString(text)
            var start = 0
//            val pattern = Pattern.compile("#face_[0-9]{1,3}#", Pattern.CASE_INSENSITIVE)
            val pattern = Pattern.compile("\\[face_[0-9]{1,3}]", Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val faceText = matcher.group()
                val key = faceText.replace("\\[".toRegex(), "").replace("]".toRegex(), "")
                val options = BitmapFactory.Options()
                var bitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier(key, "drawable", context.packageName), options)
                bitmap = Bitmap.createScaledBitmap(bitmap!!, IMDensityUtils.dp2px(context, 15f), IMDensityUtils.dp2px(context, 15f), true)
                val imageSpan = ImageSpan(context, bitmap)
                val startIndex = text.indexOf(faceText, start)
                val endIndex = startIndex + faceText.length
                if (startIndex >= 0) spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = endIndex - 1
            }
            spannableString
        } else {
            SpannableString("")
        }
    }

    /**
     * toSpannableString
     * 回话列表使用
     */
    fun toSpannableString(context: Context, text: String): SpannableString {
        return if (!TextUtils.isEmpty(text)) {
            val spannableString = SpannableString(text)
            var start = 0
//            val pattern = Pattern.compile("#face_[0-9]{1,3}#", Pattern.CASE_INSENSITIVE)
            val pattern = Pattern.compile("\\[face_[0-9]{1,3}]", Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val faceText = matcher.group()
                val key = faceText.replace("\\[".toRegex(), "").replace("]".toRegex(), "")
                val options = BitmapFactory.Options()
                var bitmap = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier(key, "drawable", context.packageName), options)
                bitmap = Bitmap.createScaledBitmap(bitmap!!, IMDensityUtils.dp2px(context, 20f), IMDensityUtils.dp2px(context, 20f), true)
                val imageSpan = ImageSpan(context, bitmap)
                val startIndex = text.indexOf(faceText, start)
                val endIndex = startIndex + faceText.length
                if (startIndex >= 0) spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = endIndex - 1
            }
            spannableString
        } else {
            SpannableString("")
        }
    }

    init {
        faceTexts.add(EmojiBean("[face_1]", R.drawable.face_1))
        faceTexts.add(EmojiBean("[face_2]", R.drawable.face_2))
        faceTexts.add(EmojiBean("[face_3]", R.drawable.face_3))
        faceTexts.add(EmojiBean("[face_4]", R.drawable.face_4))
        faceTexts.add(EmojiBean("[face_5]", R.drawable.face_5))
        faceTexts.add(EmojiBean("[face_6]", R.drawable.face_6))
        faceTexts.add(EmojiBean("[face_7]", R.drawable.face_7))
        faceTexts.add(EmojiBean("[face_8]", R.drawable.face_8))
        faceTexts.add(EmojiBean("[face_9]", R.drawable.face_9))
        faceTexts.add(EmojiBean("[face_10]", R.drawable.face_10))
        faceTexts.add(EmojiBean("[face_11]", R.drawable.face_11))
        faceTexts.add(EmojiBean("[face_12]", R.drawable.face_12))
        faceTexts.add(EmojiBean("[face_13]", R.drawable.face_13))
        faceTexts.add(EmojiBean("[face_14]", R.drawable.face_14))
        faceTexts.add(EmojiBean("[face_15]", R.drawable.face_15))
        faceTexts.add(EmojiBean("[face_16]", R.drawable.face_16))
        faceTexts.add(EmojiBean("[face_17]", R.drawable.face_17))
        faceTexts.add(EmojiBean("[face_18]", R.drawable.face_18))
        faceTexts.add(EmojiBean("[face_19]", R.drawable.face_19))
        faceTexts.add(EmojiBean("[face_20]", R.drawable.face_20))
        faceTexts.add(EmojiBean("[face_21]", R.drawable.face_21))
        faceTexts.add(EmojiBean("[face_22]", R.drawable.face_22))
        faceTexts.add(EmojiBean("[face_23]", R.drawable.face_23))
        faceTexts.add(EmojiBean("[face_24]", R.drawable.face_24))
        faceTexts.add(EmojiBean("[face_25]", R.drawable.face_25))
        faceTexts.add(EmojiBean("[face_26]", R.drawable.face_26))
        faceTexts.add(EmojiBean("[face_27]", R.drawable.face_27))
        faceTexts.add(EmojiBean("[face_28]", R.drawable.face_28))
        faceTexts.add(EmojiBean("[face_29]", R.drawable.face_29))
        faceTexts.add(EmojiBean("[face_30]", R.drawable.face_30))
        faceTexts.add(EmojiBean("[face_31]", R.drawable.face_31))
        faceTexts.add(EmojiBean("[face_32]", R.drawable.face_32))
        faceTexts.add(EmojiBean("[face_33]", R.drawable.face_33))
        faceTexts.add(EmojiBean("[face_34]", R.drawable.face_34))
        faceTexts.add(EmojiBean("[face_35]", R.drawable.face_35))
        faceTexts.add(EmojiBean("[face_36]", R.drawable.face_36))
        faceTexts.add(EmojiBean("[face_37]", R.drawable.face_37))
        faceTexts.add(EmojiBean("[face_38]", R.drawable.face_38))
        faceTexts.add(EmojiBean("[face_39]", R.drawable.face_39))
        faceTexts.add(EmojiBean("[face_40]", R.drawable.face_40))
        faceTexts.add(EmojiBean("[face_41]", R.drawable.face_41))
        faceTexts.add(EmojiBean("[face_42]", R.drawable.face_42))
        faceTexts.add(EmojiBean("[face_43]", R.drawable.face_43))
        faceTexts.add(EmojiBean("[face_44]", R.drawable.face_44))
        faceTexts.add(EmojiBean("[face_45]", R.drawable.face_45))
        faceTexts.add(EmojiBean("[face_46]", R.drawable.face_46))
        faceTexts.add(EmojiBean("[face_47]", R.drawable.face_47))
        faceTexts.add(EmojiBean("[face_48]", R.drawable.face_48))
        faceTexts.add(EmojiBean("[face_49]", R.drawable.face_49))
        faceTexts.add(EmojiBean("[face_50]", R.drawable.face_50))
        faceTexts.add(EmojiBean("[face_51]", R.drawable.face_51))
        faceTexts.add(EmojiBean("[face_52]", R.drawable.face_52))
        faceTexts.add(EmojiBean("[face_53]", R.drawable.face_53))
        faceTexts.add(EmojiBean("[face_54]", R.drawable.face_54))
        faceTexts.add(EmojiBean("[face_55]", R.drawable.face_55))
        faceTexts.add(EmojiBean("[face_56]", R.drawable.face_56))
        faceTexts.add(EmojiBean("[face_57]", R.drawable.face_57))
        faceTexts.add(EmojiBean("[face_58]", R.drawable.face_58))
        faceTexts.add(EmojiBean("[face_59]", R.drawable.face_59))
        faceTexts.add(EmojiBean("[face_60]", R.drawable.face_60))
    }
}