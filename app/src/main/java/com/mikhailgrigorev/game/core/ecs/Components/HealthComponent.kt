package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component

class HealthComponent(
    healthPoints: Int
) : Component() {
    var healthPoints : Int
        private set

    init {
        this.healthPoints = healthPoints
    }

    fun applyDamage(damageComponent: DamageComponent) {
        this.healthPoints = damageComponent(this)
    }
}