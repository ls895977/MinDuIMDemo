package com.css.im_kit.imservice.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by zb on 2019/8/1.
 * 页面功能:
 */

public class TimeUtils {
    public static boolean isSameDay(long time1, long time2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time1);

        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTimeInMillis(time2);

        return calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public static long getDayInterval(long time1, long time2) {
        // 获得当前时区
        TimeZone tz = TimeZone.getDefault();
        // UTC毫秒加上这个偏移值，得到本地时区的时间
        long delta = tz.getRawOffset();
        long base = 24 * 3600 * 1000L;
        long day1 = (time1 + delta) / base + 1L;
        long day2 = (time2 + delta) / base + 1L;
        System.out.println(new Date(time1));
        System.out.println(new Date(time2));
        System.out.println(day1);
        System.out.println(day2);
        return (day1 - day2);
    }

    public static String timeStampToDate(long tsp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(tsp);
    }

    public static String timeStampToDateChz(long tsp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        return sdf.format(tsp);
    }

    public static String timeStampToDatePoint(long tsp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return sdf.format(tsp);
    }

    public static String timeStampToDateV2(long tsp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(tsp);
    }

    public static String timeStampToDateV3(long tsp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd", Locale.getDefault());
        return sdf.format(tsp);
    }

    public static String timeStampToDateV4(long tsp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        return sdf.format(tsp);
    }

    /**
     * 获取月和日
     * 传入格式为yyyy-MM-dd HH:mm:ss
     */
    public static String backTimeMMDDHHMM(String str1, String pattern) {
//        pattern="yyyy/MM/dd HH:mm";
        String dateString = "";
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy");
        try {
            Date date = format1.parse(str1);
            dateString = formatter.format(date);
            String YYYY = format2.format(date);//获取传入年份
            if (!YYYY.equals(getStringDateShort())) {//比对传入的年份和当前时间年是否一样
                return str1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 获取月和日
     * 传入格式为2007-1-18
     */
    public static String backTimeMMDD(String str1, String pattern) {
//        pattern="yyyy/MM/dd HH:mm";
        String dateString = "";
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy");
        try {
            Date date = format1.parse(str1);
            dateString = formatter.format(date);
            String YYYY = format2.format(date);//获取传入年份
            if (!YYYY.equals(getStringDateShort())) {//比对传入的年份和当前时间年是否一样
                return str1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 反馈时间戳
     * 传入格式为2020-09-20 12:12:12
     */
    public static long backTimeLong(String time) {
        int day = 5 * 6;
        long dateString = 0;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format1.parse(time);
            assert date != null;
            dateString = Objects.requireNonNull(format1.parse(format1.format(date))).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            try {
                Date date = format2.parse(time);
                assert date != null;
                dateString = Objects.requireNonNull(format1.parse(format1.format(date))).getTime();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        return dateString;
    }
}
