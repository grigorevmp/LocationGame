package com.mikhailgrigorev.game.entities.sprit

import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.fsm.State

class Spirit (
    val id: Int,
    natureForcesDamage: NatureForcesValues,
    natureForcesDefenceReduce : NatureForcesValues,
    ) : Component() {

    var natureForcesDamage = natureForcesDamage.values
        private set
    var natureForcesDefenceReduce = natureForcesDefenceReduce.values
        private set

    var abilityPack = ArrayList<Ability>()
        private set

    fun addAbility(ability: Ability) {
        abilityPack.add(ability)
    }
}