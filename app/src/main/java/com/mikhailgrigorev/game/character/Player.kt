package com.mikhailgrigorev.game.character

import android.content.Context
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent

import com.mikhailgrigorev.game.core.ecs.Entity

class Player(context: Context): Entity() {
    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent
    private var speed: Float = 0.2.toFloat()

    init{
        val size = 3f
        positionComponent = this.addComponent(PositionComponent(
            7f,
            Game.maxY - size - 1,
            size))
        bitmapComponent = this.addComponent(BitmapComponent(
            positionComponent = positionComponent,
            context = context,
            id = 0,
            name = "Player",
            desc = "Desc",
            bitmapId = R.drawable.player,
            group = "player"
        ))
    }

    override fun update() {
        positionComponent.update()
        bitmapComponent.update()
    }

    fun stepUp() {
        positionComponent.move(
            0f,
            -speed)
        update()
    }

}

