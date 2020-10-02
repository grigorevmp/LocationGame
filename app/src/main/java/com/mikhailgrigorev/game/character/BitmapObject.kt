package com.mikhailgrigorev.game.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.mikhailgrigorev.game.game.GameView

open class BitmapObject{

    // координаты
    var x = 0f
    var y = 0f

    // размер
    var size = 0f

    // скорость
    var speed = 0f

    // id картинки
    var bitmapId = 0

    // картинка
    var bitmap : Bitmap? = null

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
    open fun update() {
    }

    // рисуем картинку
    fun draw(paint: Paint?, canvas: Canvas) {
        bitmap?.let { canvas.drawBitmap(
            it,
            x * GameView.unitW,
            y * GameView.unitH, paint) }
    }

    fun getXPos(): Float {
        return x
    }

    fun getYPos(): Float{
        return y
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