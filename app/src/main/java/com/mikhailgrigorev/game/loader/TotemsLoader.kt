package com.mikhailgrigorev.game.loader

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.EnemyDBHelper
import com.mikhailgrigorev.game.databases.TotemDBHelper
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.map.Totem

class TotemsLoader(context: Context) {
    /**
     * format: x,size,y,id,name,description,bitmapID,group
     * sample: 4f,2f,-25,0,God,Description,grave,totem
     */

    var totems = ArrayList<Entity>()
        private set

    private val fileName = "MapData/Totems/data.csv"
    private val classesFileName = "MapData/Totems/classes.csv"


    init{

        val dataForFirstLoad = CSVReader(context, fileName).data
        val classesData = CSVReader(context, classesFileName).data


        // Database writing TEST
        val dbHelper = TotemDBHelper(context)
        val database = dbHelper.writableDatabase
        for (totem in dataForFirstLoad) {
            DBHelperFunctions().spawnTotems(context, totem)
        }


        // Database reading TEST
        val cursor: Cursor =
            database.query(TotemDBHelper.TABLE_TOTEM, null, null, null, null, null, null)

        val data = java.util.ArrayList<IntArray>()

        if (cursor.moveToFirst()) {
            val indexX         : Int  = cursor.getColumnIndex(EnemyDBHelper.X)
            val indexSIZE      : Int  = cursor.getColumnIndex(EnemyDBHelper.SIZE)
            val indexY         : Int  = cursor.getColumnIndex(EnemyDBHelper.Y)
            val indexID        : Int  = cursor.getColumnIndex(EnemyDBHelper.ID)
            val indexEnemyID   : Int  = cursor.getColumnIndex(EnemyDBHelper.EnemyID)
            val indexHEALTH    : Int  = cursor.getColumnIndex(EnemyDBHelper.HEALTH)
            val indexDMG       : Int  = cursor.getColumnIndex(EnemyDBHelper.DMG)
            val indexAIR       : Int  = cursor.getColumnIndex(EnemyDBHelper.AIR)
            val indexWATER     : Int  = cursor.getColumnIndex(EnemyDBHelper.WATER)
            val indexFIRE      : Int  = cursor.getColumnIndex(EnemyDBHelper.FIRE)
            val indexEARTH     : Int  = cursor.getColumnIndex(EnemyDBHelper.EARTH)
            val indexDEFENCE   : Int  = cursor.getColumnIndex(EnemyDBHelper.DEFENCE)
            val indexAIR2      : Int  = cursor.getColumnIndex(EnemyDBHelper.AIR2)
            val indexWATER2    : Int  = cursor.getColumnIndex(EnemyDBHelper.WATER2)
            val indexFIRE2     : Int  = cursor.getColumnIndex(EnemyDBHelper.FIRE2)
            val indexEARTH2    : Int  = cursor.getColumnIndex(EnemyDBHelper.EARTH2)
            do {
                data.add(
                    intArrayOf(
                        cursor.getInt(indexX      ),
                        cursor.getInt(indexSIZE   ),
                        cursor.getInt(indexY      ),
                        cursor.getInt(indexID     ),
                        cursor.getInt(indexEnemyID),
                        cursor.getInt(indexHEALTH ),
                        cursor.getInt(indexDMG    ),
                        cursor.getInt(indexAIR    ),
                        cursor.getInt(indexWATER  ),
                        cursor.getInt(indexFIRE   ),
                        cursor.getInt(indexEARTH  ),
                        cursor.getInt(indexDEFENCE),
                        cursor.getInt(indexAIR2   ),
                        cursor.getInt(indexWATER2 ),
                        cursor.getInt(indexFIRE2  ),
                        cursor.getInt(indexEARTH2 ),
                    )
                )
            } while (cursor.moveToNext())
        } else Log.d("mLog", "0 rows")

        cursor.close()


        val totemClass = mutableMapOf<Int, List<String>>()
        for(tClass in classesData)
            totemClass[tClass[0].toInt()] = listOf(tClass[1], tClass[2], tClass[3], tClass[4])

        for (totem in data){
            val obj = Totem(
                context,
                _x = totem[0].toFloat(),
                _size =  totem[1].toFloat(),
                _y =  Game.maxY + totem[2].toFloat(),
                _id =  totem[3],
                _name = totemClass[totem[4]]!![0],
                _desc = totemClass[totem[4]]!![1],
                _bitmapId = context.resources.getIdentifier(totemClass[totem[4]]!![2], "drawable", context.packageName),
                _group = totemClass[totem[4]]!![3],
                _health = totem[5],
                _damage = totem[6],
                _damageAir = totem[7],
                _damageWater = totem[8],
                _damageFire = totem[9],
                _damageEarth = totem[10],
                _defence = totem[11],
                _defenceAir = totem[12],
                _defenceWater = totem[13],
                _defenceFire = totem[14],
                _defenceEarth = totem[15],
            )
            totems.add(obj)
        }
    }


}