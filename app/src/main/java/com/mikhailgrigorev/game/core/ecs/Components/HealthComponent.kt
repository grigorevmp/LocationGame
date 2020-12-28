package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import kotlin.random.Random

class HealthComponent(
    healthPoints: Int,
    maxHealthPoints: Int = healthPoints
) : Component() {

    class HealthUpgrader(
        var healthPoints: Int,
        var maxHealthPoints: Int
    ) : Component.ComponentUpgrader<HealthComponent>(HealthComponent::class.java) {}

    var healthPoints : Int
        private set

    var maxHealthPoints: Int
        private set

    init {
        this.healthPoints = healthPoints
        this.maxHealthPoints = maxHealthPoints
    }

    fun applyDamage(damageComponent: DamageComponent) {
        this.healthPoints = damageComponent(this)
    }

    override fun upgrade(upgrader: ComponentUpgrader<Component>) {
        val healthUpgrader = upgrader as HealthUpgrader
        this.maxHealthPoints += healthUpgrader.maxHealthPoints
        this.healthPoints += healthUpgrader.healthPoints
        if (this.healthPoints > this.maxHealthPoints) this.healthPoints = this.maxHealthPoints
    }
}