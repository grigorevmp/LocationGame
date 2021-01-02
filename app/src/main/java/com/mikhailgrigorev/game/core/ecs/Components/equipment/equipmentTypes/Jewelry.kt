package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.DefenceComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquippableItem
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Jewelry(
    id: Int,
    name: String,
    equippableItemType: String,
    physicalDefence : Int,
    natureForcesDefence: NatureForcesValues,
    physicalDamage: Int,
    natureForcesDamage: NatureForcesValues,
    criticalChancePercent: Int,
    criticalMultiplier: Float
    ) : EquippableItem (id, name, equippableItemType) {
        val defenceComponent = this.addComponent(DefenceComponent(
            physicalDefence,
            natureForcesDefence
        ))
        val damageComponent = this.addComponent(DamageComponent(
            physicalDamage,
            natureForcesDamage,
            criticalChancePercent,
            criticalMultiplier
        ))
}