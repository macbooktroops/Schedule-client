package com.example.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.common.bean.Schedule;

import java.util.ArrayList;
import java.util.List;

/**
 * 18-06-22
 * Schedule 저장 DB
 * 일단 테스트로 SQLite 이용.
 * 테스트 끝나면 바로 Realm 변경 예정.
 */
public class ScheduleDB {

    static final String TAG = ScheduleDB.class.getSimpleName();
    private ScheSQLiteHelper mHelper;

    private ScheduleDB(Context context) {
        mHelper = new ScheSQLiteHelper(context);
    }

    public static ScheduleDB getInstance(Context context) {
        return new ScheduleDB(context);
    }

    public int addSchedule(Schedule schedule) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getWritableDatabase();

        //테이블에 데이터삽입
        //(필드명, 데이터)
        ContentValues values = new ContentValues();
        values.put(ScheDBConfig.SCHEDULE_TITLE, schedule.getTitle());
        values.put(ScheDBConfig.SCHEDULE_COLOR, schedule.getColor());
        values.put(ScheDBConfig.SCHEDULE_DESC, schedule.getDesc());
        values.put(ScheDBConfig.SCHEDULE_STATE, schedule.getState());
        values.put(ScheDBConfig.SCHEDULE_LOCATION, schedule.getLocation());
        values.put(ScheDBConfig.SCHEDULE_TIME, schedule.getTime());
        values.put(ScheDBConfig.SCHEDULE_YEAR, schedule.getYear());
        values.put(ScheDBConfig.SCHEDULE_MONTH, schedule.getMonth());
        values.put(ScheDBConfig.SCHEDULE_DAY, schedule.getDay());
        values.put(ScheDBConfig.SCHEDULE_EVENT_SET_ID, schedule.getEventSetId());

        long row = db.insert(ScheDBConfig.SCHEDULE_TABLE_NAME, null, values); //insert
        db.close();

