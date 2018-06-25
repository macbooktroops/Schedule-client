package com.example.common.data;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 18-06-23
 * SQLite Helper
 * 별도의 DB관리 코드를 만들어서 재사용 SQLiteOpenHelper
 */
public class ScheSQLiteHelper extends SQLiteOpenHelper {

    public ScheSQLiteHelper(Context context) {
        super(context)
    }
}
