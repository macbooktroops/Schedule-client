package com.playgilround.schedule.client.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 18-06-15
 * Date 작업 클래스
 */
public class DateUtils {


    //날짜 형식 문자열로 변환 된 타임 스탬프
    public static String timeStamp2Date(long time, String format) {
        if (time == 0) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    //타임 스탬프로 변환 된 날짜 형식 문자열
    public static long date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}
