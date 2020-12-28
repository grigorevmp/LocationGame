package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component

class TotemComponent(health: Int,
                     damage: Int,
                     damageAir: Int,
                     damageWater: Int,
                     damageFire: Int,
                     damageEarth: Int,
                     defence: Int,
                     defenceAir: Int,
                     defenceWater: Int,
                     defenceEarth: Int,
                     defenceFire: Int,
) : Component() {

    var health: Int
        private set
    var damage: Int
        private set
    var damageAir: Int
        private set
    var damageWater: Int
        private set
    var damageFire: Int
        private set
    var damageEarth: Int
        private set
    var defence: Int
        private set
    var defenceAir: Int
        private set
    var defenceWater: Int
        private set
    var defenceEarth: Int
        private set
    var defenceFire: Int
        private set

    init {
        this.health = health
        this.damage = damage
        this.damageAir = damageAir
        this.damageWater = damageWater
        this.damageFire = damageFire
        this.damageEarth = damageEarth
        this.defence = defence
        this.defenceAir = defenceAir
        this.defenceWater = defenceWater
        this.defenceEarth = defenceEarth
        this.defenceFire = defenceFire
    }
}