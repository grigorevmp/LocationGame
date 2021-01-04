package com.mikhailgrigorev.game.entities.sprit

import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent

class Ability  (
    val id: Int,
    val name: String,
    val spirit: Spirit,
    damageMultiplier : Float
    ) : Component() {

    class AbilityUpgrader : Component.ComponentUpgrader<Ability> (Ability::class.java) {

    }


    var damageMultiplier = damageMultiplier
        private set

    val damageComponent: DamageComponent
        get() = DamageComponent(
            0,
            NatureForcesValues(
                air   = (spirit.natureForcesDamage[NatureForces.Air.ordinal]   * damageMultiplier).toInt(),
                water = (spirit.natureForcesDamage[NatureForces.Water.ordinal] * damageMultiplier).toInt(),
                earth = (spirit.natureForcesDamage[NatureForces.Earth.ordinal] * damageMultiplier).toInt(),
                fire  = (spirit.natureForcesDamage[NatureForces.Fire.ordinal]  * damageMultiplier).toInt(),
            ),
            0,
            0f
        )
}