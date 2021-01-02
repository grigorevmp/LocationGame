package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.DefenceComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquippableItem
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Jewelry(
    id: Int,
    name: String,
    equippableItemType: String
    ) : EquippableItem (id, name, equippableItemType) {
        val defenceComponent = this.addComponent(DefenceComponent(
            0,
            NatureForcesValues()
        ))
        val damageComponent = this.addComponent(DamageComponent(
            0,
            NatureForcesValues(),
            0,
            0f
        ))
}