package com.mikhailgrigorev.game.core.ecs.Components.equipment

import com.mikhailgrigorev.game.core.ecs.Components.inventory.InventoryComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Gem
import com.mikhailgrigorev.game.core.ecs.Components.inventory.Slots
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.core.ecs.Entity

open class EquippableItem (
    id: Int,
    name: String,
    private val equippableItemType: String
    ): Item (id, name, 1, equippable) {

    var gemSlots = Slots<Gem>()
        private set

    fun equipToEntity(entity: Entity) {
        val equipment = entity.getComponent(EquipmentComponent::class.java)
        val inventory = entity.getComponent(InventoryComponent::class.java)
        if (equipment != null && inventory != null) {
            val equipmentField = EquipmentComponent::class.java.getField(equippableItemType)
            val equippingItem = inventory.takeItem(this.id)
            if (equippingItem != null) inventory.dropItem(equippingItem)
            inventory.addItem(equipmentField.get(equipment) as Item)
            equipmentField.set(equipment, this)
        }
    }

    fun takeFromEntity(entity: Entity) {
        val equipment = entity.getComponent(EquipmentComponent::class.java)
        val inventory = entity.getComponent(InventoryComponent::class.java)
        if (equipment != null && inventory != null) {
            val equipmentField = EquipmentComponent::class.java.getField(equippableItemType)
            inventory.addItem(equipmentField.get(equipment) as Item)
            equipmentField.set(equipment, null)
        }
    }
}