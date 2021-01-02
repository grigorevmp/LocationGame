package com.mikhailgrigorev.game.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemsDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "itemsDb";
    public static final String TABLE_ITEMS = "items";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String DAMAGE = "dmg";
    public static final String COUNT = "count";
    public static final String TYPE = "type";
    public static final String EQTYPE = "eqType";
    public static final String VALUE = "value";
    public static final String ISE = "ise";

    public ItemsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_ITEMS + "("
                + ID + " integer primary key,"
                + NAME + " text,"
                + DAMAGE + " integer,"
                + COUNT + " integer,"
                + EQTYPE + " text,"
                + VALUE + " text,"
                + ISE + " integer,"
                + TYPE + " integer"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE items ADD COLUMN eqType text DEFAULT 'jewerly'");
            db.execSQL("ALTER TABLE items ADD COLUMN value text DEFAULT ''");
            db.execSQL("ALTER TABLE items ADD COLUMN ise INTEGER DEFAULT 0");
        }
    }
}