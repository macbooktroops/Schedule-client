package com.example.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.common.bean.Schedule;

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
        int row = db.delete(ScheDBConfig.SCHEDULE_TABLE_NAME, String.format("%s=?", ScheDBConfig.SCHEDULE_EVENT_SET_ID), new String[]{String.valueOf(id)});

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

}
