package com.mikhailgrigorev.game.map


import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Components.*

import com.mikhailgrigorev.game.core.ecs.Entity

class Totem(context: Context,
            _x : Float,
            _y: Float,
            _size: Float,
            _id: Int,
            _name: String,
            _desc: String,
            _bitmapId: Int,
            _group: String,
            _health: Int,
            _damage: Int,
            _damageAir: Int,
            _damageWater: Int,
            _damageFire: Int,
            _damageEarth: Int,
            _defence: Int,
            _defenceAir: Int,
            _defenceWater: Int,
            _defenceEarth: Int,
            _defenceFire: Int,
               ): Entity() {

    private var id = _id
    private var name = _name
    private var desc = _desc
    private var bitmapId = _bitmapId
    private var group = _group
    private var x = _x
    private var y = _y
    private var size = _size

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent
    private var totemComponent: TotemComponent
    private var upgradeComponent: UpgradeComponent


    init{
        totemComponent = this.addComponent(TotemComponent(
            _health,
            _damage,
            _damageAir,
            _damageWater,
            _damageFire,
            _damageEarth,
            _defence,
            _defenceAir,
            _defenceWater,
            _defenceEarth,
            _defenceFire,
        ))
        positionComponent = this.addComponent(PositionComponent(
            x,
            y,
            size
        ))
        bitmapComponent = this.addComponent(BitmapComponent(
            positionComponent = positionComponent,
            context = context,
            id = id,
            name = name,
            desc = desc,
            bitmapId = bitmapId,
            group = group
        ))
        upgradeComponent = this.addComponent(UpgradeComponent())
        upgradeComponent.addUpgrader(HealthComponent.HealthUpgrader(
            0,
            10
        ))

    }

    override fun update() {
    }
}

