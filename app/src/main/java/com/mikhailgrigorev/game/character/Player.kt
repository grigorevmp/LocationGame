package com.mikhailgrigorev.game.character

import android.content.Context
import com.mikhailgrigorev.game.game.GameView
import com.mikhailgrigorev.game.R


class Player(context: Context): BitmapObject() {

    init{

        // характеристики
        id = 0
        desc = "Player"

        // координаты
        x = 7f
        y = GameView.maxY - size - 1

        // размер
        size = 5f

        // скорость
        speed = 0.2.toFloat()

        // id картинки
        bitmapId = R.drawable.ship

        // инициализируем корабль
        init(context)
    }

    override fun update() {
        rect.set(x*GameView.unitW, y*GameView.unitH, (x+size)*GameView.unitW, (y+size)*GameView.unitH)
    }

    fun stepUp() {
        y -= speed
        update()
    }

}

