package com.mikhailgrigorev.game.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AllItemsDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "all_itemsDb";
    public static final String TABLE_All_ITEMS = "all_items";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DESC = "_desc";
    public static final String TYPE = "type";
    public static final String RESOURCE = "res";
    public static final String EQTYPE = "eqType";

    public AllItemsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_All_ITEMS + "("
                + ID + " integer primary key,"
                + NAME + " text,"
                + DESC + " text,"
                + RESOURCE + " text,"
                + EQTYPE + " text,"
                + TYPE + " integer"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE all_items ADD COLUMN res text DEFAULT 'meat'");
        }
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE all_items ADD COLUMN eqType text DEFAULT 'null'");
        }
    }
}