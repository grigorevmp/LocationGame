package com.mikhailgrigorev.game.core.ecs.Components.equipment

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Weapon

class EquipmentComponent (
    var weapon: Weapon
    ) : Component() {}