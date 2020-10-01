package com.mikhailgrigorev.game.character


import android.content.Context
import com.mikhailgrigorev.game.core.GameView
import com.mikhailgrigorev.game.R


class Player(context: Context): PlayerBody() {
    init{
        bitmapId = R.drawable.ship// определяем начальные параметры
        size = 5f
        x = 7f
        y = GameView.maxY - size - 1
        speed = 0.2.toFloat()
        init(context) // инициализируем корабль
    }

    override fun update() { // перемещаем корабль в зависимости от нажатой кнопки

    }

    fun getXPos(): Float {
        return x
    }

    fun getYPos(): Float{
        return y
    }

    fun getSizePos(): Float{
        return size
    }

    fun step() {
        y -= 1
    }
}

