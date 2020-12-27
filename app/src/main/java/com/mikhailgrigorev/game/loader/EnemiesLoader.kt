package com.mikhailgrigorev.game.loader

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.EnemyDBHelper
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.game.Game


fun getCount(id: Int, context: Context): Int {
    var c: Cursor? = null
    val db = EnemyDBHelper(context).readableDatabase
    return try {
        val query = "select * from enemies where _id = $id"
        c = db.rawQuery(query, null)
        if (c.moveToFirst()) {
            1
        } else 0
    } finally {
        c?.close()
        db?.close()
    }
}

class EnemiesLoader(context: Context) {
    /**
     * ENEMY CLASS
     * format: EnemyID, NAME, CLASS, DESCRIPTION, BITMAP_HEADER
     *
     * +0 EnemyID
     * +1 NAME
     * +2 CLASS
     * +3 DESCRIPTION
     * +4 BITMAP_HEADER
     *
     * 0,Skeleton,skeleton,Bones,skeleton
     *
     * ENEMY DATA
     * format: EnemyID,multiple,X,Y,SIZE,ID,HEALTH,DMG,AIR,WATER,EARTH,FIRE,CC,CM,DEFENCE,AIR,WATER,EARTH,FIRE
     *
     * example:
     */

    var enemies = ArrayList<Entity>()
        private set

    private val classesFileName = "MapData/Enemies/classes.csv"
    private val dataFileName = "MapData/Enemies/data.csv"


