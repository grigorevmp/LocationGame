package com.mikhailgrigorev.game.core.ecs.Components.equipment

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Armor
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Jewelry
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Weapon

class EquipmentComponent (
    var weapon: Weapon? = null,
    var head: Armor? = null,
    var armor: Armor? = null,
    var belt: Armor? = null,
    var amulet: Jewelry? = null,
    var ring: Jewelry? = null
    ) : Component() {}