package com.example.calendar.widget.calendar;

import android.util.Log;

import java.util.Calendar;

/**
 * 18-05-28
 * 달력 작업
 */
public class CalendarUtils {

    static final String TAG = CalendarUtils.class.getSimpleName();

    /**
     * 년과 월을 통해 날짜 얻기
     *
     * @params year
     * @params month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;

        Log.d(TAG, "Month..->"  +month);
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;

            case 4:
            case 6:
            case 9:
            case 11:
                return 30;

            case 2:
                //윤년 생각해야함.
                Log.d(TAG, "year-->" + year); //2016, 2020...
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;

        }
    }


    /**
     * 요일에 현재 월 번호 1을 반환합니다.
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);//현재 요일 (일요일은 1, 토요일은 7)
    }
}
