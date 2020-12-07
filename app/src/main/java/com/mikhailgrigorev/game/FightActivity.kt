package com.mikhailgrigorev.game

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import com.mikhailgrigorev.game.activities.MainActivity
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.loader.EnemiesLoader
import kotlinx.android.synthetic.main.activity_fight.*

class FightActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_fight)

        val player = Player(this)
        var enemy: Enemy? = null

        // AUTH TO MAIN
        menuBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Set", "Game")
            startActivity(intent)
            finish()
        }


        val enemyId = if(intent.getStringExtra("enemyId") != null) {
            intent.getStringExtra("enemyId")
        } else{
            "-1"
        }


        enemy = findEnemy(enemyId)


        // Exit if data not exists
        if ((enemy == null) or (enemyId == "-1"))
            exit()


        logEnemyIdText("Fighting with #${enemy!!.getComponent(BitmapComponent::class.java)!!._name}")

        updateAllHealthText(
            enemy.getComponent(HealthComponent::class.java)!!.healthPoints.toString(),
            player.getComponent(HealthComponent::class.java)!!.healthPoints.toString())


        // listeners

        fightButton.setOnClickListener {
            fight()
        }

    }

    private fun findEnemy(enemyId: String): Enemy? {
        val enemiesLoader = EnemiesLoader(this)
        for (enemyIterator in enemiesLoader.enemies){
            if(enemyIterator.getComponent(BitmapComponent::class.java)!!._id.toString() == enemyId) {
                return enemyIterator as Enemy
            }
        }
        return null
    }

    private fun exit(errorCode: Int = -1){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Set", "Game")
        startActivity(intent)
        finish()
    }


    private fun logEnemyIdText(message: String){
        header.text = message
    }

    private fun updateAllHealthText(valuePlayer: String, valueEnemy: String){
        if (valuePlayer.toInt() < 0)
            updatePlayerHealthText("0")
        else
            updatePlayerHealthText(valuePlayer)
        if (valuePlayer.toInt() < 0)
            updateHealthText("0")
        else
            updateHealthText(valueEnemy)
    }

    private fun updatePlayerHealthText(value: String){
        playerHPValue.text = value
    }

    private fun updateHealthText(value: String){
        healthValue.text = value
    }

    private fun fight(){

    }


    override fun onKeyUp(keyCode: Int, msg: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("Set", "Game")
                startActivity(intent)
                finish()
            }
        }
        return false
    }


}