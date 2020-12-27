package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import kotlin.random.Random

class HealthComponent(
    healthPoints: Int,
    maxHealthPoints: Int = healthPoints
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
        this.maxHealthPoints = maxHealthPoints
    }

    fun applyDamage(damageComponent: DamageComponent) {
        this.healthPoints = damageComponent(this)
    }

    // "FUNCTIONS FOR TEST" BLOCK
    //-------------------------------------------------------
    //-------------------------------------------------------
    fun setHealthPointsValue(value: Int) {
        if (this.healthPoints + value < this.maxHealthPoints)
            this.healthPoints = value
        else
            this.healthPoints = this.maxHealthPoints
    }

    fun addHealthPointsValue(value: Int) {
        if (this.healthPoints + value < this.maxHealthPoints)
            this.healthPoints += value
        else
            this.healthPoints = this.maxHealthPoints
    }

    fun setMaxHealthPointsValue(value: Int) {
        this.maxHealthPoints = value
    }

    fun addMaxHealthPointsValue(value: Int) {
        this.maxHealthPoints += value
    }
    //-------------------------------------------------------
    //-------------------------------------------------------

    override fun upgrade(upgrader: ComponentUpgrader<Component>) {
        val healthUpgrader = upgrader as HealthUpgrader
        this.maxHealthPoints += healthUpgrader.value
    }
}