package com.mikhailgrigorev.game.map


import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent

import com.mikhailgrigorev.game.core.ecs.Entity

class Building(context: Context,
               _x : Float,
               _y: Float,
               _size: Float,
                _id: Int,
               _name: String,
               _desc: String,
               _bitmapId: Int,
               _group: String
               ): Entity() {

    private var id = _id
    private var name = _name
    private var desc = _desc
    private var bitmapId = _bitmapId
    private var group = _group
    private var x = _x
    private var y = _y
    private var size = _size

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent

    init{
        positionComponent = this.addComponent(PositionComponent(
            x,
            y,
            size
        ))
        bitmapComponent = this.addComponent(BitmapComponent(
            positionComponent = positionComponent,
            context = context,
            id = id,
            name = name,
            desc = desc,
            bitmapId = bitmapId,
            group = group
        ))
    }

    override fun update() {
    }


}

