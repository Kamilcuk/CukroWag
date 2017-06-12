package com.example.kamil.cukrowag.food;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.kamil.cukrowag.util.Consumer1;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by kamil on 11.06.17.
 */

public class DatabaseHelper extends SQLiteAssetHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DBNAME = "bazadanych.sqlite3";

    public DatabaseHelper(Context context) {
        super(context, DBNAME, null, DATABASE_VERSION);
    }

    public static void rawQueryCursorDo(SQLiteDatabase db, String query, Consumer1<Cursor> cons) {
        final Cursor cursor = db.rawQuery(query, null);
        if ( cursor != null ) {
            if (cursor.moveToFirst()) {
                do {
                    cons.accept(cursor);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }

    static boolean checkTableExists(SQLiteDatabase db, String tableName) {
        final Cursor cursor = db.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}