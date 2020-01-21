package com.beacon.location.Dbs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author Qumoy
 * Create Date 2020/1/20
 * Description：
 * Modifier:
 * Modify Date:
 * Bugzilla Id:
 * Modify Content:
 */
public class SqlHelper extends SQLiteOpenHelper {
    public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table Beacon(_id integer primary key autoincrement,name varchar(20),uuid varchar(255),x double,y double)");
        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX nn ON Beacon (name)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
