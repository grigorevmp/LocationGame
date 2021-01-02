package com.mikhailgrigorev.game.core.ecs.Components.inventory

import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class Slots<Thing : Item> {
    private var things = ArrayList<Thing>()

    fun addThing(thing: Thing) {
        things.add(thing)
    }

    fun takeThing(id: Int) : Thing? {
        return things.find { it.id == id }
    }
}