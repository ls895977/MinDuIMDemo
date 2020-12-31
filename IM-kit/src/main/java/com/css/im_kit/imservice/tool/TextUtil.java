package com.css.im_kit.imservice.tool;

public class TextUtil {

    public static boolean isNotNullOrEmpty(String s) {
        return !isNullOrEmpty(s);
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String judgeNull(String s) {
        return judgeNull(s, "");
    }

    public static String judgeNull(String s, String defaultStr) {
        return isNullOrEmpty(s) ? defaultStr : s;
    }

}
