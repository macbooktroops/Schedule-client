package com.example.common.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 18-06-23
 * SQLite Helper
 * 별도의 DB관리 코드를 만들어서 재사용 SQLiteOpenHelper
 * SQLiteOpenHelper 클래스는 데이터베이스를 생성하고 버전을 관리
 */
public class ScheSQLiteHelper extends SQLiteOpenHelper {

    static final String TAG = ScheSQLiteHelper.class.getSimpleName();

    public ScheSQLiteHelper(Context context) {
        // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
        super(context, ScheDBConfig.DATABASE_NAME, null, ScheDBConfig.DATABASE_VERSION);
    }

    //DB 생성

    /**
     * http://recipes4dev.tistory.com/120
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ScheDBConfig.CREATE_EVENT_SET_TABLE_SQL); //EventSet 테이블 생성
        db.execSQL(ScheDBConfig.CREATE_SCHEDULE_TABLE_SQL); //schedule 테이블 생성
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL(ScheDBConfig.DROP_EVENT_SET_TABLE_SQL);
            db.execSQL(ScheDBConfig.DROP_SCHEDULE_TABLE_SQL);
            onCreate(db);
        }
    }

}
