package com.mikhailgrigorev.game.entities

import android.content.Context
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent

import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.loader.PlayerLoader

class Player(context: Context): Entity() {

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent
    private var healthComponent: HealthComponent
    private var speed: Float = 0.2.toFloat()

    init{
        val playerData = PlayerLoader(context)
        speed = playerData.speed
        val size = playerData.size
        positionComponent = this.addComponent(PositionComponent(
            playerData.x,
            Game.maxY - size - playerData.yOffset,
            size))
        bitmapComponent = this.addComponent(BitmapComponent(
            positionComponent = positionComponent,
            context = context,
            id = playerData.id,
            name = playerData.name,
            desc = playerData.desc,
            bitmapId = playerData.bitmapId,
            group = playerData.group
        ))
        healthComponent = this.addComponent(HealthComponent(
            300
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

