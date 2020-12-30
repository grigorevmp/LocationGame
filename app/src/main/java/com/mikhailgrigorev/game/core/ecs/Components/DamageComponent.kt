package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import kotlin.random.Random

class DamageComponent(
    private var physicalDamage: Int,
    natureForcesDamage: NatureForcesValues,
    private var criticalChancePercent: Int,
    private var criticalMultiplier: Float
): Component() {

    class DamageUpgrader(
        val physicalDamage: Int,
        natureForcesDamage: NatureForcesValues
    ) : Component.ComponentUpgrader<DamageComponent>(DamageComponent::class.java) {
        var natureForcesDamage = Array(NatureForces.count) {0}
            private set

        init {
            for (i in 0 until NatureForces.count){
                this.natureForcesDamage[i] = natureForcesDamage.natureForcesValues[i]
            }
        }
    }

    private var natureForcesDamage = Array(NatureForces.count) {0}

    private var isLastCritical: Boolean = false

    init {
        for (i in 0 until NatureForces.count){
            this.natureForcesDamage[i] = natureForcesDamage.natureForcesValues[i]
        }
    }

    operator fun invoke(healthComponent: HealthComponent) : Int {
        val enemy = healthComponent.entity
        val enemyEquipment = enemy?.getComponent(EquipmentComponent::class.java)
        var enemyPhysicalDefence = 0
        val enemyForcesDefence = Array<Int>(NatureForces.count) { 0 }
        val enemyDefenceComponent = enemy?.getComponent(DefenceComponent::class.java)
        val enemyArmorDefenceComponent = enemyEquipment?.armor?.defenceComponent

        if (enemyDefenceComponent != null)
            enemyPhysicalDefence += enemyDefenceComponent.physicalDefence
        if (enemyArmorDefenceComponent != null)
            enemyPhysicalDefence += enemyArmorDefenceComponent.physicalDefence

        for (i in 0 until NatureForces.count) {
            if (enemyDefenceComponent != null) {
                enemyForcesDefence[i] += enemyDefenceComponent.natureForcesDefence[i]
            }
            if (enemyArmorDefenceComponent != null) {
                enemyForcesDefence[i] += enemyArmorDefenceComponent.natureForcesDefence[i]
            }
        }


        var applyingPhysicalDamage = physicalDamage
        val applyingForcesDamage = natureForcesDamage

        val myEquipmentComponent = this.entity?.getComponent(EquipmentComponent::class.java)
        val myWeaponDamageComponent = myEquipmentComponent?.weapon?.damageComponent

        if (myWeaponDamageComponent != null) {
            applyingPhysicalDamage += myWeaponDamageComponent.physicalDamage
            for (i in 0 until NatureForces.count) {
                applyingForcesDamage[i] += myWeaponDamageComponent.natureForcesDamage[i]
            }
            if (Random.nextInt(1, 100) <= myWeaponDamageComponent.criticalChancePercent) {
                applyingPhysicalDamage = (applyingPhysicalDamage * myWeaponDamageComponent.criticalMultiplier).toInt()
                isLastCritical = true
            } else isLastCritical = false
        }


        applyingPhysicalDamage -= enemyPhysicalDefence
        if (applyingPhysicalDamage < 0) applyingPhysicalDamage = 0
        for (i in 0 until NatureForces.count) {
            applyingForcesDamage[i] -= enemyForcesDefence[i]
            if (applyingForcesDamage[i] < 0) applyingForcesDamage[i] = 0
        }


        var newHealthPoints = healthComponent.healthPoints
        newHealthPoints -= applyingPhysicalDamage
        newHealthPoints -= applyingForcesDamage.sum()
        return newHealthPoints
    }

    override fun upgrade(context: Context, upgrader: ComponentUpgrader<Component>) {
        val damageUpgrader = upgrader as DamageUpgrader
        this.physicalDamage += damageUpgrader.physicalDamage
        for (i in 0 until NatureForces.count){
            this.natureForcesDamage[i] += damageUpgrader.natureForcesDamage[i]
        }
    }
}