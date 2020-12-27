package com.mikhailgrigorev.game.core.ecs.Components.equipment

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.weapon.Weapon

class EquipmentComponent (
    var weapon: Weapon
    ) : Component() {}