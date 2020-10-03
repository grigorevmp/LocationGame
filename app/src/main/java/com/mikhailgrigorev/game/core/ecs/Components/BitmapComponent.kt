package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import android.graphics.*
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.game.GameView

class BitmapComponent(
    id: Int = 0,
    name: String = "objectName",
    desc: String= "objectDesc",
    bitmapId: Int = 0,
    bitmap : Bitmap? = null
) : Component() {

    // id
    var _id : Int = id
        private set
    // name
    var _name: String = name
        private set
    // description
    var _desc: String = desc
        private set
    // id картинки
    var _bitmapId: Int = bitmapId
        private set
    // картинка
    var _bitmap : Bitmap? = bitmap
        private set

    // сжимаем картинку до нужных размеров
    fun init(context: Context) {
        val positionComponent = this.getEntity()?.getComponent(PositionComponent::class.java)
        if(positionComponent != null) {
            val size = positionComponent._size
            val cBitmap = BitmapFactory.decodeResource(context.resources, _bitmapId)
            _bitmap = Bitmap.createScaledBitmap(
                cBitmap,
                (size * GameView.unitW).toInt(),
                (size * GameView.unitH).toInt(),
                false
            )
            cBitmap.recycle()
        }
    }

    // тут будут вычисляться новые координаты
    override fun update() {
    }

    // рисуем картинку
    fun draw(paint: Paint?, canvas: Canvas) {
        val positionComponent = this.getEntity()?.getComponent(PositionComponent::class.java)
        if(positionComponent != null)
        _bitmap?.let { canvas.drawBitmap(
            it,
            positionComponent._x * GameView.unitW,
            positionComponent._y * GameView.unitH,
            paint) }
    }

}