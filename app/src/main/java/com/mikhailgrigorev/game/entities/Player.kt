package com.mikhailgrigorev.game.entities

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForcesValues

import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.loader.PlayerLoader

class Player(context: Context): Entity() {

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent
    private var healthComponent: HealthComponent
    private var damageComponent: DamageComponent
    private var defenceComponent: DefenceComponent
    private var upgradeComponent: UpgradeComponent
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
            playerData.health
        ))
        damageComponent = this.addComponent(
            DamageComponent(
                playerData.damage,playerData.naturalDamageValue,playerData.cc,playerData.cm
            )
        )
        defenceComponent = this.addComponent(
            DefenceComponent(
                playerData.defence, playerData.naturalValueDef
            )
        )
        upgradeComponent = this.addComponent(
            UpgradeComponent(
            )
        )
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

