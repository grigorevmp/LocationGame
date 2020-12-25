package com.mikhailgrigorev.game.entities


import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForcesValues

import com.mikhailgrigorev.game.core.ecs.Entity

class Enemy(context: Context,
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
    private var healthComponent: HealthComponent
    private var damageComponent: DamageComponent
    private var defenceComponent: DefenceComponent
    private var upgradeComponent: UpgradeComponent
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
        healthComponent = this.addComponent(HealthComponent(
            300
        ))
        val naturalDamageValue = NatureForcesValues(5, 10, 20, 30)
        damageComponent = this.addComponent(
            DamageComponent(
                10,naturalDamageValue,10,5f
            )
        )
        val naturalValueDef = NatureForcesValues(0, 0, 0, 0)
        defenceComponent = this.addComponent(
            DefenceComponent(
                0, naturalValueDef
            )
        )
        upgradeComponent = this.addComponent(
            UpgradeComponent(
            )
        )
    }

    override fun update() {
    }

}