        Log.d(TAG, "add row -->" + row);
        return row > 0 ? getLastScheduleId() : 0;
    }

    private int getLastScheduleId() {
        //읽기가 가능하게 DB 열기
        SQLiteDatabase db = mHelper.getReadableDatabase();
        //DB에 있는 데이터를 쉽게 처리하기 위해
        // Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.query(ScheDBConfig.SCHEDULE_TABLE_NAME, null, null, null, null, null, null, null);

        int id = 0;

        //Cursor를 마지막 행(Row)으로 이동 시킨다.
        if (cursor.moveToLast()) {

            //DB 테이블의 해당 필드(컬럼) 이름을 얻어 옵니다.
            id = cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_ID));
        }

        cursor.close();
        db.close();
        mHelper.close();
        return id;

    }

    //삭제
    public boolean removeSchedule(long id) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int row = db.delete(ScheDBConfig.SCHEDULE_TABLE_NAME, String.format("%s=?", ScheDBConfig.SCHEDULE_ID), new String[]{String.valueOf(id)});

        db.close();
        mHelper.close();
        return row != 0;
    }

    //업데이트
    public boolean updateSchedule(Schedule schedule) {
        SQLiteDatabase db = mHelper.getWritableDatabase(); //읽고 쓰기
        ContentValues values = new ContentValues();
        values.put(ScheDBConfig.SCHEDULE_TITLE, schedule.getTitle());
        values.put(ScheDBConfig.SCHEDULE_COLOR, schedule.getColor());
        values.put(ScheDBConfig.SCHEDULE_DESC, schedule.getDesc());
        values.put(ScheDBConfig.SCHEDULE_STATE, schedule.getState());
        values.put(ScheDBConfig.SCHEDULE_LOCATION, schedule.getLocation());
        values.put(ScheDBConfig.SCHEDULE_YEAR, schedule.getYear());
        values.put(ScheDBConfig.SCHEDULE_MONTH, schedule.getMonth());
        values.put(ScheDBConfig.SCHEDULE_TIME, schedule.getTime());
        values.put(ScheDBConfig.SCHEDULE_DAY, schedule.getDay());
        values.put(ScheDBConfig.SCHEDULE_EVENT_SET_ID, schedule.getEventSetId());

        int row = db.update(ScheDBConfig.SCHEDULE_TABLE_NAME, values, String.format("%s=?", ScheDBConfig.SCHEDULE_ID), new String[]{String.valueOf(schedule.getId())});
        db.close();
        mHelper.close();

        return row > 0;
    }

    public void removeScheduleByEventSetId(int id) {
        SQLiteDatabase db = mHelper.getWritableDatabase(); //쓰기 가능
        db.delete(ScheDBConfig.SCHEDULE_TABLE_NAME, String.format("%s=?", ScheDBConfig.SCHEDULE_EVENT_SET_ID), new String[]{String.valueOf(id)});
        db.close();
        mHelper.close();
    }


    //해당되는 연월일에 스케줄 얻기
    public List<Schedule> getScheduleByDate(int year, int month, int day) {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase(); //읽기
        Cursor cursor = db.query(ScheDBConfig.SCHEDULE_TABLE_NAME, null,
                String.format("%s=? and %s=? and %s=?", ScheDBConfig.SCHEDULE_YEAR,
                        ScheDBConfig.SCHEDULE_MONTH, ScheDBConfig.SCHEDULE_DAY),
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)}, null, null, null);

        Schedule schedule;

        while (cursor.moveToNext()) {
            schedule = new Schedule();
            schedule.setId(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_ID)));
            schedule.setColor(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_COLOR)));
            schedule.setTitle(cursor.getString(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_TITLE)));
            schedule.setLocation(cursor.getString(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_LOCATION)));
            schedule.setDesc(cursor.getString(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_DESC)));
            schedule.setState(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_STATE)));
            schedule.setYear(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_YEAR)));
            schedule.setMonth(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_MONTH)));
            schedule.setDay(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_DAY)));
            schedule.setTime(cursor.getLong(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_TIME)));
            schedule.setEventSetId(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_EVENT_SET_ID)));
            schedules.add(schedule);
        }

        cursor.close();
        db.close();
        mHelper.close();
        return schedules;
    }

    //id에 따른 스케줄 정보얻기.
    public List<Schedule> getScheduleByEventSetId(int id) {
        List<Schedule> schedules = new ArrayList<>();

        SQLiteDatabase db = mHelper.getReadableDatabase(); //쓰기만 허용
        Cursor cursor = db.query(ScheDBConfig.SCHEDULE_TABLE_NAME, null,
                String.format("%s=?", ScheDBConfig.SCHEDULE_EVENT_SET_ID), new String[]{String.valueOf(id)}, null, null, null);

        Schedule schedule;

        while (cursor.moveToNext()) { //자료있을때까지 계속.
            schedule = new Schedule();
            schedule.setId(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_ID)));
            schedule.setColor(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_COLOR)));
            schedule.setTitle(cursor.getString(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_TITLE)));
            schedule.setDesc(cursor.getString(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_DESC)));
            schedule.setLocation(cursor.getString(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_LOCATION)));
            schedule.setState(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_STATE)));
            schedule.setYear(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_YEAR)));
            schedule.setMonth(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_MONTH)));
            schedule.setDay(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_DAY)));
            schedule.setTime(cursor.getLong(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_TIME)));
            schedule.setEventSetId(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.SCHEDULE_EVENT_SET_ID)));
            schedules.add(schedule);
        }
        cursor.close();
        db.close();
        mHelper.close();
        return schedules;

    }

    //해당연월 힌트?얻기 .
    public List<Integer> getTaskHintByMonth(int year, int month) {
        List<Integer> taskHint = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase(); //읽기
        Cursor cursor = db.query(ScheDBConfig.SCHEDULE_TABLE_NAME, new String[]{ScheDBConfig.SCHEDULE_DAY},
                String.format("%s=? and %s=?", ScheDBConfig.SCHEDULE_YEAR,
                        ScheDBConfig.SCHEDULE_MONTH), new String[] {String.valueOf(year), String.valueOf(month)}, null, null, null);

        while (cursor.moveToNext()) {
            taskHint.add(cursor.getInt(0));
        }
        cursor.close();
        db.close();
        mHelper.close();
        return taskHint;

    }

}
