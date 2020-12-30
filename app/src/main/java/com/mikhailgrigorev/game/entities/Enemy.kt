package com.mikhailgrigorev.game.entities


import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.core.data.NatureForcesValues

import com.mikhailgrigorev.game.core.ecs.Entity

class Enemy(context: Context,
            _x : Float,
            _y: Float,
            _size: Float,
            _id: Int,
            _health: Int,
            _damage: Int,
            _defence: Int,
            _cc: Int,
            _cm: Float,
            _name: String,
            _desc: String,
            _bitmapId: Int,
            _group: String,
            _naturalDamageValue: NatureForcesValues,
            _naturalValueDef: NatureForcesValues,
            _multiple: Int,
            items: String,
            itemsNum: String
): Entity() {

    var items = items
        private set
    var itemsNum = itemsNum
        private set

    private var id = _id
    private var name = _name
    private var desc = _desc
    private var bitmapId = _bitmapId
    private var group = _group
    private var x = _x
    private var y = _y
    private var size = _size
    private var health = _health
    private var damage = _damage
    private var defence = _defence
    private var cc = _cc
    private var cm = _cm
    private val naturalDamageValue = _naturalDamageValue
    private val naturalValueDef = _naturalValueDef

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent
    private var healthComponent: HealthComponent
    private var damageComponent: DamageComponent
    private var defenceComponent: DefenceComponent
    private var upgradeComponent: UpgradeComponent
    private var multiple: Int = _multiple
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
            group = group,
            multiple = multiple
        ))
        healthComponent = this.addComponent(HealthComponent(
            health
        ))
        damageComponent = this.addComponent(
            DamageComponent(
                damage,naturalDamageValue,cc,cm
            )
        )
        defenceComponent = this.addComponent(
            DefenceComponent(
                defence, naturalValueDef
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

