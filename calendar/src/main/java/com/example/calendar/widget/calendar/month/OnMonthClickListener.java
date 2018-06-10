package com.example.calendar.widget.calendar.month;

/**
 * 18-05-26
 * Month 클릭 리스너
 */
public interface OnMonthClickListener {

    void onClickThisMonth(int year, int month, int day);
    void onClickLastMonth(int year, int month, int day);
    void onClickNextMonth(int year, int month, int day);

}
