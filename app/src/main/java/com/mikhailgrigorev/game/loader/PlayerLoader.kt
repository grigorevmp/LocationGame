package com.mikhailgrigorev.game.loader

import android.content.Context
import com.mikhailgrigorev.game.R

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

    }

}