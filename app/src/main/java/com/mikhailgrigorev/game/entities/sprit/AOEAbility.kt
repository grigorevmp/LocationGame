package com.mikhailgrigorev.game.entities.sprit

import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.entities.Enemy

class AOEAbility(
    id: Int,
    name: String,
    spirit: Spirit,
    level: Int = 0,
    damageMultipliers : Array<Float>
) : Ability(id, name, spirit, level, damageMultipliers)  {

    override operator fun invoke(focusedEnemy: Enemy, enemies: ArrayList<Enemy>) {
        val enemiesIterator = enemies.iterator()
        enemiesIterator.forEach { enemy ->
            val enemyHealthComponent = enemy.getComponent(HealthComponent::class.java)
            enemyHealthComponent?.let { it.applyDamage(damageComponent) }
        }
    }
}