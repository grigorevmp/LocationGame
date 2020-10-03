package com.mikhailgrigorev.game.core.ecs.Components

import android.graphics.RectF
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.game.GameView

class PositionComponent( x: Float, y: Float, size: Float,
) : Component() {

    var _x = x
        private set
    var _y = y
        private set
    var _size = size
        private set

    var _rect: RectF = RectF(_x, _y, _x + _size, _y + _size)
        private set

    init {
        update()
    }

    fun move(xDisplacement: Float, yDisplacement: Float) {
        _x += xDisplacement
        _y += yDisplacement
    }

    override fun update() {
        _rect.set(
            _x * GameView.unitW,
            _y * GameView.unitH,
            (_x + _size) * GameView.unitW,
            (_y + _size) * GameView.unitH
        )
    }
}