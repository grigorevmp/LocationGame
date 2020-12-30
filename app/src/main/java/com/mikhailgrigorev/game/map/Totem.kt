package com.mikhailgrigorev.game.map


import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Components.*

import com.mikhailgrigorev.game.core.ecs.Entity

class Totem(context: Context,
            x : Float,
            y: Float,
            size: Float,
            id: Int,
            name: String,
            desc: String,
            bitmapId: Int,
            group: String,
            maxHealth: Int,
            damage: Int,
            damageAir: Int,
            damageWater: Int,
            damageFire: Int,
            damageEarth: Int,
            defence: Int,
            defenceAir: Int,
            defenceWater: Int,
            defenceEarth: Int,
            defenceFire: Int,
               ): Entity() {

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent = this.addComponent(PositionComponent(
        x,
        y,
        size
    ))
    private var upgradeComponent: UpgradeComponent = this.addComponent(UpgradeComponent())


    init{
        bitmapComponent = this.addComponent(BitmapComponent(
            positionComponent = positionComponent,
            context = context,
            id = id,
            name = name,
            desc = desc,
            bitmapId = bitmapId,
            group = group
        ))
        upgradeComponent.addUpgrader(HealthComponent.HealthUpgrader(
            healthPoints = 0,
            maxHealthPoints = maxHealth
        ))

    }

    override fun update() {
    }
}

