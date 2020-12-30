package com.mikhailgrigorev.game.loader

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.entities.Building

class BuildingsLoader(context: Context) {
    /**
     * format: x,size,y,id,name,description,bitmapID,group
     * sample: 10f,2f,-10,3,Office,Description,office,building
     */

    var mapObjects = ArrayList<Entity>()
        private set

    private val fileName = "MapData/buildings.csv"

    init{

        val data = CSVReader(context, fileName).data

        for (building in data){
            val obj = Building(
                context,
                _x = building[0].toFloat(),
                _size =  building[1].toFloat(),
                _y =  Game.maxY + building[2].toFloat(),
                _id =  building[3].toInt(),
                _name = building[4],
                _desc = building[5],
                _bitmapId = context.resources.getIdentifier(building[6], "drawable", context.packageName),
                _group = building[7]
            )
            mapObjects.add(obj)
        }
    }


}