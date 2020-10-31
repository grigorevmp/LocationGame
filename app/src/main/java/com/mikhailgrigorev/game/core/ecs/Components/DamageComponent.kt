package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForces
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForcesValues
import kotlin.random.Random

class DamageComponent(
    physicalDamage: Int,
    natureForcesDamage: NatureForcesValues,
    criticalChancePercent: Int,
    criticalMultiplier: Float
): Component() {

    class DamageUpgrader : Component.ComponentUpgrader<DamageComponent>(DamageComponent::class.java) {
        val physicalDamage = Random.nextInt(0,10)
        val natureForcesDamageArray = Array<Int>(NatureForces.count) { Random.nextInt(0,10)}
    }

    var physicalDamage: Int
        private set

    var natureForcesDamageArray = Array<Int>(NatureForces.count) {0}

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

        for (i in 0 until NatureForces.count){
            natureForcesDamageArray[i] = natureForcesDamage.natureForcesValues[i]
        }
    }

    operator fun invoke(healthComponent: HealthComponent) : Int {
        var newHealthPoints = healthComponent.healthPoints
        var applyingPhysicalDamage = 0
        var applyingForcesDamage = 0
        val defenceComponent = healthComponent.entity?.getComponent(DefenceComponent::class.java)
        if (defenceComponent != null) {
            applyingPhysicalDamage = physicalDamage - defenceComponent.physicalDefence
            for (i in 0 until NatureForces.count) {
                applyingForcesDamage += natureForcesDamageArray[i] - defenceComponent.natureForcesDefence[i]
            }
        }

        if(Random.nextInt(1,100) <= criticalChancePercent) {
            applyingPhysicalDamage = (applyingPhysicalDamage*criticalMultiplier).toInt()
            isLastCritical = true
        } else isLastCritical = false

        newHealthPoints -= applyingPhysicalDamage
        newHealthPoints -= applyingForcesDamage
        return newHealthPoints
    }

    override fun upgrade(upgrader: ComponentUpgrader<Component>) {
        val damageUpgrader = upgrader as DamageUpgrader
        this.physicalDamage += damageUpgrader.physicalDamage
        for (i in 0 until NatureForces.count){
            this.natureForcesDamageArray[i] += damageUpgrader.natureForcesDamageArray[i]
        }
    }
}