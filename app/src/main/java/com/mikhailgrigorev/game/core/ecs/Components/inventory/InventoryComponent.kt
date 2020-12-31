package com.mikhailgrigorev.game.core.ecs.Components.inventory

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class InventoryComponent : Component() {
    private var items = HashMap<Int, Item>()

    fun addItem(item: Item) {
        val inventoryItem = items[item.id]
        if (inventoryItem == null) { items[item.id] = item }
        else{ inventoryItem.add(item.count) }
    }

    fun dropItem(item: Item) {
        if(item.id in items.keys) {
            if (items[item.id]!!.count == item.count)
                items.remove(item.id)
            else if (items[item.id]!!.count > item.count)
                items[item.id]!!.take(item.count)
        }
    }

    fun takeItem (id: Int) : Item? {
        return items[id]
    }

    fun getAllItems() : MutableCollection<Item> {
        return items.values
    }
}