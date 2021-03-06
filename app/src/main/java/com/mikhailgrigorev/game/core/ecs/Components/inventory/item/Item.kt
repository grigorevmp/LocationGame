package com.mikhailgrigorev.game.core.ecs.Components.inventory.item

import com.mikhailgrigorev.game.core.ecs.Entity

open class Item (id: Int, name: String, count: Int, val type: Int = none) : Entity() {
    companion object {
        const val none = 0b00
        const val stackable = 0b10
        const val equippable = 0b01

        fun isType(item: Item,typeCheckValue: Int):  Boolean{
            return ((item.type and typeCheckValue) != 0)
        }
        fun isType(type: Int, typeCheckValue: Int):  Boolean{
            return ((type and typeCheckValue) != 0)
        }
    }
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

    fun isType(typeCheckValue: Int):  Boolean{
        return ((type and typeCheckValue) != 0)
    }
}