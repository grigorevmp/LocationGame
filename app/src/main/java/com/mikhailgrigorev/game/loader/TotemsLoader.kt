package com.mikhailgrigorev.game.loader

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.map.Totem

class TotemsLoader(context: Context) {
    /**
     * format: x,size,y,id,name,description,bitmapID,group
     * sample: 10f,2f,-10,3,Office,Description,office,building
     */

    var totems = ArrayList<Entity>()
        private set

    private val fileName = "MapData/totems.csv"

    init{

        val data = CSVReader(context, fileName).data

        for (totem in data){
            val obj = Totem(
                context,
                _x = totem[0].toFloat(),
                _size =  totem[1].toFloat(),
                _y =  Game.maxY + totem[2].toFloat(),
                _id =  totem[3].toInt(),
                _name = totem[4],
                _desc = totem[5],
                _bitmapId = context.resources.getIdentifier(totem[6], "drawable", context.packageName),
                _group = totem[7]
            )
            totems.add(obj)
        }
    }


}