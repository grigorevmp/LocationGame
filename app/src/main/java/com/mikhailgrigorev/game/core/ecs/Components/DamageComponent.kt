package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import kotlin.random.Random

class DamageComponent(
    physicalDamage: Int,
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

    var physicalDamage = physicalDamage
        private set

    var natureForcesDamage = Array(NatureForces.count) {0}
        private set

    private var isLastCritical: Boolean = false

    init {
        for (i in 0 until NatureForces.count){
            this.natureForcesDamage[i] = natureForcesDamage.natureForcesValues[i]
        }
    }

    operator fun invoke(healthComponent: HealthComponent) : Int {
        val equipmentFields = EquipmentComponent::class.java.declaredFields

        val enemy = healthComponent.entity
        val enemyEquipment = enemy?.getComponent(EquipmentComponent::class.java)

        var enemyPhysicalDefence = 0
        val enemyForcesDefence = Array<Int>(NatureForces.count) { 0 }
        val enemyDefenceComponent = enemy?.getComponent(DefenceComponent::class.java)

        if (enemyDefenceComponent != null) {
            enemyPhysicalDefence += enemyDefenceComponent.physicalDefence
            for (i in 0 until NatureForces.count) {
                enemyForcesDefence[i] += enemyDefenceComponent.natureForcesDefence[i]
            }
        }

        for (field in equipmentFields) {
            field.isAccessible = true
            val equippableItem = field.get(enemyEquipment) as Item
            val equippableItemDefenceComponent =
                equippableItem.getComponent(DefenceComponent::class.java)
            if (equippableItemDefenceComponent != null) {
                enemyPhysicalDefence += equippableItemDefenceComponent.physicalDefence
                for (i in 0 until NatureForces.count) {
                    enemyForcesDefence[i] += equippableItemDefenceComponent.natureForcesDefence[i]
                }
            }
        }


        val myEquipment = this.entity?.getComponent(EquipmentComponent::class.java)

        var myPhysicalDamage = physicalDamage
        val myForcesDamage = natureForcesDamage.toIntArray()
        var myCriticalChancePercent = criticalChancePercent
        var myCriticalMultiplier = criticalMultiplier


        for (field in equipmentFields) {
            field.isAccessible = true
            val equippableItem = field.get(myEquipment) as Item
            val equippableItemDamageComponent = equippableItem.getComponent(DamageComponent::class.java)
            if (equippableItemDamageComponent != null) {
                myPhysicalDamage += equippableItemDamageComponent.physicalDamage
                for (i in 0 until NatureForces.count) {
                    myForcesDamage[i] += equippableItemDamageComponent.natureForcesDamage[i]
                }
                myCriticalChancePercent += equippableItemDamageComponent.criticalChancePercent
                myCriticalMultiplier += equippableItemDamageComponent.criticalMultiplier
            }
        }

        if (Random.nextInt(1, 100) <= myCriticalChancePercent) {
            myPhysicalDamage = (myPhysicalDamage * myCriticalMultiplier).toInt()
            isLastCritical = true
        } else isLastCritical = false


        myPhysicalDamage -= enemyPhysicalDefence
        if (myPhysicalDamage < 0) myPhysicalDamage = 0
        for (i in 0 until NatureForces.count) {
            myForcesDamage[i] -= enemyForcesDefence[i]
            if (myForcesDamage[i] < 0) myForcesDamage[i] = 0
        }


        var newHealthPoints = healthComponent.healthPoints
        newHealthPoints -= myPhysicalDamage
        newHealthPoints -= myForcesDamage.sum()
        if (newHealthPoints < 0) newHealthPoints = 0
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