package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import kotlin.random.Random

class HealthComponent(
    healthPoints: Int
) : Component() {

    class HealthUpgrader : Component.ComponentUpgrader<HealthComponent>(HealthComponent::class.java) {
        val value = Random.nextInt(0,100)
    }

    var healthPoints : Int
        private set

    var maxHealthPoints: Int
        private set

    init {
        this.healthPoints = healthPoints
        this.maxHealthPoints = healthPoints
    }

    fun applyDamage(damageComponent: DamageComponent) {
        this.healthPoints = damageComponent(this)
    }

    override fun upgrade(upgrader: ComponentUpgrader<Component>) {
        val healthUpgrader = upgrader as HealthUpgrader
        this.maxHealthPoints += healthUpgrader.value
    }
}