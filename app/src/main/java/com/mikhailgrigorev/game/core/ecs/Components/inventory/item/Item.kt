package com.mikhailgrigorev.game.core.ecs.Components.inventory.item

open class Item (id: Int, name: String, count: Int) {
    var id = id
        private set
    var name = name
        private set
    var count = count
        private set

    fun add(count: Int) {
        this.count += count
    }
    fun take(count: Int) : Boolean {
        if (count > this.count) return false
        this.count -= count
        return true
    }
}