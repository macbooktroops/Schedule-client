package com.example.calendar.widget.calendar;

/**
 * 18-05-29
 * 캘린더 클릭 리스너 등록
 */
public interface OnCalendarClickListener {
    void onClickDate(int year, int month, int day);

    void onPageChange(int year, int month, int day);
}
