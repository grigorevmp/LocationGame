package com.mikhailgrigorev.game.core.ecs.Components.inventory.item

class Gem (
    id: Int,
    name: String,
    count: Int
    ) : Item(id, name, count, Item.stackable) {

}