package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Entity

class UpgradeComponent : Component() {
    private var upgraders = ArrayList<ComponentUpgrader<out Component>>()

    fun <_Component : Component> addUpgrader(upgrader: ComponentUpgrader<_Component>) {
        this.upgraders.add(upgrader)
    }

    fun upgrade(context: Context,entity: Entity){
        for (upgrader in this.upgraders) {
            entity.getComponent(upgrader.improvingComponent)?.upgrade(context, upgrader as ComponentUpgrader<Component>)
        }
    }
}