package com.mikhailgrigorev.game.loader

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.EnemyDBHelper
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.game.Game

class EnemiesLoader(context: Context, specialSpawn:Boolean = false) {
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
        val data = java.util.ArrayList<Array<String>>()

       // Database writing TEST
       val dbHelper = EnemyDBHelper(context)
       val database = dbHelper.writableDatabase
       //val contentValues = ContentValues()
        if(specialSpawn) {
            for (enemy in dataForFirstLoad) {

                val random1: Double = (0.0001 + Math.random() * (0.0020 - 0.0001))
                val random2: Double = (0.0001 + Math.random() * (0.0020 - 0.0001))

                var multiplexer1 = 1
                if(Math.random() < 0.5)
                    multiplexer1 = -1

                var multiplexer2 = 1
                if(Math.random() < 0.5)
                    multiplexer2 = -1

                val x: Double = (random1*multiplexer1)
                val y: Double = (random2*multiplexer2)

                Log.d("DISTANCE FOR Enemies", "$x - $y")

                DBHelperFunctions.spawnEnemy(context, enemy, x, y)
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
            val indexITEMS     : Int  = cursor.getColumnIndex(EnemyDBHelper.ITEMS)
            val indexITEMSNUM  : Int  = cursor.getColumnIndex(EnemyDBHelper.ITEMSNUM)
            do {
                data.add(
                    arrayOf(
                        cursor.getString(indexEnemyID),
                        cursor.getString(indexmultiple),
                        cursor.getString(indexX),
                        cursor.getString(indexY),
                        cursor.getString(indexSIZE),
                        cursor.getString(indexID),
                        cursor.getString(indexHEALTH),
                        cursor.getString(indexDMG),
                        cursor.getString(indexAIR),
                        cursor.getString(indexWATER),
                        cursor.getString(indexEARTH),
                        cursor.getString(indexFIRE),
                        cursor.getString(indexCC),
                        cursor.getString(indexCM),
                        cursor.getString(indexDEFENCE),
                        cursor.getString(indexAIR2),
                        cursor.getString(indexWATER2),
                        cursor.getString(indexEARTH2),
                        cursor.getString(indexFIRE2),
                        cursor.getString(indexITEMS),
                        cursor.getString(indexITEMSNUM),
                        cursor.getString(indexSpecial)
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
                _x = enemy[2].toDouble(),
                _y = enemy[3].toDouble(),
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
                ),
                items = enemy[19],
                itemsNum = enemy[20]
            )
            enemies.add(obj)
        }

    }
}