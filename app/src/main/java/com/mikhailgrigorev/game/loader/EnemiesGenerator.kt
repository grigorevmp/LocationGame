package com.mikhailgrigorev.game.loader

import android.content.Context
import android.database.Cursor
import android.util.Log
import com.mikhailgrigorev.game.core.data.NatureForcesValues
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.EnemyDBHelper
import com.mikhailgrigorev.game.entities.Enemy
import java.security.SecureRandom
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class EnemiesGenerator(
    private val context: Context,
                       x: Double = 0.0,
                       y: Double = 0.0,
                       oldX: Double = 0.0,
                       oldY: Double = 0.0,
                       recreate: Boolean = false,
                       addNewRecords: Boolean = false,
                       num: Int = 0) {

    var enemies = ArrayList<Entity>()
        private set

    private val classesFileName = "MapData/Enemies/classes.csv"

    val AB = "0123456789"
    var rnd: SecureRandom = SecureRandom()

    private fun isPointInCircle(x: Double,
                        y: Double,
                        oldX: Double,
                        oldY: Double,
                        radius: Double): Boolean {
        return sqrt((x-oldX)*(x-oldX) + (y-oldY)*(y-oldY)) < radius
    }

    private fun generateUUID(len: Int): String {
        rnd.setSeed(System.currentTimeMillis())
        val sb = StringBuilder(len)
        for (i in 0 until len) sb.append(AB[rnd.nextInt(AB.length)])
        return sb.toString()
    }

    private fun getRandomArrayElement(array: List<Int>): Int {
        val list = array.filter { it % 2 == 0 }
        return list.shuffled().take(1)[0]
    }

    private fun generateEnemy(x: Double, y: Double): ArrayList<String>{
        val enemy: ArrayList<String> = ArrayList()

        // Items num
        // Enemies IDs -> MapData/Enemies/classes.csv
        val enemiesID = listOf(0, 1, 0, 1)
        val enID = getRandomArrayElement(enemiesID).toString()
        enemy.add(enID)

        // Multiple - 0,1
        val multiple = listOf(0)
        enemy.add(getRandomArrayElement(multiple).toString())


        val random1: Double = (0.0001 + Math.random() * (0.0040 - 0.0001))
        var coeff = 1
        if(Math.random() < 0.5)
            coeff = -1

        // X
        val zero_x = x + (random1*coeff)
        enemy.add(zero_x.toString())


        val random2: Double = (0.0001 + Math.random() * (0.0040 - 0.0001))
        var coeff2 = 1
        if(Math.random() < 0.5)
            coeff2 = -1

        // Y
        val zero_y = y + (random2*coeff2)
        enemy.add(zero_y.toString())

        // SIZE
        val size = listOf(2)
        enemy.add(getRandomArrayElement(size).toString())

        // ID
        var id = generateUUID(8).toInt()
        while (DBHelperFunctions.isEnemyExists(id, context)){
            id = generateUUID(10).toInt()
        }
        enemy.add(id.toString())

        // HEALTH
        var randomNumber:Int = (-50..50).shuffled()[0]
        val health = 300 + randomNumber
        enemy.add(health.toString())

        // DMG
        randomNumber = (-5..5).shuffled()[0]
        val dmg = 5 + randomNumber
        enemy.add(dmg.toString())

        // AIR
        randomNumber = (0..5).shuffled()[0]
        val air = 0 + randomNumber
        enemy.add(air.toString())

        // WATER
        randomNumber = (0..5).shuffled()[0]
        val water = 0 + randomNumber
        enemy.add(water.toString())

        // EARTH
        randomNumber = (0..5).shuffled()[0]
        val earth = 0 + randomNumber
        enemy.add(earth.toString())

        // FIRE
        randomNumber = (0..5).shuffled()[0]
        val fire = 0 + randomNumber
        enemy.add(fire.toString())

        // CC
        randomNumber = (0..2).shuffled()[0]
        val cc = 0 + randomNumber
        enemy.add(cc.toString())

        // CM
        randomNumber = (0..2).shuffled()[0]
        val cm = 0 + randomNumber
        enemy.add(cm.toString())

        // DEFENCE
        randomNumber = (-5..5).shuffled()[0]
        val def = 5 + randomNumber
        enemy.add(def.toString())

        // AIR
        randomNumber = (0..5).shuffled()[0]
        val defAir = 0 + randomNumber
        enemy.add(defAir.toString())

        // WATER
        randomNumber = (0..5).shuffled()[0]
        val defWater = 0 + randomNumber
        enemy.add(defWater.toString())

        // EARTH
        randomNumber = (0..5).shuffled()[0]
        val defEarth = 0 + randomNumber
        enemy.add(defEarth.toString())

        // FIRE
        randomNumber = (0..5).shuffled()[0]
        val defFire = 0 + randomNumber
        enemy.add(defFire.toString())

        ////////////////////////////////////
        // CHECK THIS
        ////////////////////////////////////

        // ITEMS
        var items = ""
        if(enID == "0"){
            items = "2"
        }
        else if (enID == "1"){
            items = "3"
        }

        // ITEMS NUM
        randomNumber = (0..5).shuffled()[0]
        val itemsNum = randomNumber.toString()

        enemy.add(items)
        enemy.add(itemsNum)

        return enemy

    }

    init{
        // Data files
        val classesData = CSVReader(context, classesFileName).data
        val data = java.util.ArrayList<Array<String>>()

       // Database writing TEST
       val dbHelper = EnemyDBHelper(context)
       val database = dbHelper.writableDatabase

        if(recreate){
            database.delete(EnemyDBHelper.TABLE_ENEMIES, null, null);
        }

        if(addNewRecords) {
            for (enemy in 1..num) {
                val newEnemy = generateEnemy(x, y)
                val radius = 0.0040
                Log.d("DISTANCE - Spawning", "STARTED")
                if((isPointInCircle(newEnemy[2].toDouble(), newEnemy[3].toDouble(), oldX, oldY, radius)) and !recreate) {
                    Log.d("DISTANCE - Spawning", "IN CIRCLE")
                    continue
                }
                Log.d("DISTANCE - Spawning", "TRUE")
                DBHelperFunctions.spawnEnemy(context, newEnemy)
            }
        }


        // Database reading TEST
        val cursor: Cursor =
            database.query(EnemyDBHelper.TABLE_ENEMIES, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            val indexEnemyID   : Int  = cursor.getColumnIndex(EnemyDBHelper.EnemyID)
            val indexmultiple  : Int  = cursor.getColumnIndex(EnemyDBHelper.multiple)
            val indexX         : Int  = cursor.getColumnIndex(EnemyDBHelper.X)
            val indexY         : Int  = cursor.getColumnIndex(EnemyDBHelper.Y)
            val indexSIZE      : Int  = cursor.getColumnIndex(EnemyDBHelper.SIZE)
            val indexID        : Int  = cursor.getColumnIndex(EnemyDBHelper.ID)
            val indexHEALTH    : Int  = cursor.getColumnIndex(EnemyDBHelper.HEALTH)
            val indexDMG       : Int  = cursor.getColumnIndex(EnemyDBHelper.DMG)
            val indexAIR       : Int  = cursor.getColumnIndex(EnemyDBHelper.AIR)
            val indexWATER     : Int  = cursor.getColumnIndex(EnemyDBHelper.WATER)
            val indexEARTH     : Int  = cursor.getColumnIndex(EnemyDBHelper.EARTH)
            val indexFIRE      : Int  = cursor.getColumnIndex(EnemyDBHelper.FIRE)
            val indexCC        : Int  = cursor.getColumnIndex(EnemyDBHelper.CC)
            val indexCM        : Int  = cursor.getColumnIndex(EnemyDBHelper.CM)
            val indexDEFENCE   : Int  = cursor.getColumnIndex(EnemyDBHelper.DEFENCE)
            val indexAIR2      : Int  = cursor.getColumnIndex(EnemyDBHelper.AIR2)
            val indexWATER2    : Int  = cursor.getColumnIndex(EnemyDBHelper.WATER2)
            val indexEARTH2    : Int  = cursor.getColumnIndex(EnemyDBHelper.EARTH2)
            val indexFIRE2     : Int  = cursor.getColumnIndex(EnemyDBHelper.FIRE2)
            val indexSpecial   : Int  = cursor.getColumnIndex(EnemyDBHelper.Special)
            val indexITEMS     : Int  = cursor.getColumnIndex(EnemyDBHelper.ITEMS)
            val indexITEMSNUM  : Int  = cursor.getColumnIndex(EnemyDBHelper.ITEMSNUM)
            do {
                data.add(
                    arrayOf(
                        cursor.getString(indexEnemyID),
                        cursor.getString(indexmultiple),
                        cursor.getString(indexX),
                        cursor.getString(indexY),
                        cursor.getString(indexSIZE),
                        cursor.getString(indexID),
                        cursor.getString(indexHEALTH),
                        cursor.getString(indexDMG),
                        cursor.getString(indexAIR),
                        cursor.getString(indexWATER),
                        cursor.getString(indexEARTH),
                        cursor.getString(indexFIRE),
                        cursor.getString(indexCC),
                        cursor.getString(indexCM),
                        cursor.getString(indexDEFENCE),
                        cursor.getString(indexAIR2),
                        cursor.getString(indexWATER2),
                        cursor.getString(indexEARTH2),
                        cursor.getString(indexFIRE2),
                        cursor.getString(indexITEMS),
                        cursor.getString(indexITEMSNUM),
                        cursor.getString(indexSpecial)
                    )
                )
            } while (cursor.moveToNext())
        } else Log.d("mLog", "0 rows")
        cursor.close()

        // Load enemy classes
        val enemyClass = mutableMapOf<Int, List<String>>()
        for(enClass in classesData)
            enemyClass[enClass[0].toInt()] = listOf(enClass[1], enClass[2], enClass[3], enClass[4])

        // Load enemy data, link it with class and create Enemy
        for (enemy in data){
            val obj = Enemy(
                context,
                _multiple = enemy[1].toInt(),
                _x = enemy[2].toDouble(),
                _y = enemy[3].toDouble(),
                _size = enemy[4].toFloat(),
                _id = enemy[5].toInt(),
                _name = enemyClass[enemy[0].toInt()]!![0],
                _group = enemyClass[enemy[0].toInt()]!![1],
                _desc = enemyClass[enemy[0].toInt()]!![2],
                _bitmapId = context.resources.getIdentifier(
                    enemyClass[enemy[0].toInt()]!![3],
                    "drawable",
                    context.packageName
                ),
                _health = enemy[6].toInt(),
                _damage = enemy[7].toInt(),
                _cc = enemy[12].toInt(),
                _cm = enemy[13].toFloat(),
                _defence = enemy[14].toInt(),
                _naturalDamageValue = NatureForcesValues(
                    enemy[8].toInt(),
                    enemy[9].toInt(),
                    enemy[10].toInt(),
                    enemy[11].toInt()
                ),
                _naturalValueDef = NatureForcesValues(
                    enemy[15].toInt(),
                    enemy[16].toInt(),
                    enemy[17].toInt(),
                    enemy[18].toInt()
                ),
                items = enemy[19],
                itemsNum = enemy[20]
            )
            enemies.add(obj)
        }

    }
}