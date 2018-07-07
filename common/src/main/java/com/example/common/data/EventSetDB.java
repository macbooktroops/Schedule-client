package com.example.common.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.common.bean.EventSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 18-06-30
 * 좌측 카테고리 이벤트 저장 클래
 */
public class EventSetDB {
    private ScheSQLiteHelper mHelper;

    private EventSetDB(Context context) {
        mHelper = new ScheSQLiteHelper(context);
    }

    public static EventSetDB getInstance(Context context) {
        return new EventSetDB(context);
    }

    //EventSet 스케줄 분류 추가
    public int addEventSet(EventSet eventSet) {
        SQLiteDatabase db = mHelper.getWritableDatabase(); //쓰기 가능
        ContentValues values = new ContentValues();
        values.put(ScheDBConfig.EVENT_SET_NAME, eventSet.getName());
        values.put(ScheDBConfig.EVENT_SET_COLOR, eventSet.getColor());
        values.put(ScheDBConfig.EVENT_SET_ICON, eventSet.getIcon());

        long row = db.insert(ScheDBConfig.EVENT_SET_TABLE_NAME, null, values);
        db.close();

        return row > 0 ? getLastEventSetId() : 0;
    }
    //EventSet 저장된 정보를 Map 추가.
    public Map<Integer, EventSet> getAllEventSetMap() {
        Map<Integer, EventSet> eventSets = new HashMap<>();
        SQLiteDatabase db = mHelper.getReadableDatabase(); //읽기
        Cursor cursor = db.query(ScheDBConfig.EVENT_SET_TABLE_NAME, null, null, null, null, null, null);
        EventSet eventSet;

        while (cursor.moveToNext()) {
            eventSet = new EventSet();
            eventSet.setId(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_ID)));
            eventSet.setName(cursor.getString(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_NAME)));
            eventSet.setColor(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_COLOR)));
            eventSet.setIcon(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_ICON)));
            eventSets.put(eventSet.getId(), eventSet);
        }
        cursor.close();
        db.close();
        mHelper.close();
        return eventSets;
    }


    //EventSet 저장된 정보얻기
    public List<EventSet> getAllEventSet() {
        List<EventSet> eventSets = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase(); //읽기

        Cursor cursor = db.query(ScheDBConfig.EVENT_SET_TABLE_NAME, null, null, null, null, null, null);
        EventSet eventSet;

        //저장된 이벤트 셋 모두 얻
        while (cursor.moveToNext()) {
            eventSet = new EventSet();
            eventSet.setId(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_ID)));
            eventSet.setName(cursor.getString(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_NAME)));
            eventSet.setColor(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_COLOR)));
            eventSet.setIcon(cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_ICON)));
            eventSets.add(eventSet);
        }
        cursor.close();
        db.close();
        mHelper.close();
        return eventSets;

    }

    //제일 마지막에 저장된 이벤트 id얻기
    private int getLastEventSetId() {
        SQLiteDatabase db = mHelper.getReadableDatabase(); //읽기 가능
        Cursor cursor = db.query(ScheDBConfig.EVENT_SET_TABLE_NAME, null, null, null, null, null, null);

        int id = 0;
        if (cursor.moveToLast()) {
            id = cursor.getInt(cursor.getColumnIndex(ScheDBConfig.EVENT_SET_ID));
        }

        cursor.close();
        db.close();
        mHelper.close();
        return id;
    }
}
