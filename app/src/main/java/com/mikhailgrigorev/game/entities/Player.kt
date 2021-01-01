package com.mikhailgrigorev.game.entities

import android.content.Context
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Weapon
import com.mikhailgrigorev.game.core.ecs.Components.inventory.InventoryComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.entities.sprit.Spirit
import com.mikhailgrigorev.game.game.Game
import com.mikhailgrigorev.game.loader.PlayerLoader

class Player(context: Context): Entity() {

    private var bitmapComponent: BitmapComponent
    private var positionComponent: PositionComponent
    private var healthComponent: HealthComponent
    private var damageComponent: DamageComponent
    private var defenceComponent: DefenceComponent
    private var equipmentComponent: EquipmentComponent
    private var inventoryComponent: InventoryComponent
    private var spirit: Spirit
    private var speed: Float = 0.2.toFloat()

    var manna: Int = 0
        private set

    var mannaMax: Int = 0
        private set

    init{
        val playerData = PlayerLoader(context)
        //manna = playerData.manna
        manna = 20
        mannaMax = playerData.mannamax
        speed = playerData.speed
        val size = playerData.size
        positionComponent = this.addComponent(PositionComponent(
            playerData.x,
            Game.maxY - size - playerData.yOffset,
            size))
        bitmapComponent = this.addComponent(BitmapComponent(
            positionComponent = positionComponent,
            context = context,
            id = playerData.id,
            name = playerData.name,
            desc = playerData.desc,
            bitmapId = playerData.bitmapId,
            group = playerData.group
        ))
        healthComponent = this.addComponent(HealthComponent(
            playerData.health, playerData.maxhealth
        ))
        damageComponent = this.addComponent(
            DamageComponent(
                playerData.damage, playerData.naturalDamageValue, playerData.cc, playerData.cm
            )
        )

        val naturalValueDef = NatureForcesValues(0, 0, 0, 0)
        defenceComponent = this.addComponent(DefenceComponent(0, naturalValueDef))

        inventoryComponent = this.addComponent(InventoryComponent())


        // _________________________________
        // _________________________________
        // _________________________________
        // ITEMS PRELOADING BETA DELETE ME
        // _________________________________
        // _________________________________
        // _________________________________
        DBHelperFunctions.createItem(
            context, arrayListOf(
                "1",
                "Fresh meat",
                "0",
                "30",
                "${Item.stackable}"
            )
        )
        DBHelperFunctions.createItem(
            context, arrayListOf(
                "2",
                "Bones",
                "0",
                "30",
                "${Item.stackable}"
            )
        )
        DBHelperFunctions.createItem(
            context, arrayListOf(
                "3",
                "Rotten meat",
                "30",
                "2",
                "${Item.stackable}"
            )
        )
        DBHelperFunctions.createItem(
            context, arrayListOf(
                "4",
                "Wood",
                "0",
                "30",
                "${Item.stackable}"
            )
        )
        DBHelperFunctions.createItem(
            context, arrayListOf(
                "5",
                "Souls",
                "0",
                "30",
                "${Item.stackable}"
            )
        )
        DBHelperFunctions.createItem(
            context, arrayListOf(
                "9",
                "Ring",
                "0",
                "1",
                "${Item.equippable}"
            )
        )
        // _________________________________
        // _________________________________
        // _________________________________
        // _________________________________
        // _________________________________
        // _________________________________

        val itemsTemp = DBHelperFunctions.loadAllItem(context)

        for (item in itemsTemp)
            inventoryComponent.addItem(item)




        equipmentComponent = this.addComponent(
            EquipmentComponent(
                weapon = Weapon(
                    520,
                    "Sword",
                    15,
                    NatureForcesValues(),
                    0,
                    0f
                )
            )
        )
        defenceComponent = this.addComponent(
            DefenceComponent(
                playerData.defence, playerData.naturalValueDef
            )
        )

        spirit = this.addComponent(Spirit())
    }

    override fun update() {
        positionComponent.update()
        bitmapComponent.update()
    }

    fun stepUp() {
        positionComponent.move(
            0f,
            -speed)
        update()
    }

}

