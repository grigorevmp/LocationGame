package com.mikhailgrigorev.game.core.ecs.Components

import com.mikhailgrigorev.game.core.ecs.Component

class DefenceComponent(
    physicalDefencePercent : Int
) : Component(){
    var physicalDefencePercent: Int
        private set

    init {
        this.physicalDefencePercent = physicalDefencePercent
    }
}