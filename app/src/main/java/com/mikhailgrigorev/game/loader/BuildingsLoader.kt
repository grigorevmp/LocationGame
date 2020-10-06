package com.mikhailgrigorev.game.loader

import android.content.Context
import android.content.res.AssetManager
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.map.Building
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class BuildingsLoader(context: Context) {

    var mapObjects = ArrayList<Entity>()
        private set

    private val mContext: Context = context
    private var am: AssetManager = mContext.assets

    var ins: InputStream = am.open("MapData/buildings.csv")

    private val reader = BufferedReader(InputStreamReader(ins))
    private var line = reader.readLine()

    init{

        while(line != null){
            val lineArr = line.split(",")
            val obj = Building(
                context,
                _x = (lineArr[0]).toFloat(),
                _size =  (lineArr[1]).toFloat(),
                _y =  Game.maxY + lineArr[2].toFloat(),
                _id =  (lineArr[3]).toInt(),
                _name = lineArr[4],
                _desc = lineArr[5],
                _bitmapId = context.resources.getIdentifier(lineArr[6], "drawable", context.packageName),
                _group = lineArr[7]
            )
            mapObjects.add(obj)
            line = reader.readLine()
        }
    }

}