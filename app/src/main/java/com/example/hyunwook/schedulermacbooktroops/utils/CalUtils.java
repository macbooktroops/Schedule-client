package com.example.hyunwook.schedulermacbooktroops.utils;

import com.example.hyunwook.schedulermacbooktroops.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 18-06-25
 * 기타 유틸
 */
public class CalUtils {

    //curtime -> HH:mm
    public static String timeStamp2Time(long time) {
        return new SimpleDateFormat("HH:mm", Locale.KOREA).format(new Date(time));
    }


    //스케줄 볼 경우, 좌측 색깔 지정
    public static int getScheduleBlockView(int color) {
        switch (color) {
            case 0:
                return R.drawable.purple_schedule_left_block;
            case 1:
                return R.drawable.blue_schedule_left_block;
            case 2:
                return R.drawable.green_schedule_left_block;
            case 3:
                return R.drawable.pink_schedule_left_block;
            case 4:
                return R.drawable.orange_schedule_left_block;
            case 5:
                return R.drawable.yellow_schedule_left_block;
            default:
                return R.drawable.purple_schedule_left_block;
        }
//    public static int getEventSetCOlor(int color)

    }
}