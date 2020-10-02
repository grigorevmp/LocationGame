package com.mikhailgrigorev.game.core.ecs.Components

import android.graphics.Rect
import android.graphics.RectF
import com.mikhailgrigorev.game.core.ecs.Component
import java.nio.channels.FileLock

class PositionComponent(
    private var _x: Float,
    private var _y: Float,
    var _rect: RectF = RectF()
) : Component() {
    fun x(): Float { return _x }
    fun y(): Float { return _y }
    fun rect(): RectF { return _rect }

    fun move(xDisplacement: Float, yDisplacement: Float) {
        _x += xDisplacement
        _y += yDisplacement
    }
}