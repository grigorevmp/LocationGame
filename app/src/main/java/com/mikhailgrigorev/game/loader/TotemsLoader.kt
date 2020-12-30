package com.mikhailgrigorev.game.loader

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.TotemDBHelper
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.entities.Totem

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
            DBHelperFunctions.spawnTotems(context, totem)
        }


        // Database reading TEST
        val cursor: Cursor =
            database.query(TotemDBHelper.TABLE_TOTEM, null, null, null, null, null, null)

        val data = java.util.ArrayList<Array<String>>()

        if (cursor.moveToFirst()) {
            val indexX         : Int  = cursor.getColumnIndex(TotemDBHelper.X)
            val indexSIZE      : Int  = cursor.getColumnIndex(TotemDBHelper.SIZE)
            val indexY         : Int  = cursor.getColumnIndex(TotemDBHelper.Y)
            val indexID        : Int  = cursor.getColumnIndex(TotemDBHelper.ID)
            val indexEnemyID   : Int  = cursor.getColumnIndex(TotemDBHelper.ENEMYID)
            val indexHEALTH    : Int  = cursor.getColumnIndex(TotemDBHelper.HEALTH)
            val indexDMG       : Int  = cursor.getColumnIndex(TotemDBHelper.DMG)
            val indexAIR       : Int  = cursor.getColumnIndex(TotemDBHelper.AIR)
            val indexWATER     : Int  = cursor.getColumnIndex(TotemDBHelper.WATER)
            val indexFIRE      : Int  = cursor.getColumnIndex(TotemDBHelper.FIRE)
            val indexEARTH     : Int  = cursor.getColumnIndex(TotemDBHelper.EARTH)
            val indexDEFENCE   : Int  = cursor.getColumnIndex(TotemDBHelper.DEFENCE)
            val indexAIR2      : Int  = cursor.getColumnIndex(TotemDBHelper.AIR2)
            val indexWATER2    : Int  = cursor.getColumnIndex(TotemDBHelper.WATER2)
            val indexFIRE2     : Int  = cursor.getColumnIndex(TotemDBHelper.FIRE2)
            val indexEARTH2    : Int  = cursor.getColumnIndex(TotemDBHelper.EARTH2)
            val indexITEMS     : Int  = cursor.getColumnIndex(TotemDBHelper.ITEMS)
            val indexITEMSNUM   : Int  = cursor.getColumnIndex(TotemDBHelper.ITEMSNUM)
            do {
                data.add(
                    arrayOf(
                        cursor.getString(indexX      ),
                        cursor.getString(indexSIZE   ),
                        cursor.getString(indexY      ),
                        cursor.getString(indexID     ),
                        cursor.getString(indexEnemyID),
                        cursor.getString(indexHEALTH ),
                        cursor.getString(indexDMG    ),
                        cursor.getString(indexAIR    ),
                        cursor.getString(indexWATER  ),
                        cursor.getString(indexFIRE   ),
                        cursor.getString(indexEARTH  ),
                        cursor.getString(indexDEFENCE),
                        cursor.getString(indexAIR2   ),
                        cursor.getString(indexWATER2 ),
                        cursor.getString(indexFIRE2  ),
                        cursor.getString(indexEARTH2 ),
                        cursor.getString(indexITEMS  ),
                        cursor.getString(indexITEMSNUM ),
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
                x = totem[0].toFloat(),
                size =  totem[1].toFloat(),
                y =  Game.maxY + totem[2].toFloat(),
                id =  totem[3].toInt(),
                name = totemClass[totem[4].toInt()]!![0],
                desc = totemClass[totem[4].toInt()]!![1],
                bitmapId = context.resources.getIdentifier(totemClass[totem[4].toInt()]!![2], "drawable", context.packageName),
                group = totemClass[totem[4].toInt()]!![3],
                maxHealth = totem[5].toInt(),
                damage = totem[6].toInt(),
                damageAir = totem[7].toInt(),
                damageWater = totem[8].toInt(),
                damageFire = totem[9].toInt(),
                damageEarth = totem[10].toInt(),
                defence = totem[11].toInt(),
                defenceAir = totem[12].toInt(),
                defenceWater = totem[13].toInt(),
                defenceFire = totem[14].toInt(),
                defenceEarth = totem[15].toInt(),
                items = totem[16],
                itemsNum = totem[17],
            )
            totems.add(obj)
        }
    }


}