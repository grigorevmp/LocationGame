package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquippableItem
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Jewelry(
    id: Int,
    name: String,
    equippableItemType: String
    ) : EquippableItem (id, name, equippableItemType) {

}