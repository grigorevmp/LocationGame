package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Component

class DefenceComponent(
    physicalDefence : Int,
    natureForcesDefence: NatureForcesValues
) : Component(){

    class DefenceUpgrader(
        val physicalDefence: Int,
        natureForcesDefence: NatureForcesValues
    ) : Component.ComponentUpgrader<DefenceComponent>(DefenceComponent::class.java) {
        val natureForcesDefence = natureForcesDefence.values
    }

    var physicalDefence = physicalDefence
        private set

    var natureForcesDefence = natureForcesDefence.values
        private set

    override fun upgrade(context: Context, upgrader: ComponentUpgrader<Component>) {
        val defenceUpgrader = upgrader as DefenceUpgrader
        this.physicalDefence += defenceUpgrader.physicalDefence
        for (i in 0 until NatureForces.count){
            this.natureForcesDefence[i] += defenceUpgrader.natureForcesDefence[i]
        }
    }
}