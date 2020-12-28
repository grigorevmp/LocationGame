package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.entities.Player
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

    override fun upgrade(context: Context, upgrader: ComponentUpgrader<Component>) {
        val healthUpgrader = upgrader as HealthUpgrader
        this.maxHealthPoints += healthUpgrader.maxHealthPoints
        this.healthPoints += healthUpgrader.healthPoints
        if (this.healthPoints > this.maxHealthPoints) this.healthPoints = this.maxHealthPoints
        DBHelperFunctions().setPlayerHealth(context, this.entity as Player)
    }
}