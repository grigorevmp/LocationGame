package com.mikhailgrigorev.game.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import com.mikhailgrigorev.game.core.GameView

open class PlayerBody {
    var x // координаты
            = 0f
    var y = 0f
    var size // размер
            = 0f
    var speed // скорость
            = 0f
    var bitmapId // id картинки
            = 0
    var bitmap // картинка
            : Bitmap? = null

    fun init(context: Context) { // сжимаем картинку до нужных размеров
        val cBitmap = BitmapFactory.decodeResource(context.resources, bitmapId)
        bitmap = Bitmap.createScaledBitmap(
            cBitmap, (size * GameView.unitW).toInt(), (size * GameView.unitH).toInt(), false
        )
        cBitmap.recycle()
    }

    open fun update() { // тут будут вычисляться новые координаты
    }

    fun draw(paint: Paint?, canvas: Canvas) { // рисуем картинку
        bitmap?.let { canvas.drawBitmap(it, x * GameView.unitW, y * GameView.unitH, paint) }
    }
}