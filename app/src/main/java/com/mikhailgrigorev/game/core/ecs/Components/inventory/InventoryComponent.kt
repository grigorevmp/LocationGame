package com.mikhailgrigorev.game.core.ecs.Components.inventory

import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item

class InventoryComponent : Component() {
    var items = HashMap<Int, Item>()
        private set

    fun addItem(item: Item) {
        val inventoryItem = items[item.id]
        if (inventoryItem == null) { items[item.id] = item }
        else{ inventoryItem.add(item.count) }
    }

    fun dropItemById(id: Int) {
        items.remove(id)
    }


    fun takeItem (item: Item) : Item? {
        val invItem = items[item.id]
        if (invItem != null && item.count <= invItem.count) {
            invItem.take(item.count)
            val retItem = Item(
                invItem.id,
                invItem.name,
                item.count
            )
            if (item.count == invItem.count) items.remove(item.id)
            return retItem
        }
        return null
    }

    fun getAllItems() : MutableCollection<Item> {
        return items.values
    }
}