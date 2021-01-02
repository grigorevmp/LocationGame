package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.DefenceComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquippableItem
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Armor (
    id: Int,
    name: String,
    equippableItemType: String,
    physicalDefence : Int,
    natureForcesDefence: NatureForcesValues
    ) : EquippableItem(id, name, equippableItemType) {
        val defenceComponent = this.addComponent(DefenceComponent(
            physicalDefence,
            natureForcesDefence
        ))
}