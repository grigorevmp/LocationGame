package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForces
import com.mikhailgrigorev.game.core.ecs.Components.Data.NatureForcesValues
import java.lang.annotation.Native
import kotlin.random.Random

class DefenceComponent(
    physicalDefence : Int,
    natureForcesDefence: NatureForcesValues
) : Component(){

    class DefenceUpgrader : Component.ComponentUpgrader<DefenceComponent>(DefenceComponent::class.java) {
        val physicalDefence = Random.nextInt(0,10)
        val natureForcesDefenceArray = Array<Int>(NatureForces.count) { Random.nextInt(0,10)}
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

    override fun upgrade(upgrader: ComponentUpgrader<Component>) {
        val defenceUpgrader = upgrader as DefenceUpgrader
        this.physicalDefence += defenceUpgrader.physicalDefence
        for (i in 0 until NatureForces.count){
            this.natureForcesDefence[i] += defenceUpgrader.natureForcesDefenceArray[i]
        }
    }
}