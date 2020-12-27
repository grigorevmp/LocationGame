package com.mikhailgrigorev.game.core.ecs.Components.inventory.item.weapon

import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Weapon(
    id: Int,
    name: String,
    damage: Int
    ) : Item (id, name,1) {
    var damage = damage
        private set
}