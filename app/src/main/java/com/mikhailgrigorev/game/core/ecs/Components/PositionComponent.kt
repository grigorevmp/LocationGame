package com.mikhailgrigorev.game.core.ecs.Components

import android.graphics.Rect
import android.graphics.RectF
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.game.GameView
import java.nio.channels.FileLock

class PositionComponent(
    private var _x: Float,
    private var _y: Float,
    private var _size: Float,
) : Component() {
    lateinit var _rect: RectF
    init {
        update()
    }
    fun getX(): Float { return _x }
    fun getY(): Float { return _y }
    fun getSize(): Float {return  _size }
    fun getRect(): RectF { return _rect }

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