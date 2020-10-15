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
            _group: String
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
    private var upgradeComponent: UpgradeComponent = UpgradeComponent()

    init{
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
        upgradeComponent.addUpgrader(HealthComponent.HealthUpgrader())
        upgradeComponent.addUpgrader(DamageComponent.DamageUpgrader())
        upgradeComponent.addUpgrader(DefenceComponent.DefenceUpgrader())
    }

    override fun update() {
    }
}

