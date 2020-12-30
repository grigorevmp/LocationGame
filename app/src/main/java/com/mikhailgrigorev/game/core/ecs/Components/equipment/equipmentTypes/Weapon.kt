package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Weapon(
    id: Int,
    name: String,
    //physicalDamage: Int,
    //natureForcesDamage: NatureForcesValues,
    //criticalChancePercent: Int,
    //criticalMultiplier: Float
    val damage: Int
    ) : Item (id, name,1, Item.equippable) {
        //val damageComponent = this.addComponent(DamageComponent(
        //    physicalDamage,
        //    natureForcesDamage,
        //    criticalChancePercent,
        //    criticalMultiplier
        //))
}