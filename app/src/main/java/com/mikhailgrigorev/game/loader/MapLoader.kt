package com.mikhailgrigorev.game.loader

import android.content.Context
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.game.GameView
import com.mikhailgrigorev.game.map.Building

class MapLoader(context: Context) {

    var mapObjects = ArrayList<Entity>()
        private set
    //val fileReader = FileReader()

    init{
        val obj = Building(context,
            _x = 2f,
            _size = 2f,
            _y = GameView.maxY - 2f - 10,
            _id = 0,
            _name = "Apple Store",
            _desc = "Description",
            _bitmapId = R.drawable.building,
            _group = "building")
        val obj2 = Building(context,
            _x = 7f,
            _size = 2f,
            _y = GameView.maxY - 2f - 20,
            _id = 0,
            _name = "Hospital",
            _desc = "Description",
            _bitmapId = R.drawable.hospital,
            _group = "building")
        val obj3 = Building(context,
            _x = 15f,
            _size = 2f,
            _y = GameView.maxY - 2f - 12,
            _id = 0,
            _name = "Tagret",
            _desc = "Description",
            _bitmapId = R.drawable.marker,
            _group = "building")
        val obj4 = Building(context,
            _x = 10f,
            _size = 2f,
            _y = GameView.maxY - 2f - 8,
            _id = 0,
            _name = "Office",
            _desc = "Description",
            _bitmapId = R.drawable.office,
            _group = "building")
        mapObjects.add(obj)
        mapObjects.add(obj2)
        mapObjects.add(obj3)
        mapObjects.add(obj4)
    }

}