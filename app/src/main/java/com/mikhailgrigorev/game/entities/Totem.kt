package com.mikhailgrigorev.game.entities


import android.content.Context
import com.mikhailgrigorev.game.core.data.NatureForcesValues
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
            val maxHealth: Int,
            val damage: Int,
            val damageAir: Int,
            val damageWater: Int,
            val damageFire: Int,
            val damageEarth: Int,
            val defence: Int,
            val defenceAir: Int,
            val defenceWater: Int,
            val defenceEarth: Int,
            val defenceFire: Int,
            items: String,
            itemsNum: String
               ): Entity() {

    var items = items
        private set
    var itemsNum = itemsNum
        private set

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent = this.addComponent(PositionComponent(
        x.toDouble(),
        y.toDouble(),
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
        upgradeComponent.addUpgrader(DamageComponent.DamageUpgrader(
            damage,
            NatureForcesValues(damageAir,damageWater,damageEarth,damageFire)
        ))
        upgradeComponent.addUpgrader(DefenceComponent.DefenceUpgrader(
            defence,
            NatureForcesValues(defenceAir,defenceWater,defenceEarth,defenceFire)
        ))

    }

    override fun update() {
    }
}

