package com.mikhailgrigorev.game.loader

import android.content.Context
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.game.Game

class EnemiesLoader(context: Context) {
    /**
     * ENEMY CLASS
     * format: ID, NAME, CLASS, DESCRIPTION, BITMAP
     * 0,Skeleton,skeleton,Bones,devil
     *
     * ENEMY DATA
     * format: X,SIZE,Y,ID
     * sample: 17f,2f,-16,0
     */


    /**
     * ENEMY CLASS [26.12.2020]
     * format: EnemyID, NAME, CLASS, DESCRIPTION, BITMAP_HEADER
     *
     * +0 EnemyID
     * +1 NAME
     * +2 CLASS
     * +3 DESCRIPTION
     * +4 BITMAP_HEADER
     *
     * 0,Skeleton,skeleton,Bones,skeleton
     *
     * ENEMY DATA
     * format: EnemyID,multiple,X,Y,SIZE,ID,HEALTH,DMG,AIR,WATER,EARTH,FIRE,CC,CM,DEFENCE,AIR,WATER,EARTH,FIRE
     *
     * +0 EnemyID
     * 1 multiple
     * +2 X
     * +3 Y
     * +4 SIZE
     * +5 ID
     * +6 HEALTH
     * +7 DMG
     * +8 AIR
     * +9 WATER
     * +10 EARTH
     * +11 FIRE
     * +12 CC
     * +13 CM
     * +14 DEFENCE
     * +15 AIR
     * +16 WATER
     * +17 EARTH
     * +18 FIRE
     *
     * example:
     */

    var enemies = ArrayList<Entity>()
        private set

    private val classesFileName = "MapData/Enemies/classes.csv"
    private val dataFileName = "MapData/Enemies/data.csv"

    init{
        // Data files
        val classesData = CSVReader(context, classesFileName).data
        val data = CSVReader(context, dataFileName).data

        // Load enemy classes
        val enemyClass = mutableMapOf<Int , List<String>>()
        for(enClass in classesData)
            enemyClass[enClass[0].toInt()] = listOf(enClass[1], enClass[2], enClass[3], enClass[4])

        // Load enemy data, link it with class and create Enemy
        for (enemy in data){
            val obj = Enemy(
                context,
                _multiple = enemy[1].toInt(),
                _x = enemy[2].toFloat(),
                _y =  Game.maxY + enemy[3].toFloat(),
                _size =  enemy[4].toFloat(),
                _id =  enemy[5].toInt(),
                _name = enemyClass[enemy[0].toInt()]!![0],
                _group = enemyClass[enemy[0].toInt()]!![1],
                _desc = enemyClass[enemy[0].toInt()]!![2],
                _bitmapId = context.resources.getIdentifier(enemyClass[enemy[0].toInt()]!![3], "drawable", context.packageName),
                _health =  enemy[6].toInt(),
                _damage = enemy[7].toInt(),
                _cc = enemy[12].toInt(),
                _cm = enemy[13].toFloat(),
                _defence =  enemy[14].toInt(),
                _naturalDamageValue = NatureForcesValues(
                    enemy[8].toInt(),
                    enemy[9].toInt(),
                    enemy[10].toInt(),
                    enemy[11].toInt()),
                _naturalValueDef = NatureForcesValues(
                    enemy[15].toInt(),
                    enemy[16].toInt(),
                    enemy[17].toInt(),
                    enemy[18].toInt())
            )
            enemies.add(obj)
        }

    }
}