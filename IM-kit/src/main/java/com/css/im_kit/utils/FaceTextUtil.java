package com.css.im_kit.utils;


import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;

import com.css.im_kit.R;
import com.css.im_kit.ui.bean.EmojiBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.style.ImageSpan;

/**
 * Glide 帮助类
 */

public class FaceTextUtil {
    public static List<EmojiBean> faceTexts = new ArrayList<>();

    static {
        faceTexts.add(new EmojiBean("\\face_1", R.drawable.face_1));
        faceTexts.add(new EmojiBean("\\face_2", R.drawable.face_2));
        faceTexts.add(new EmojiBean("\\face_3", R.drawable.face_3));
        faceTexts.add(new EmojiBean("\\face_4", R.drawable.face_4));
        faceTexts.add(new EmojiBean("\\face_5", R.drawable.face_5));
        faceTexts.add(new EmojiBean("\\face_6", R.drawable.face_6));
        faceTexts.add(new EmojiBean("\\face_7", R.drawable.face_7));
        faceTexts.add(new EmojiBean("\\face_8", R.drawable.face_8));
        faceTexts.add(new EmojiBean("\\face_9", R.drawable.face_9));
        faceTexts.add(new EmojiBean("\\face_10", R.drawable.face_10));
        faceTexts.add(new EmojiBean("\\face_11", R.drawable.face_11));
        faceTexts.add(new EmojiBean("\\face_12", R.drawable.face_12));
        faceTexts.add(new EmojiBean("\\face_13", R.drawable.face_13));
        faceTexts.add(new EmojiBean("\\face_14", R.drawable.face_14));
        faceTexts.add(new EmojiBean("\\face_15", R.drawable.face_15));
        faceTexts.add(new EmojiBean("\\face_16", R.drawable.face_16));
        faceTexts.add(new EmojiBean("\\face_17", R.drawable.face_17));
        faceTexts.add(new EmojiBean("\\face_18", R.drawable.face_18));
        faceTexts.add(new EmojiBean("\\face_19", R.drawable.face_19));
        faceTexts.add(new EmojiBean("\\face_20", R.drawable.face_20));
        faceTexts.add(new EmojiBean("\\face_21", R.drawable.face_21));
        faceTexts.add(new EmojiBean("\\face_22", R.drawable.face_22));
        faceTexts.add(new EmojiBean("\\face_23", R.drawable.face_23));
        faceTexts.add(new EmojiBean("\\face_24", R.drawable.face_24));
        faceTexts.add(new EmojiBean("\\face_25", R.drawable.face_25));
        faceTexts.add(new EmojiBean("\\face_26", R.drawable.face_26));
        faceTexts.add(new EmojiBean("\\face_27", R.drawable.face_27));
        faceTexts.add(new EmojiBean("\\face_28", R.drawable.face_28));
        faceTexts.add(new EmojiBean("\\face_29", R.drawable.face_29));
        faceTexts.add(new EmojiBean("\\face_30", R.drawable.face_30));
        faceTexts.add(new EmojiBean("\\face_31", R.drawable.face_31));
        faceTexts.add(new EmojiBean("\\face_32", R.drawable.face_32));
        faceTexts.add(new EmojiBean("\\face_33", R.drawable.face_33));
        faceTexts.add(new EmojiBean("\\face_34", R.drawable.face_34));
        faceTexts.add(new EmojiBean("\\face_35", R.drawable.face_35));
        faceTexts.add(new EmojiBean("\\face_36", R.drawable.face_36));
        faceTexts.add(new EmojiBean("\\face_37", R.drawable.face_37));
        faceTexts.add(new EmojiBean("\\face_38", R.drawable.face_38));
        faceTexts.add(new EmojiBean("\\face_39", R.drawable.face_39));
        faceTexts.add(new EmojiBean("\\face_40", R.drawable.face_40));
        faceTexts.add(new EmojiBean("\\face_41", R.drawable.face_41));
        faceTexts.add(new EmojiBean("\\face_42", R.drawable.face_42));
        faceTexts.add(new EmojiBean("\\face_43", R.drawable.face_43));
        faceTexts.add(new EmojiBean("\\face_44", R.drawable.face_44));
        faceTexts.add(new EmojiBean("\\face_45", R.drawable.face_45));
        faceTexts.add(new EmojiBean("\\face_46", R.drawable.face_46));
        faceTexts.add(new EmojiBean("\\face_47", R.drawable.face_47));
        faceTexts.add(new EmojiBean("\\face_48", R.drawable.face_48));
        faceTexts.add(new EmojiBean("\\face_49", R.drawable.face_49));
        faceTexts.add(new EmojiBean("\\face_50", R.drawable.face_50));
        faceTexts.add(new EmojiBean("\\face_51", R.drawable.face_51));
        faceTexts.add(new EmojiBean("\\face_52", R.drawable.face_52));
        faceTexts.add(new EmojiBean("\\face_53", R.drawable.face_53));
        faceTexts.add(new EmojiBean("\\face_54", R.drawable.face_54));
        faceTexts.add(new EmojiBean("\\face_55", R.drawable.face_55));
        faceTexts.add(new EmojiBean("\\face_56", R.drawable.face_56));
        faceTexts.add(new EmojiBean("\\face_57", R.drawable.face_57));
        faceTexts.add(new EmojiBean("\\face_58", R.drawable.face_58));
        faceTexts.add(new EmojiBean("\\face_59", R.drawable.face_59));
        faceTexts.add(new EmojiBean("\\face_60", R.drawable.face_60));
        faceTexts.add(new EmojiBean("\\emotion_del_normal", R.drawable.emotion_del_normal));
        faceTexts.add(new EmojiBean("\\emotion_del_down", R.drawable.emotion_del_down));
    }

    public static String parse(String s) {
        for (EmojiBean faceText : faceTexts) {
            s = s.replace("\\" + faceText.getText(), faceText.getText());
            s = s.replace(faceText.getText(), "\\" + faceText.getText());
        }
        return s;
    }

    /**
     * toSpannableString
     *
     * @return SpannableString
     * @throws
     */
    public static SpannableString toSpannableStringList(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString spannableString = new SpannableString(text);
            int start = 0;
            Pattern pattern = Pattern.compile("\\\\face_[0-9]{1,3}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String faceText = matcher.group();
                String key = faceText.substring(1);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
                bitmap = Bitmap.createScaledBitmap(bitmap, IMDensityUtils.dp2px(context, 15f), IMDensityUtils.dp2px(context, 15f), true);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                int startIndex = text.indexOf(faceText, start);
                int endIndex = startIndex + faceText.length();
                if (startIndex >= 0)
                    spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = (endIndex - 1);
            }

            return spannableString;
        } else {
            return new SpannableString("");
        }
    }

    /**
     * toSpannableString
     *
     * @return SpannableString
     * @throws
     */
    public static SpannableString toSpannableString(Context context, String text) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString spannableString = new SpannableString(text);
            int start = 0;
            Pattern pattern = Pattern.compile("\\\\face_[0-9]{1,3}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                String faceText = matcher.group();
                String key = faceText.substring(1);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(key, "drawable", context.getPackageName()), options);
                bitmap = Bitmap.createScaledBitmap(bitmap, IMDensityUtils.dp2px(context, 20f), IMDensityUtils.dp2px(context, 20f), true);
                ImageSpan imageSpan = new ImageSpan(context, bitmap);
                int startIndex = text.indexOf(faceText, start);
                int endIndex = startIndex + faceText.length();
                if (startIndex >= 0)
                    spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = (endIndex - 1);
            }

            return spannableString;
        } else {
            return new SpannableString("");
        }
    }
}
