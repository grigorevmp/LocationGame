package com.mikhailgrigorev.game.core.ecs.Components

import android.graphics.RectF
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.game.Game

class PositionComponent( x: Double, y: Double, size: Float,
) : Component() {

    var x: Double
        private set
    var y: Double
        private set
    var size: Float
        private set
    var rect: RectF = RectF(x.toFloat(), y.toFloat(), x.toFloat() + size, y.toFloat() + size)
        private set

    init {
        this.x = x
        this.y = y
        this.size = size
        update()
    }


    override fun update() {
        rect.set(
            x.toFloat() * Game.unitW,
            y.toFloat() * Game.unitH,
            (x.toFloat() + size) * Game.unitW,
            (y.toFloat() + size) * Game.unitH
        )
    }
}