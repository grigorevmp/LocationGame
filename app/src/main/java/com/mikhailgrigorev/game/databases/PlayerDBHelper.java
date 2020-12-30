package com.mikhailgrigorev.game.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlayerDBHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "playerDb";
    public static final String TABLE_PLAYER = "player";

    public static final String Speed = "speed";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String SIZE = "size";
    public static final String ID = "_id";
    public static final String HEALTH = "health";
    public static final String MAXHEALTH = "maxhealth";
    public static final String DMG = "damage";
    public static final String AIR = "airdam";
    public static final String WATER = "waterdam";
    public static final String EARTH = "earthdam";
    public static final String FIRE = "firedam";
    public static final String CC = "cc";
    public static final String CM = "cm";
    public static final String DEFENCE = "defence";
    public static final String AIR2 = "airdef";
    public static final String WATER2 = "waterdef";
    public static final String EARTH2 = "earthdef";
    public static final String FIRE2 = "firedef";
    public static final String Special = "special";
    public static final String MANNA = "manna";
    public static final String MANNAMAX = "mannamax";

    public static final String KEY_NAME = "name";
    public static final String KEY_GROUP = "_group";
    public static final String KEY_DESC = "description";
    public static final String KEY_BITMAP = "bitmap";

    public PlayerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PLAYER + "("
                + Speed + " integer,"
                + X + " integer,"
                + Y + " integer,"
                + SIZE + " integer,"
                + ID + " integer primary key,"
                + HEALTH + " integer,"
                + MAXHEALTH + " integer,"
                + DMG + " integer,"
                + AIR + " integer,"
                + WATER + " integer,"
                + EARTH + " integer,"
                + FIRE + " integer,"
                + CC + " integer,"
                + CM + " integer,"
                + DEFENCE + " integer,"
                + AIR2 + " integer,"
                + WATER2 + " integer,"
                + EARTH2 + " integer,"
                + FIRE2 + " integer,"
                + Special + " integer,"
                + KEY_NAME + " text,"
                + KEY_GROUP + " text,"
                + KEY_DESC + " text,"
                + KEY_BITMAP + " text,"
                + MANNA + " integer,"
                + MANNAMAX + " integer"
                + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE player ADD COLUMN playerMannaValue INTEGER DEFAULT 300");
        }
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE player ADD COLUMN mannamax INTEGER DEFAULT 300");
        }
        //db.execSQL("drop table if exists " + TABLE_PLAYER);
        //onCreate(db);
    }
}