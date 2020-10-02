package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import android.graphics.*
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent
import com.mikhailgrigorev.game.game.GameView

class BitmapComponent (
    // характеристики
    var id : Int = 0,
    var desc: String = "object",

    // размер
    var size: Float = 0f,

    // скорость
    var speed: Float = 0f,

    // id картинки
    var bitmapId: Int = 0,
    // картинка
    private var bitmap : Bitmap? = null
) : Component() {



    // сжимаем картинку до нужных размеров
    fun init(context: Context) {
        val cBitmap = BitmapFactory.decodeResource(context.resources, bitmapId)
        bitmap = Bitmap.createScaledBitmap(
            cBitmap,
            (size * GameView.unitW).toInt(),
            (size * GameView.unitH).toInt(),
            false
        )
        cBitmap.recycle()
    }

    // тут будут вычисляться новые координаты
    override fun update() {
    }

    // рисуем картинку
    fun draw(paint: Paint?, canvas: Canvas) {
        var positionComponent = this.entity()?.getComponent(PositionComponent::class.java)
        if(positionComponent != null)
        bitmap?.let { canvas.drawBitmap(
            it,
            positionComponent.x() * GameView.unitW,
            positionComponent.y() * GameView.unitH,
            paint) }
    }

    fun getObjectId(): Int {
        return id
    }

    fun getObjectDesc(): String {
        return desc
    }

    fun getObjectSpeed(): Float{
        return speed
    }

    fun getObjectBitmapId(): Int{
        return bitmapId
    }

    fun getObjectSize(): Float{
        return size
    }
}