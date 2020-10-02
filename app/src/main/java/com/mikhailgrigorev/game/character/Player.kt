package com.mikhailgrigorev.game.character

import android.content.Context
import com.mikhailgrigorev.game.game.GameView
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent

import com.mikhailgrigorev.game.core.ecs.Entity



class Player(context: Context): Entity() {

    init{
        var bitmapComponent = this.addComponent(BitmapComponent(
            id = 0,
            desc = "Player",
            size = 5f,
            speed = 0.2.toFloat(),
            bitmapId = R.drawable.ship
        ))
        bitmapComponent.init(context)
        this.addComponent(PositionComponent(7f,GameView.maxY - bitmapComponent.size - 1))
    }

    override fun update() {
        var bitmapComponent = this.getComponent(BitmapComponent::class.java)
        var positionComponent = this.getComponent(PositionComponent::class.java)
        positionComponent!!.rect().set(
            positionComponent.x()*GameView.unitW,
            positionComponent.y()*GameView.unitH,
            (positionComponent.x()+bitmapComponent!!.size)*GameView.unitW,
            (positionComponent.y()+bitmapComponent.size)*GameView.unitH)
    }

    fun stepUp() {
        this.getComponent(PositionComponent::class.java)!!.move(0f, this.getComponent(BitmapComponent::class.java)!!.speed)
        update()
    }

}

