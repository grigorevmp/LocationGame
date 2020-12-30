package com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes

import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Jewelry(
    id: Int,
    name: String
    ) : Item (id, name,1, Item.equippable) {

}