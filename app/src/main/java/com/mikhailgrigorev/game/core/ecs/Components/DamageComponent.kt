package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import kotlin.random.Random

class DamageComponent(
    physicalDamage: Int,
    criticalChancePercent: Int,
    criticalMultiplier: Float
): Component() {
    var physicalDamage: Int
        private set

    var criticalChancePercent: Int
        private set

    var criticalMultiplier: Float
        private set

    var isLastCritical: Boolean = false
        private set

    init {
        this.physicalDamage = physicalDamage
        this.criticalChancePercent = criticalChancePercent
        this.criticalMultiplier = criticalMultiplier
    }

    operator fun invoke(healthComponent: HealthComponent) : Int {
        var newHealthPoints = healthComponent.healthPoints
        var applyingPhysicalDamage = physicalDamage
        val defenceComponent = healthComponent.entity?.getComponent(DefenceComponent::class.java)
        if (defenceComponent != null)
            applyingPhysicalDamage -= applyingPhysicalDamage * defenceComponent.physicalDefencePercent

        if(Random.nextInt(1,100) <= criticalChancePercent) {
            applyingPhysicalDamage = (applyingPhysicalDamage*criticalMultiplier).toInt()
            isLastCritical = true
        } else isLastCritical = false

        newHealthPoints -= applyingPhysicalDamage
        return newHealthPoints
    }

    fun addPhysicalDamage(value: Int){
        this.physicalDamage += value
    }
}