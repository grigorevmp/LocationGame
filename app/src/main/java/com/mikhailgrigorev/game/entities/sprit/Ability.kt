package com.mikhailgrigorev.game.entities.sprit

import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.entities.Enemy

open class Ability  (
    val id: Int,
    val name: String,
    val spirit: Spirit,
    level: Int = 0,
    damageMultipliers : Array<Float>
    ) {

    var level = level
        private set
    val maxLevel = damageMultipliers.size - 1

    val damageMultiplier: Float
        get() = damageMultipliers[level]

    var damageMultipliers = damageMultipliers
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

    fun levelUp(){ ++level }


    open operator fun invoke(focusedEnemy: Enemy, enemies: ArrayList<Enemy>){
        val focusedEnemyHealthComponent = focusedEnemy.getComponent(HealthComponent::class.java)
        focusedEnemyHealthComponent?.let { it.applyDamage(damageComponent) }
    }
}