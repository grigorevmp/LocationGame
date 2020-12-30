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

    fun dropItemById(id: Int) {
        items.remove(id)
    }


    fun takeItem (item: Item) : Item? {
        return items[item.id]
    }

    fun getAllItems() : MutableCollection<Item> {
        return items.values
    }
}