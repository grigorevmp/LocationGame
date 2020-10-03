package com.mikhailgrigorev.game.core.ecs.Components

import android.graphics.RectF
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.game.GameView

class PositionComponent( x: Float, y: Float, size: Float,
) : Component() {

    var x: Float
        private set
    var y: Float
        private set
    var size: Float
        private set
    var rect: RectF = RectF(x, y, x + size, y + size)
        private set

    init {
        this.x = x
        this.y = y
        this.size = size
        update()
    }

    fun move(xDisplacement: Float, yDisplacement: Float) {
        x += xDisplacement
        y += yDisplacement
    }

    override fun update() {
        rect.set(
            x * GameView.unitW,
            y * GameView.unitH,
            (x + size) * GameView.unitW,
            (y + size) * GameView.unitH
        )
    }
}