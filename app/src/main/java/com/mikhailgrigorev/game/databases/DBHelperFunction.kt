package com.mikhailgrigorev.game.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquippableItem
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Armor
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Jewelry
import com.mikhailgrigorev.game.core.ecs.Components.equipment.equipmentTypes.Weapon
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.game.Game

class DBHelperFunctions {
    companion object {

        // IS DATABASE OBJECT EXISTS
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        private fun isEnemyExists(id: Int, context: Context): Boolean {
            var c: Cursor? = null
            val db = EnemyDBHelper(context).readableDatabase
            return try {
                val query = "select * from enemies where _id = $id"
                c = db.rawQuery(query, null)
                c.moveToFirst()
            } finally {
                c?.close()
                db?.close()
            }
        }

        private fun isTotemExists(id: Int, context: Context): Boolean {
            var c: Cursor? = null
            val db = TotemDBHelper(context).readableDatabase
            return try {
                val query = "select * from totem where _id = $id"
                c = db.rawQuery(query, null)
                c.moveToFirst()
            } finally {
                c?.close()
                db?.close()
            }
        }

        private fun isItemExists(id: Int, context: Context): Boolean {
            var c: Cursor? = null
            val db = ItemsDBHelper(context).readableDatabase
            return try {
                val query = "select * from items where _id = $id"
                c = db.rawQuery(query, null)
                c.moveToFirst()
            } finally {
                c?.close()
                db?.close()
            }
        }
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------

        // "FUNCTIONS FOR TEST" BLOCK
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        fun restorePlayerHealth(context: Context, player: Player) {
            val playerHealthComponent = player.getComponent(HealthComponent::class.java)
            playerHealthComponent!!.upgrade(
                context, HealthComponent.HealthUpgrader(
                    playerHealthComponent.maxHealthPoints - playerHealthComponent.healthPoints,
                    0
                ) as Component.ComponentUpgrader<Component>
            )
        }

        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------

        // ENEMY FUNCTIONS
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        fun loadEnemyIDByXY(context: Context, x: Int, y: Int): String {
            val dbHelper = EnemyDBHelper(context)
            val database = dbHelper.writableDatabase

            val query = "select * from enemies where x = $x AND y = ${-Game.maxY + y}"
            val cursor = database.rawQuery(query, null)

            var enemiesIds = ""

            if (cursor.moveToFirst()) {
                val indexEnemyID: Int = cursor.getColumnIndex(EnemyDBHelper.ID)
                do {
                    enemiesIds += cursor.getInt(indexEnemyID)
                    enemiesIds += ","
                } while (cursor.moveToNext())
            } else Log.d("mLog", "0 rows")
            enemiesIds = enemiesIds.substring(0, enemiesIds.length - 1)
            cursor.close()
            return enemiesIds
        }

        fun spawnEnemy(context: Context, enemy: List<String>) {
            val dbHelper = EnemyDBHelper(context)
            val database = dbHelper.writableDatabase
            val contentValues = ContentValues()

            contentValues.put(EnemyDBHelper.EnemyID, enemy[0].toInt())
            contentValues.put(EnemyDBHelper.multiple, enemy[1].toInt())
            contentValues.put(EnemyDBHelper.X, enemy[2].toInt())
            contentValues.put(EnemyDBHelper.Y, enemy[3].toInt())
            contentValues.put(EnemyDBHelper.SIZE, enemy[4].toInt())
            contentValues.put(EnemyDBHelper.ID, enemy[5].toInt())
            contentValues.put(EnemyDBHelper.HEALTH, enemy[6].toInt())
            contentValues.put(EnemyDBHelper.DMG, enemy[7].toInt())
            contentValues.put(EnemyDBHelper.AIR, enemy[8].toInt())
            contentValues.put(EnemyDBHelper.WATER, enemy[9].toInt())
            contentValues.put(EnemyDBHelper.EARTH, enemy[10].toInt())
            contentValues.put(EnemyDBHelper.FIRE, enemy[11].toInt())
            contentValues.put(EnemyDBHelper.CC, enemy[12].toInt())
            contentValues.put(EnemyDBHelper.CM, enemy[13].toInt())
            contentValues.put(EnemyDBHelper.DEFENCE, enemy[14].toInt())
            contentValues.put(EnemyDBHelper.AIR2, enemy[15].toInt())
            contentValues.put(EnemyDBHelper.WATER2, enemy[16].toInt())
            contentValues.put(EnemyDBHelper.EARTH2, enemy[17].toInt())
            contentValues.put(EnemyDBHelper.FIRE2, enemy[18].toInt())
            contentValues.put(EnemyDBHelper.ITEMS, enemy[19].toInt())
            contentValues.put(EnemyDBHelper.ITEMSNUM, enemy[20].toInt())
            contentValues.put(EnemyDBHelper.Special, 0)

            if (!isEnemyExists(enemy[5].toInt(), context)) {
                database.insert(EnemyDBHelper.TABLE_ENEMIES, null, contentValues)
            }
        }

        fun setEnemyHealth(context: Context, enemy: Enemy) {
            val dbHelper = EnemyDBHelper(context)
            val database = dbHelper.writableDatabase
            val enemyId = enemy.getComponent(BitmapComponent::class.java)!!._id
            val contentValues = ContentValues()

            contentValues.put(
                EnemyDBHelper.HEALTH,
                enemy.getComponent(HealthComponent::class.java)!!.healthPoints
            )
            database.update(EnemyDBHelper.TABLE_ENEMIES, contentValues, "_id = $enemyId", null)
        }

        fun deleteEnemy(context: Context, enemy: Enemy) {
            val dbHelper = EnemyDBHelper(context)
            val database = dbHelper.writableDatabase
            val enemyId = enemy.getComponent(BitmapComponent::class.java)!!._id

            database.delete(EnemyDBHelper.TABLE_ENEMIES, "_id = $enemyId", null)
        }
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------

        // ENEMY FUNCTIONS
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        fun createItem(context: Context, item: List<String>) {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase
            val contentValues = ContentValues()

            contentValues.put(ItemsDBHelper.ID, item[0].toInt())
            contentValues.put(ItemsDBHelper.NAME, item[1])
            contentValues.put(ItemsDBHelper.DAMAGE, item[2].toInt())
            contentValues.put(ItemsDBHelper.COUNT, item[3].toInt())
            contentValues.put(ItemsDBHelper.TYPE, item[4].toInt())
            contentValues.put(ItemsDBHelper.EQTYPE, item[5])
            contentValues.put(ItemsDBHelper.VALUE, item[6])
            contentValues.put(ItemsDBHelper.ISE, item[7].toInt())

            if (!isItemExists(item[0].toInt(), context)) {
                database.insert(ItemsDBHelper.TABLE_ITEMS, null, contentValues)
            }
        }

        fun dropItem(context: Context, id: Int) {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase
            database.delete(ItemsDBHelper.TABLE_ITEMS, "_id = $id", null)
        }

        fun equipItem(context: Context, id: Int) {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase

            val contentValues = ContentValues()
            contentValues.put(ItemsDBHelper.ISE, 1)
            database.update(ItemsDBHelper.TABLE_ITEMS, contentValues, "_id = $id", null)
        }

        fun unEquipItem(context: Context, id: Int) {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase

            val contentValues = ContentValues()
            contentValues.put(ItemsDBHelper.ISE, 0)
            database.update(ItemsDBHelper.TABLE_ITEMS, contentValues, "_id = $id", null)
        }

        fun replaceItem(context: Context, id: Int, count: Int) {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase

            val contentValues = ContentValues()
            contentValues.put(ItemsDBHelper.COUNT, count)
            database.update(ItemsDBHelper.TABLE_ITEMS, contentValues, "_id = $id", null)
        }
        fun loadEquippedItem(context: Context): ArrayList<EquippableItem> {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase

            // Database reading TEST
            val cursor: Cursor =
                database.query(ItemsDBHelper.TABLE_ITEMS, null, null, null, null, null, null)

            val items: ArrayList<EquippableItem> = ArrayList()

            if (cursor.moveToFirst()) {
                val indexID: Int = cursor.getColumnIndex(ItemsDBHelper.ID)
                val indexNAME: Int = cursor.getColumnIndex(ItemsDBHelper.NAME)
                val indexEQT: Int = cursor.getColumnIndex(ItemsDBHelper.EQTYPE)
                val indexValue: Int = cursor.getColumnIndex(ItemsDBHelper.VALUE)
                val indexISE: Int = cursor.getColumnIndex(ItemsDBHelper.ISE)
                do {
                    if(cursor.getInt(indexISE) == 1) {
                            if (cursor.getString(indexEQT) == "jewelry") {
                                val values = cursor.getString(indexValue).split('.')
                                items.add(
                                    Jewelry(
                                        cursor.getInt(indexID),
                                        cursor.getString(indexNAME),
                                        EquipmentComponent::ring.name,
                                        values[0].toInt(),
                                        NatureForcesValues(
                                            values[1].toInt(),
                                            values[2].toInt(),
                                            values[3].toInt(),
                                            values[4].toInt()
                                        ),
                                        values[5].toInt(),
                                        NatureForcesValues(
                                            values[6].toInt(),
                                            values[7].toInt(),
                                            values[8].toInt(),
                                            values[9].toInt()
                                        ),
                                        values[10].toInt(),
                                        values[11].toFloat()
                                    )
                                )
                            }
                            if (cursor.getString(indexEQT) == "armor") {
                                val values = cursor.getString(indexValue).split('.')
                                items.add(
                                    Armor(
                                        cursor.getInt(indexID),
                                        cursor.getString(indexNAME),
                                        EquipmentComponent::armor.name,
                                        values[0].toInt(),
                                        NatureForcesValues(
                                            values[1].toInt(),
                                            values[2].toInt(),
                                            values[3].toInt(),
                                            values[4].toInt()
                                        )
                                    )
                                )
                            }
                            if (cursor.getString(indexEQT) == "weapon") {
                                val values = cursor.getString(indexValue).split('.')
                                items.add(
                                    Weapon(
                                        cursor.getInt(indexID),
                                        cursor.getString(indexNAME),
                                        EquipmentComponent::weapon.name,
                                        values[0].toInt(),
                                        NatureForcesValues(
                                            values[1].toInt(),
                                            values[2].toInt(),
                                            values[3].toInt(),
                                            values[4].toInt()
                                        ),
                                        values[5].toInt(),
                                        values[6].toFloat()
                                    )
                                )
                            }
                    }
                } while (cursor.moveToNext())
            } else Log.d("mLog", "0 rows")
            cursor.close()
            return items
        }
        fun loadAllItem(context: Context): ArrayList<Item> {
            val dbHelper = ItemsDBHelper(context)
            val database = dbHelper.writableDatabase

            // Database reading TEST
            val cursor: Cursor =
                database.query(ItemsDBHelper.TABLE_ITEMS, null, null, null, null, null, null)

            val items: ArrayList<Item> = ArrayList()

            if (cursor.moveToFirst()) {
                val indexID: Int = cursor.getColumnIndex(ItemsDBHelper.ID)
                val indexNAME: Int = cursor.getColumnIndex(ItemsDBHelper.NAME)
                val indexDAMAGE: Int = cursor.getColumnIndex(ItemsDBHelper.DAMAGE)
                val indexCOUNT: Int = cursor.getColumnIndex(ItemsDBHelper.COUNT)
                val indexTYPE: Int = cursor.getColumnIndex(ItemsDBHelper.TYPE)
                val indexEQT: Int = cursor.getColumnIndex(ItemsDBHelper.EQTYPE)
                val indexValue: Int = cursor.getColumnIndex(ItemsDBHelper.VALUE)
                val indexISE: Int = cursor.getColumnIndex(ItemsDBHelper.ISE)
                do {
                    if(cursor.getInt(indexISE) == 0) {
                        val type = cursor.getInt(indexTYPE)
                        if (Item.isType(type, Item.equippable)) {
                            if (cursor.getString(indexEQT) == "jewelry") {
                                val values = cursor.getString(indexValue).split('.')
                                items.add(
                                    Jewelry(
                                        cursor.getInt(indexID),
                                        cursor.getString(indexNAME),
                                        EquipmentComponent::ring.name,
                                        values[0].toInt(),
                                        NatureForcesValues(
                                            values[1].toInt(),
                                            values[2].toInt(),
                                            values[3].toInt(),
                                            values[4].toInt()
                                        ),
                                        values[5].toInt(),
                                        NatureForcesValues(
                                            values[6].toInt(),
                                            values[7].toInt(),
                                            values[8].toInt(),
                                            values[9].toInt()
                                        ),
                                        values[10].toInt(),
                                        values[11].toFloat()
                                    )
                                )
                            }
                            if (cursor.getString(indexEQT) == "armor") {
                                val values = cursor.getString(indexValue).split('.')
                                items.add(
                                    Armor(
                                        cursor.getInt(indexID),
                                        cursor.getString(indexNAME),
                                        EquipmentComponent::armor.name,
                                        values[0].toInt(),
                                        NatureForcesValues(
                                            values[1].toInt(),
                                            values[2].toInt(),
                                            values[3].toInt(),
                                            values[4].toInt()
                                        )
                                    )
                                )
                            }
                            if (cursor.getString(indexEQT) == "weapon") {
                                val values = cursor.getString(indexValue).split('.')
                                items.add(
                                    Weapon(
                                        cursor.getInt(indexID),
                                        cursor.getString(indexNAME),
                                        EquipmentComponent::weapon.name,
                                        values[0].toInt(),
                                        NatureForcesValues(
                                            values[1].toInt(),
                                            values[2].toInt(),
                                            values[3].toInt(),
                                            values[4].toInt()
                                        ),
                                        values[5].toInt(),
                                        values[6].toFloat()
                                    )
                                )
                            }
                        } else {
                            items.add(
                                Item(
                                    cursor.getInt(indexID),
                                    cursor.getString(indexNAME),
                                    cursor.getInt(indexCOUNT),
                                    cursor.getInt(indexTYPE)
                                )
                            )
                        }
                    }
                } while (cursor.moveToNext())
            } else Log.d("mLog", "0 rows")
            cursor.close()
            return items
        }
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------

        // TOTEMS
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        fun spawnTotems(context: Context, totem: List<String>) {
            val dbHelper = TotemDBHelper(context)
            val database = dbHelper.writableDatabase
            val contentValues = ContentValues()


            contentValues.put(TotemDBHelper.X, totem[0].toInt())
            contentValues.put(TotemDBHelper.SIZE, totem[1].toInt())
            contentValues.put(TotemDBHelper.Y, totem[2].toInt())
            contentValues.put(TotemDBHelper.ID, totem[3].toInt())
            contentValues.put(TotemDBHelper.ENEMYID, totem[4].toInt())
            contentValues.put(TotemDBHelper.HEALTH, totem[5].toInt())
            contentValues.put(TotemDBHelper.DMG, totem[6].toInt())
            contentValues.put(TotemDBHelper.AIR, totem[7].toInt())
            contentValues.put(TotemDBHelper.WATER, totem[8].toInt())
            contentValues.put(TotemDBHelper.EARTH, totem[10].toInt())
            contentValues.put(TotemDBHelper.FIRE, totem[9].toInt())
            contentValues.put(TotemDBHelper.DEFENCE, totem[11].toInt())
            contentValues.put(TotemDBHelper.AIR2, totem[12].toInt())
            contentValues.put(TotemDBHelper.WATER2, totem[13].toInt())
            contentValues.put(TotemDBHelper.EARTH2, totem[15].toInt())
            contentValues.put(TotemDBHelper.FIRE2, totem[14].toInt())
            contentValues.put(TotemDBHelper.ITEMS, totem[16].toInt())
            contentValues.put(TotemDBHelper.ITEMSNUM, totem[17].toInt())

            if (!isTotemExists(totem[3].toInt(), context)) {
                database.insert(TotemDBHelper.TABLE_TOTEM, null, contentValues)
            }
        }
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------

        // PLAYERS
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        fun setPlayerHealth(context: Context, player: Player) {
            val dbHelper = PlayerDBHelper(context)
            val database = dbHelper.writableDatabase
            val playerId = player.getComponent(BitmapComponent::class.java)!!._id
            val contentValues = ContentValues()

            val playerHealthComponent = player.getComponent(HealthComponent::class.java)
            contentValues.put(
                PlayerDBHelper.HEALTH,
                playerHealthComponent!!.healthPoints
            )
            contentValues.put(
                PlayerDBHelper.MAXHEALTH,
                playerHealthComponent.maxHealthPoints
            )
            database.update(PlayerDBHelper.TABLE_PLAYER, contentValues, "_id = $playerId", null)
        }
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------
        // ---------------------------------------------------------------

    }
}