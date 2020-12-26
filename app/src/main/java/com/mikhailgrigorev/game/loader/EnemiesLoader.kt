package com.mikhailgrigorev.game.loader

import android.content.Context
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
                _x = enemy[0].toFloat(),
                _size =  enemy[1].toFloat(),
                _y =  Game.maxY + enemy[2].toFloat(),
                _id =  enemy[3].toInt(),
                _name = enemyClass[enemy[3].toInt()]!![0],
                _group = enemyClass[enemy[3].toInt()]!![1],
                _desc = enemyClass[enemy[3].toInt()]!![2],
                _bitmapId = context.resources.getIdentifier(enemyClass[enemy[3].toInt()]!![3], "drawable", context.packageName)
            )
            enemies.add(obj)
        }

    }
}