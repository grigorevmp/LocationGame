package com.mikhailgrigorev.game

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mikhailgrigorev.game.activities.MainActivity
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.fsm.FSM
import com.mikhailgrigorev.game.core.fsm.State
import com.mikhailgrigorev.game.core.fsm.Transition
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.loader.EnemiesLoader
import kotlinx.android.synthetic.main.activity_fight.*


class FightActivity : AppCompatActivity() {
    enum class ButtonType {
        None,
        Attack,
        PhysicalAttack,
        NatureAttack;
    }

    var fightFSM = FSM<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_fight)

        val player = Player(this)
        var enemy: Enemy? = null
        val enemies: ArrayList<Enemy> = ArrayList()

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

        val enemyMulId = if(intent.getStringExtra("enemyMulId") != null) {
            intent.getStringExtra("enemyMulId")
        } else{
            "-1"
        }

        if(enemyId != "-1" ){
            enemy = findEnemy(enemyId)
        }

        if(enemyMulId != "-1" ){
            val enemyStr = enemyMulId.split(',')
            val enemyStrIter = enemyStr.iterator()
            var i = 0
            enemyStrIter.forEach {
                findEnemy(it)?.let { it1 -> enemies.add(it1) }

                val btnEnemy = Button(this)
                btnEnemy.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                btnEnemy.text = "Id${it}#${i}"
                val index: Int = i
                btnEnemy.id = i
                btnEnemy.setOnClickListener {
                    enemy = enemies[index]
                    logEnemyIdText("Fighting with #${enemy!!.getComponent(BitmapComponent::class.java)!!._name}")
                    currentId.text = enemy!!.getComponent(BitmapComponent::class.java)!!._id.toString()
                    updateAllHealthText(
                        player.getComponent(HealthComponent::class.java)!!.healthPoints.toString(),
                        enemy!!.getComponent(HealthComponent::class.java)!!.healthPoints.toString()
                    )
                }
                i++
                chooseEnemyLayout.addView(btnEnemy)
            }
            enemy = enemies[0]
        }

        // Exit if data not exists
        if ((enemy == null) or ((enemyId == "-1") and (enemyMulId == "-1")))
            exit(-1)


        logEnemyIdText("Fighting with #${enemy!!.getComponent(BitmapComponent::class.java)!!._name}")
        currentId.text = enemy!!.getComponent(BitmapComponent::class.java)!!._id.toString()
        updateAllHealthText(
            player.getComponent(HealthComponent::class.java)!!.healthPoints.toString(),
            enemy!!.getComponent(HealthComponent::class.java)!!.healthPoints.toString()

        )


        // listeners

        fightButton.setOnClickListener {
            fightFSM.handle(ButtonType.Attack.ordinal)
            fightFSM.execute()
            attackButtonsLayout.removeAllViews()
            createWeaponButton()
            createMagicButton()
            createObjectsButton()
            createBackToAttackButton()
        }

        physicalAttackButton.setOnClickListener{
            fightFSM.handle(ButtonType.PhysicalAttack.ordinal)
            fightFSM.execute()
            fightFSM.handle(ButtonType.None.ordinal)
        }

        naturalAttackButton.setOnClickListener {
            fightFSM.handle(ButtonType.NatureAttack.ordinal)
            fightFSM.execute()
            fightFSM.handle(ButtonType.None.ordinal)
        }

        // Выбирает атаку
        val choseState = fightFSM.addState(State {})

        // Выбирает между физической и магической
        val choseAttackState = fightFSM.addState(State {})

        val attackState = fightFSM.addState(State {
            val playerDamageComponent = player.getComponent(DamageComponent::class.java)
            val playerHealthComponent = player.getComponent(HealthComponent::class.java)

            val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
            val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)

            if (playerDamageComponent != null && enemyHealthComponent != null &&
                enemyDamageComponent != null && playerHealthComponent != null
            ) {
                enemyHealthComponent.applyDamage(playerDamageComponent)
                playerHealthComponent.applyDamage(enemyDamageComponent)

                updateAllHealthText(
                    playerHealthComponent.healthPoints.toString(),
                    enemyHealthComponent.healthPoints.toString()
                )
            }
        })

        attackState.addTransition(Transition { return@Transition choseState })

        choseState.addTransition(Transition { button ->
            if (button == ButtonType.Attack.ordinal) {
                return@Transition choseAttackState

            }
            return@Transition null
        })

        choseAttackState.addTransition(Transition { button ->
            if (button == ButtonType.PhysicalAttack.ordinal || button == ButtonType.NatureAttack.ordinal) {
                return@Transition attackState
            }
            return@Transition null
        })

        fightFSM.setCurrentState(choseState)

        escapeButton.setOnClickListener {
            exit(2)
        }

    }

    private fun createBackToAttackButton() {
        val btnBackToAttack = Button(this)
        btnBackToAttack.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnBackToAttack.text = "Back"
        btnBackToAttack.setOnClickListener {
            attackButtonsLayout.removeAllViews()
        }
        attackButtonsLayout.addView(btnBackToAttack)
    }

    private fun createObjectsButton() {
        val btnObject = Button(this)
        btnObject.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnObject.isFocusable = false
        btnObject.isClickable = false
        btnObject.isEnabled = false
        btnObject.text = "Objects"
        btnObject.setOnClickListener {
            createAdditionalObjectButtons()
        }
        attackButtonsLayout.addView(btnObject)
    }

    private fun createAdditionalObjectButtons() {
        // NO objects
    }

    private fun createMagicButton() {
        val btnMagic = Button(this)
        btnMagic.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnMagic.text = "Magic"
        btnMagic.setOnClickListener {
            createAdditionalMagicButtons()
            createBackToMajorAttackButton()
        }
        attackButtonsLayout.addView(btnMagic)
    }

    private fun createAdditionalMagicButtons() {
        attackButtonsLayout.removeAllViews()
        val btnMagic = Button(this)
        btnMagic.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnMagic.text = "Nature attack"
        btnMagic.setOnClickListener {
            fightFSM.handle(ButtonType.NatureAttack.ordinal)
            fightFSM.execute()
            fightFSM.handle(ButtonType.None.ordinal)
        }
        attackButtonsLayout.addView(btnMagic)

    }

    private fun createWeaponButton() {
        val btnWeapon = Button(this)
        btnWeapon.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnWeapon.text = "Weapon"
        btnWeapon.setOnClickListener {
            createAdditionalWeaponButtons()
            createBackToMajorAttackButton()
        }
        attackButtonsLayout.addView(btnWeapon)
    }

    private fun createAdditionalWeaponButtons() {
        attackButtonsLayout.removeAllViews()
        val btnMagic = Button(this)
        btnMagic.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnMagic.text = "Physical attack"
        btnMagic.setOnClickListener {
            fightFSM.handle(ButtonType.PhysicalAttack.ordinal)
            fightFSM.execute()
            fightFSM.handle(ButtonType.None.ordinal)
        }
        attackButtonsLayout.addView(btnMagic)

    }

    private fun createBackToMajorAttackButton() {
        val btnBackToAttack = Button(this)
        btnBackToAttack.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnBackToAttack.text = "Back"
        btnBackToAttack.setOnClickListener {
            attackButtonsLayout.removeAllViews()
            createWeaponButton()
            createMagicButton()
            createObjectsButton()
            createBackToAttackButton()
        }
        attackButtonsLayout.addView(btnBackToAttack)

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