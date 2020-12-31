package com.mikhailgrigorev.game.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.game.Game

class ItemsDB {
    companion object {
        private fun isItemExists(id: Int, context: Context): Boolean {
            var c: Cursor? = null
            val db = AllItemsDBHelper(context).readableDatabase
            return try {
                val query = "select * from all_items where _id = $id"
                c = db.rawQuery(query, null)
                c.moveToFirst()
            } finally {
                c?.close()
                db?.close()
            }
        }

        fun init(context: Context){
            val item1 = arrayListOf("1", "Fresh meat", "description", "${Item.equippable}")
            val item2 = arrayListOf("2", "Bones", "description", "${Item.equippable}")
            val item3= arrayListOf("3", "Rotten meat", "description", "${Item.equippable}")
            val item4 = arrayListOf("4", "Wood", "description", "${Item.equippable}")
            val item5= arrayListOf("5", "Souls", "description", "${Item.equippable}")
            createItem(context, item1)
            createItem(context, item2)
            createItem(context, item3)
            createItem(context, item4)
            createItem(context, item5)
        }

        private fun createItem(context: Context, item: List<String>) {
            val dbHelper = AllItemsDBHelper(context)
            val database = dbHelper.writableDatabase
            val contentValues = ContentValues()

            contentValues.put(AllItemsDBHelper.ID, item[0].toInt())
            contentValues.put(AllItemsDBHelper.NAME, item[1])
            contentValues.put(AllItemsDBHelper.DESC, item[2])
            contentValues.put(AllItemsDBHelper.TYPE, item[3].toInt())

            if (!isItemExists(item[0].toInt(), context)) {
                database.insert(AllItemsDBHelper.TABLE_All_ITEMS, null, contentValues)
            }
        }

        fun loadItemByID(context: Context, id: Int): Item? {
            val dbHelper = AllItemsDBHelper(context)
            val database = dbHelper.writableDatabase

            val query = "select * from all_items where _id = $id"
            val cursor = database.rawQuery(query, null)

            var enemiesIds = ""
            var item: Item? = null
            if (cursor.moveToFirst()) {
                val indexEnemyID: Int = cursor.getColumnIndex(AllItemsDBHelper.ID)
                val indexEnemyNAME: Int = cursor.getColumnIndex(AllItemsDBHelper.NAME)
                val indexEnemyDESC: Int = cursor.getColumnIndex(AllItemsDBHelper.DESC)
                val indexEnemyTYPE: Int = cursor.getColumnIndex(AllItemsDBHelper.TYPE)
                do {
                    item = Item(
                        cursor.getInt(indexEnemyID),
                        cursor.getString(indexEnemyNAME),
                        1,
                        cursor.getInt(indexEnemyTYPE)
                    )
                } while (cursor.moveToNext())
            } else Log.d("mLog", "0 rows")
            cursor.close()
            return item
        }
    }
}