    init{
        // Data files
        val classesData = CSVReader(context, classesFileName).data
        val dataForFirstLoad = CSVReader(context, dataFileName).data
        val data = java.util.ArrayList<IntArray>()

        // Database writing TEST
        val dbHelper = EnemyDBHelper(context)
        val database = dbHelper.writableDatabase
        val contentValues = ContentValues()
        for (enemy in dataForFirstLoad) {
            contentValues.put(EnemyDBHelper.EnemyID, enemy[0].toInt())
            contentValues.put(EnemyDBHelper.multiple, enemy[1].toInt())
            contentValues.put(EnemyDBHelper.X, enemy[2].toInt())
            contentValues.put(EnemyDBHelper.Y, enemy[3].toInt())
            contentValues.put(EnemyDBHelper.SIZE, enemy[4].toInt())
            contentValues.put(EnemyDBHelper.ID, enemy[5].toInt())
            contentValues.put(EnemyDBHelper.HEALTH, enemy[6].toInt())
            contentValues.put(EnemyDBHelper.DMG, enemy[7].toInt())
            contentValues.put(EnemyDBHelper.AIR, enemy[8].toInt())
            contentValues.put(EnemyDBHelper.WATER, enemy[9].toInt())
            contentValues.put(EnemyDBHelper.EARTH, enemy[10].toInt())
            contentValues.put(EnemyDBHelper.FIRE, enemy[11].toInt())
            contentValues.put(EnemyDBHelper.CC, enemy[12].toInt())
            contentValues.put(EnemyDBHelper.CM, enemy[13].toInt())
            contentValues.put(EnemyDBHelper.DEFENCE, enemy[14].toInt())
            contentValues.put(EnemyDBHelper.AIR2, enemy[15].toInt())
            contentValues.put(EnemyDBHelper.WATER2, enemy[16].toInt())
            contentValues.put(EnemyDBHelper.EARTH2, enemy[17].toInt())
            contentValues.put(EnemyDBHelper.FIRE2, enemy[18].toInt())
            contentValues.put(EnemyDBHelper.Special, 0)

            if (getCount(enemy[5].toInt(), context) == 0) {
                database.insert(EnemyDBHelper.TABLE_ENEMIES, null, contentValues)
            }
        }



        // Database reading TEST
        val cursor: Cursor =
            database.query(EnemyDBHelper.TABLE_ENEMIES, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            val indexEnemyID   : Int  = cursor.getColumnIndex(EnemyDBHelper.EnemyID)
            val indexmultiple  : Int  = cursor.getColumnIndex(EnemyDBHelper.multiple)
            val indexX         : Int  = cursor.getColumnIndex(EnemyDBHelper.X)
            val indexY         : Int  = cursor.getColumnIndex(EnemyDBHelper.Y)
            val indexSIZE      : Int  = cursor.getColumnIndex(EnemyDBHelper.SIZE)
            val indexID        : Int  = cursor.getColumnIndex(EnemyDBHelper.ID)
            val indexHEALTH    : Int  = cursor.getColumnIndex(EnemyDBHelper.HEALTH)
            val indexDMG       : Int  = cursor.getColumnIndex(EnemyDBHelper.DMG)
            val indexAIR       : Int  = cursor.getColumnIndex(EnemyDBHelper.AIR)
            val indexWATER     : Int  = cursor.getColumnIndex(EnemyDBHelper.WATER)
            val indexEARTH     : Int  = cursor.getColumnIndex(EnemyDBHelper.EARTH)
            val indexFIRE      : Int  = cursor.getColumnIndex(EnemyDBHelper.FIRE)
            val indexCC        : Int  = cursor.getColumnIndex(EnemyDBHelper.CC)
            val indexCM        : Int  = cursor.getColumnIndex(EnemyDBHelper.CM)
            val indexDEFENCE   : Int  = cursor.getColumnIndex(EnemyDBHelper.DEFENCE)
            val indexAIR2      : Int  = cursor.getColumnIndex(EnemyDBHelper.AIR2)
            val indexWATER2    : Int  = cursor.getColumnIndex(EnemyDBHelper.WATER2)
            val indexEARTH2    : Int  = cursor.getColumnIndex(EnemyDBHelper.EARTH2)
            val indexFIRE2     : Int  = cursor.getColumnIndex(EnemyDBHelper.FIRE2)
            val indexSpecial   : Int  = cursor.getColumnIndex(EnemyDBHelper.Special)
            do {
                data.add(
                    intArrayOf(
                        cursor.getInt(indexEnemyID),
                        cursor.getInt(indexmultiple),
                        cursor.getInt(indexX),
                        cursor.getInt(indexY),
                        cursor.getInt(indexSIZE),
                        cursor.getInt(indexID),
                        cursor.getInt(indexHEALTH),
                        cursor.getInt(indexDMG),
                        cursor.getInt(indexAIR),
                        cursor.getInt(indexWATER),
                        cursor.getInt(indexEARTH),
                        cursor.getInt(indexFIRE),
                        cursor.getInt(indexCC),
                        cursor.getInt(indexCM),
                        cursor.getInt(indexDEFENCE),
                        cursor.getInt(indexAIR2),
                        cursor.getInt(indexWATER2),
                        cursor.getInt(indexEARTH2),
                        cursor.getInt(indexFIRE2),
                        cursor.getInt(indexSpecial)
                    )
                )
            } while (cursor.moveToNext())
        } else Log.d("mLog", "0 rows")

        cursor.close()

        // Load enemy classes
        val enemyClass = mutableMapOf<Int, List<String>>()
        for(enClass in classesData)
            enemyClass[enClass[0].toInt()] = listOf(enClass[1], enClass[2], enClass[3], enClass[4])

        // Load enemy data, link it with class and create Enemy
        for (enemy in data){
            val obj = Enemy(
                context,
                _multiple = enemy[1].toInt(),
                _x = enemy[2].toFloat(),
                _y = Game.maxY + enemy[3].toFloat(),
                _size = enemy[4].toFloat(),
                _id = enemy[5].toInt(),
                _name = enemyClass[enemy[0].toInt()]!![0],
                _group = enemyClass[enemy[0].toInt()]!![1],
                _desc = enemyClass[enemy[0].toInt()]!![2],
                _bitmapId = context.resources.getIdentifier(
                    enemyClass[enemy[0].toInt()]!![3],
                    "drawable",
                    context.packageName
                ),
                _health = enemy[6].toInt(),
                _damage = enemy[7].toInt(),
                _cc = enemy[12].toInt(),
                _cm = enemy[13].toFloat(),
                _defence = enemy[14].toInt(),
                _naturalDamageValue = NatureForcesValues(
                    enemy[8].toInt(),
                    enemy[9].toInt(),
                    enemy[10].toInt(),
                    enemy[11].toInt()
                ),
                _naturalValueDef = NatureForcesValues(
                    enemy[15].toInt(),
                    enemy[16].toInt(),
                    enemy[17].toInt(),
                    enemy[18].toInt()
                )
            )
            enemies.add(obj)
        }

    }
}