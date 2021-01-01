package com.mikhailgrigorev.game.loader

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.databases.ItemsDB
import com.mikhailgrigorev.game.databases.PlayerDBHelper

fun getCountPlayer(id: Int, context: Context): Int {
    var c: Cursor? = null
    val db = PlayerDBHelper(context).readableDatabase
    return try {
        val query = "select * from player where _id = $id"
        c = db.rawQuery(query, null)
        if (c.moveToFirst()) {
            1
        } else 0
    } finally {
        c?.close()
        db?.close()
    }
}

class PlayerLoader(context: Context) {
    /**
     * format: x,size,yOffset,id,name,description,bitmapID,group,speed
     * 3f,7f,1,666,Player,Desc,player,player,0.2f
     */

    private val fileName = "MapData/player.csv"

    var size = 3f
        private set
    var x = 7f
        private set
    var yOffset = 1
        private set
    var id = 0
        private set
    var name = "Player"
        private set
    var desc = "Desc"
        private set
    var bitmapId = R.drawable.player
        private set
    var group = "player"
        private set
    var speed = 0.2f
        private set

    var health = 0
        private set
    var damage = 0
        private set
    var defence = 0
        private set
    var cc = 0
        private set
    var cm = 0f
        private set
    var maxhealth = 0
        private set
    var manna = 0
        private set
    var mannamax = 0
        private set
    var naturalDamageValue: NatureForcesValues = NatureForcesValues(0,0,0,0)
        private set
    var naturalValueDef: NatureForcesValues = NatureForcesValues(0,0,0,0)
        private set
    init{

        ItemsDB.init(context)
        val dataForFirstLoad = CSVReader(context, fileName).data
        val data = java.util.ArrayList<Array<String>>()

        val dbHelper = PlayerDBHelper(context)
        val database = dbHelper.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(PlayerDBHelper.SIZE       ,       dataForFirstLoad[0][0].toInt())
        contentValues.put(PlayerDBHelper.X          ,       dataForFirstLoad[0][1].toInt())
        contentValues.put(PlayerDBHelper.Y          ,       dataForFirstLoad[0][2].toInt())
        contentValues.put(PlayerDBHelper.ID         ,       dataForFirstLoad[0][3].toInt())
        contentValues.put(PlayerDBHelper.KEY_NAME   ,       dataForFirstLoad[0][4])
        contentValues.put(PlayerDBHelper.KEY_DESC   ,       dataForFirstLoad[0][5])
        contentValues.put(PlayerDBHelper.KEY_BITMAP ,       dataForFirstLoad[0][6])
        contentValues.put(PlayerDBHelper.KEY_GROUP  ,       dataForFirstLoad[0][7])
        contentValues.put(PlayerDBHelper.Speed      ,       dataForFirstLoad[0][8].toInt())
        contentValues.put(PlayerDBHelper.HEALTH     ,       dataForFirstLoad[0][9].toInt())
        contentValues.put(PlayerDBHelper.DMG        ,       dataForFirstLoad[0][10].toInt())
        contentValues.put(PlayerDBHelper.DEFENCE    ,       dataForFirstLoad[0][11].toInt())
        contentValues.put(PlayerDBHelper.CC         ,       dataForFirstLoad[0][12].toInt())
        contentValues.put(PlayerDBHelper.CM         ,       dataForFirstLoad[0][13].toInt())
        contentValues.put(PlayerDBHelper.AIR        ,       dataForFirstLoad[0][14].toInt())
        contentValues.put(PlayerDBHelper.WATER      ,       dataForFirstLoad[0][15].toInt())
        contentValues.put(PlayerDBHelper.EARTH      ,       dataForFirstLoad[0][16].toInt())
        contentValues.put(PlayerDBHelper.FIRE       ,       dataForFirstLoad[0][17].toInt())
        contentValues.put(PlayerDBHelper.AIR2       ,       dataForFirstLoad[0][18].toInt())
        contentValues.put(PlayerDBHelper.WATER2     ,       dataForFirstLoad[0][19].toInt())
        contentValues.put(PlayerDBHelper.EARTH2     ,       dataForFirstLoad[0][20].toInt())
        contentValues.put(PlayerDBHelper.FIRE2      ,       dataForFirstLoad[0][21].toInt())
        contentValues.put(PlayerDBHelper.Special    ,       0)
        contentValues.put(PlayerDBHelper.MANNA      ,       dataForFirstLoad[0][23].toInt())
        contentValues.put(PlayerDBHelper.MANNAMAX   ,       dataForFirstLoad[0][24].toInt())
        contentValues.put(PlayerDBHelper.MAXHEALTH  ,       dataForFirstLoad[0][22].toInt())

        if (getCountPlayer(dataForFirstLoad[0][3].toInt(), context) == 0) {
            database.insert(PlayerDBHelper.TABLE_PLAYER, null, contentValues)
        }

        // Database reading TEST
        val cursor: Cursor =
            database.query(PlayerDBHelper.TABLE_PLAYER, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            val indexSIZE      : Int  = cursor.getColumnIndex(PlayerDBHelper.SIZE      )
            val indexX         : Int  = cursor.getColumnIndex(PlayerDBHelper.X         )
            val indexY         : Int  = cursor.getColumnIndex(PlayerDBHelper.Y         )
            val indexID        : Int  = cursor.getColumnIndex(PlayerDBHelper.ID        )
            val indexKEYNAME  : Int  = cursor.getColumnIndex(PlayerDBHelper.KEY_NAME   )
            val indexKEYDESC  : Int  = cursor.getColumnIndex(PlayerDBHelper.KEY_DESC   )
            val indexKEYBITMAP: Int  = cursor.getColumnIndex(PlayerDBHelper.KEY_BITMAP )
            val indexKEYGROUP : Int  = cursor.getColumnIndex(PlayerDBHelper.KEY_GROUP  )
            val indexSpeed     : Int  = cursor.getColumnIndex(PlayerDBHelper.Speed     )
            val indexHEALTH    : Int  = cursor.getColumnIndex(PlayerDBHelper.HEALTH    )
            val indexDMG       : Int  = cursor.getColumnIndex(PlayerDBHelper.DMG       )
            val indexDEFENCE   : Int  = cursor.getColumnIndex(PlayerDBHelper.DEFENCE   )
            val indexCC        : Int  = cursor.getColumnIndex(PlayerDBHelper.CC        )
            val indexCM        : Int  = cursor.getColumnIndex(PlayerDBHelper.CM        )
            val indexAIR       : Int  = cursor.getColumnIndex(PlayerDBHelper.AIR       )
            val indexWATER     : Int  = cursor.getColumnIndex(PlayerDBHelper.WATER     )
            val indexEARTH     : Int  = cursor.getColumnIndex(PlayerDBHelper.EARTH     )
            val indexFIRE      : Int  = cursor.getColumnIndex(PlayerDBHelper.FIRE      )
            val indexAIR2      : Int  = cursor.getColumnIndex(PlayerDBHelper.AIR2      )
            val indexWATER2    : Int  = cursor.getColumnIndex(PlayerDBHelper.WATER2    )
            val indexEARTH2    : Int  = cursor.getColumnIndex(PlayerDBHelper.EARTH2    )
            val indexFIRE2     : Int  = cursor.getColumnIndex(PlayerDBHelper.FIRE2     )
            val indexMAXHEALTH : Int  = cursor.getColumnIndex(PlayerDBHelper.MAXHEALTH )
            val indexMANNA : Int  = cursor.getColumnIndex(PlayerDBHelper.MANNA )
            val indexMANNAMAX : Int  = cursor.getColumnIndex(PlayerDBHelper.MANNAMAX )
            do {
                data.add(
                    arrayOf(
                        cursor.getString(indexSIZE),
                        cursor.getString(indexX   ),
                        cursor.getString(indexY   ),
                        cursor.getString(indexID  ),
                        cursor.getString(indexKEYNAME  ),
                        cursor.getString(indexKEYDESC  ),
                        cursor.getString(indexKEYBITMAP),
                        cursor.getString(indexKEYGROUP ),
                        cursor.getString(indexSpeed  ),
                        cursor.getString(indexHEALTH ),
                        cursor.getString(indexDMG    ),
                        cursor.getString(indexDEFENCE),
                        cursor.getString(indexCC ),
                        cursor.getString(indexCM ),
                        cursor.getString(indexAIR),
                        cursor.getString(indexWATER ),
                        cursor.getString(indexEARTH ),
                        cursor.getString(indexFIRE  ),
                        cursor.getString(indexAIR2  ),
                        cursor.getString(indexWATER2),
                        cursor.getString(indexEARTH2),
                        cursor.getString(indexFIRE2 ),
                        cursor.getString(indexMAXHEALTH ),
                        cursor.getString(indexMANNA ),
                        cursor.getString(indexMANNAMAX ),
                    )
                )
            } while (cursor.moveToNext())
        } else Log.d("mLog", "0 rows")

        cursor.close()

        val player = data[0]
        size = player[0].toFloat()
        x =  player[1].toFloat()
        yOffset = player[2].toInt()
        id =  (player[3]).toInt()
        name = player[4]
        desc = player[5]
        bitmapId = context.resources.getIdentifier(player[6], "drawable", context.packageName)
        group = player[7]
        speed = player[8].toFloat()
        health = player[9].toInt()
        damage = player[10].toInt()
        defence = player[11].toInt()

        cc = player[12].toInt()
        cm = player[13].toFloat()
        naturalDamageValue = NatureForcesValues( player[14].toInt(), player[15].toInt(), player[16].toInt(),player[17].toInt())
        naturalValueDef = NatureForcesValues( player[18].toInt(), player[19].toInt(), player[20].toInt(),player[21].toInt())
        maxhealth = player[22].toInt()
        manna = player[23].toInt()
        mannamax = player[24].toInt()
    }

}