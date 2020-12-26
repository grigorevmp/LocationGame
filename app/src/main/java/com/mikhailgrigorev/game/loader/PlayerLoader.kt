package com.mikhailgrigorev.game.loader

import android.content.Context
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForcesValues

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
    var naturalDamageValue: NatureForcesValues = NatureForcesValues(0,0,0,0)
        private set
    var naturalValueDef: NatureForcesValues = NatureForcesValues(0,0,0,0)
        private set
    init{

        val data = CSVReader(context, fileName).data
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
        naturalDamageValue = NatureForcesValues( player[13].toInt(), player[14].toInt(), player[15].toInt(),player[16].toInt())
        naturalValueDef = NatureForcesValues( player[17].toInt(), player[18].toInt(), player[19].toInt(),player[20].toInt())

    }

}