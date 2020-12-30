package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.DefenceComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Armor (
    id: Int,
    name: String,
    physicalDefence : Int,
    natureForcesDefence: NatureForcesValues
    ) : Item (id, name,1, Item.equippable) {
        val defenceComponent = this.addComponent(DefenceComponent(
            physicalDefence,
            natureForcesDefence
        ))
}