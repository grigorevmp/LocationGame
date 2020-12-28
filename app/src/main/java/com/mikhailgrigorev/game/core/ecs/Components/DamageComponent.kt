package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import kotlin.random.Random

class DamageComponent(
    physicalDamage: Int,
    natureForcesDamage: NatureForcesValues,
    criticalChancePercent: Int,
    criticalMultiplier: Float
): Component() {

    class DamageUpgrader(
        val physicalDamage: Int,
        natureForcesDamage: NatureForcesValues
    ) : Component.ComponentUpgrader<DamageComponent>(DamageComponent::class.java) {
        var natureForcesDamage = Array<Int>(NatureForces.count) {0}
            private set

        init {
            for (i in 0 until NatureForces.count){
                this.natureForcesDamage[i] = natureForcesDamage.natureForcesValues[i]
            }
        }
    }

    var physicalDamage: Int
        private set

    var natureForcesDamage = Array<Int>(NatureForces.count) {0}

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
            this.natureForcesDamage[i] = natureForcesDamage.natureForcesValues[i]
        }
    }

    operator fun invoke(healthComponent: HealthComponent) : Int {
        var newHealthPoints = healthComponent.healthPoints
        var applyingPhysicalDamage = 0
        var applyingForcesDamage = 0
        val weapon = healthComponent.entity?.getComponent(EquipmentComponent::class.java)?.weapon
        val defenceComponent = healthComponent.entity?.getComponent(DefenceComponent::class.java)
        if (defenceComponent != null) {
            applyingPhysicalDamage = physicalDamage - defenceComponent.physicalDefence
            for (i in 0 until NatureForces.count) {
                applyingForcesDamage += natureForcesDamage[i] - defenceComponent.natureForcesDefence[i]
            }
            if (weapon != null){
                applyingPhysicalDamage += (weapon.damage - defenceComponent.physicalDefence)
            }
        }
        else {
            applyingPhysicalDamage = physicalDamage
            if (weapon != null){ applyingPhysicalDamage += weapon.damage }
            for (i in 0 until NatureForces.count) {
                applyingForcesDamage += natureForcesDamage[i]
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
            this.natureForcesDamage[i] += damageUpgrader.natureForcesDamage[i]
        }
    }
}