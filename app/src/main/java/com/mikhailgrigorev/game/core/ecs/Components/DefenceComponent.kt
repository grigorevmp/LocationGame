package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import kotlin.random.Random

class DefenceComponent(
    physicalDefence : Int,
    natureForcesDefence: NatureForcesValues
) : Component(){

    class DefenceUpgrader(
        val physicalDefence: Int,
        natureForcesDefence: NatureForcesValues
    ) : Component.ComponentUpgrader<DefenceComponent>(DefenceComponent::class.java) {
        var natureForcesDefence = Array<Int>(NatureForces.count) {0}
            private set

        init {
            for (i in 0 until NatureForces.count){
                this.natureForcesDefence[i] = natureForcesDefence.natureForcesValues[i]
            }
        }
    }

    var physicalDefence: Int
        private set

    var natureForcesDefence = Array<Int>(NatureForces.count) {0}
        private set

    init {
        this.physicalDefence = physicalDefence
        for (i in 0 until NatureForces.count){
            this.natureForcesDefence[i] = natureForcesDefence.natureForcesValues[i]
        }
    }

    override fun upgrade(context: Context, upgrader: ComponentUpgrader<Component>) {
        val defenceUpgrader = upgrader as DefenceUpgrader
        this.physicalDefence += defenceUpgrader.physicalDefence
        for (i in 0 until NatureForces.count){
            this.natureForcesDefence[i] += defenceUpgrader.natureForcesDefence[i]
        }
    }
}