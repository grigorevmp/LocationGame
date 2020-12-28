package com.mikhailgrigorev.game.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.fsm.FSM
import com.mikhailgrigorev.game.core.fsm.State
import com.mikhailgrigorev.game.core.fsm.Transition
import com.mikhailgrigorev.game.databases.DBHelperFunctions
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
    var enemiesNum = 0
    private var enemy: Enemy? = null
    private val enemies: ArrayList<Enemy> = ArrayList()
    private val enemiesNums: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_fight)

        val player = Player(this)

        // Get information about lone enemy
        val enemyId = if(intent.getStringExtra("enemyId") != null) {
            intent.getStringExtra("enemyId")
        } else{
            "-1"
        }

        // Get information about multiple enemies
        val enemyMulId = if(intent.getStringExtra("enemyMulId") != null) {
            intent.getStringExtra("enemyMulId")
        } else{
            "-1"
        }


        // PLAYER ICONS TEST BLOCK
        // --------------------------------------------------------
        // --------------------------------------------------------
        val prgPlayer = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        prgPlayer.id = player.getComponent(BitmapComponent::class.java)!!._id * 100
        choosePlayerLayout.addView(prgPlayer)

        val btnPlayer = ImageButton(this)
        btnPlayer.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnPlayer.id = player.getComponent(BitmapComponent::class.java)!!._id
        btnPlayer.setImageResource(player.getComponent(BitmapComponent::class.java)!!._bitmapId)
        btnPlayer.setBackgroundColor(Color.TRANSPARENT)
        btnPlayer.setOnClickListener {
        }
        val paramsLO: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLO.setMargins(30, 0, 30, 0)
        choosePlayerLayout.addView(btnPlayer, paramsLO)
        // --------------------------------------------------------
        // --------------------------------------------------------



        // Get lone enemy if exists
        if(enemyId != "-1" ){
            enemiesNum += 1
            enemy = findEnemyByID(enemyId)

            // Create image button
            val prgPEnemy = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
            prgPEnemy.id = enemy!!.getComponent(BitmapComponent::class.java)!!._id * 100
            val maxHealth = enemy!!.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            prgPEnemy.max = maxHealth
            prgPEnemy.progress = maxHealth
            chooseEnemyLayout.addView(prgPEnemy)

            val btnEnemy = ImageButton(this)
            btnEnemy.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            btnEnemy.id = enemyId.toInt()
            btnEnemy.setImageResource(enemy!!.getComponent(BitmapComponent::class.java)!!._bitmapId)
            btnEnemy.setBackgroundColor(Color.TRANSPARENT)
            btnEnemy.scaleX = 1f
            btnEnemy.scaleY = 1f
            btnEnemy.setOnClickListener {
                updateViewData(enemy, player, this)

                val index2: Int =  btnEnemy.id
                val buttonTest = findViewById<ImageButton>(index2)
                buttonTest.scaleX = 1f
                buttonTest.scaleY = 1f
            }
            val paramsLO: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            paramsLO.setMargins(30, 0, 30, 0)
            chooseEnemyLayout.addView(btnEnemy, paramsLO)
        }

        // Get multiple enemies if exist
        if(enemyMulId != "-1" ){
            val enemyStr = enemyMulId.split(',')
            val enemyStrIter = enemyStr.iterator()
            var i = 0
            enemyStrIter.forEach {
                enemiesNum += 1
                findEnemyByID(it)?.let { it1 -> enemies.add(it1) }
                enemiesNums.add(i)

                val index: Int = i
                val prgPEnemy = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
                prgPEnemy.id = it.toInt() * 100
                val maxHealth = enemies[index].getComponent(HealthComponent::class.java)!!.maxHealthPoints
                prgPEnemy.max = maxHealth
                prgPEnemy.progress = maxHealth
                chooseEnemyLayout.addView(prgPEnemy)
                // Create image button
                val btnEnemy = ImageButton(this)
                btnEnemy.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                btnEnemy.id = it.toInt()
                btnEnemy.setImageResource(enemies[index].getComponent(BitmapComponent::class.java)!!._bitmapId)
                btnEnemy.setBackgroundColor(Color.TRANSPARENT)
                btnEnemy.scaleX = 0.8f
                btnEnemy.scaleY = 0.8f
                if(i== 0){
                    btnEnemy.scaleX = 1f
                    btnEnemy.scaleY = 1f
                }
                btnEnemy.setOnClickListener {
                    enemy = enemies[index]
                    updateViewData(enemy, player, this)

                    scaleAllButtons(enemies)
                    val index2: Int =  btnEnemy.id
                    val buttonTest = findViewById<ImageButton>(index2)
                    buttonTest.scaleX = 1f
                    buttonTest.scaleY = 1f
                }
                val paramsLO: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                paramsLO.setMargins(30, 0, 30, 0)
                chooseEnemyLayout.addView(btnEnemy, paramsLO)


                i++
            }
            enemy = enemies[0]
        }

        // Exit if data doesn't not exist
        if ((enemy == null) or ((enemyId == "-1") and (enemyMulId == "-1")))
            exit(-1)

        // Set info text
        updateViewData(enemy, player, this)

        // Create attack button tree
        createAttackButton()

        // Escape from the fight
        escapeButton.setOnClickListener {
            val rnds = (0..10).random()
            if(rnds > 5) {
                setNewPlayerHealthToDatabase(this, player)
                exit(2)
            }
            else{
                Toast.makeText(this, "You tried to escape, but monster stopped you", Toast.LENGTH_SHORT).show()
                if (enemyMulId == "-1") {
                    val playerDamageComponent = player.getComponent(DamageComponent::class.java)
                    val playerHealthComponent = player.getComponent(HealthComponent::class.java)

                    val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
                    val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)

                    if (playerDamageComponent != null && enemyHealthComponent != null &&
                        enemyDamageComponent != null && playerHealthComponent != null
                    ) {
                        playerHealthComponent.applyDamage(enemyDamageComponent)
                        setPlayerHealthText(playerHealthComponent.healthPoints.toString(), player, this)
                    }
                } else {
                    val playerDamageComponent = player.getComponent(DamageComponent::class.java)
                    val playerHealthComponent = player.getComponent(HealthComponent::class.java)
                    if (playerDamageComponent != null && playerHealthComponent != null
                    ) {
                        val enemiesIterator = enemies.iterator()
                        enemiesIterator.forEach {
                            val enemyDamageComponentTmp = it.getComponent(DamageComponent::class.java)
                            val enemyHealthComponentTmp = it.getComponent(HealthComponent::class.java)
                            if (enemyHealthComponentTmp != null && enemyDamageComponentTmp != null
                            ) {
                                playerHealthComponent.applyDamage(enemyDamageComponentTmp)
                                setPlayerHealthText(playerHealthComponent.healthPoints.toString(), player, this)
                            }
                        }

                    }
                }
                setNewPlayerHealthToDatabase(this, player)
            }
        }

        // Выбирает атаку
        val chooseState = fightFSM.addState(State {})

        // Выбирает между физической и магической
        val chooseAttackState = fightFSM.addState(State {})

        val attackState = fightFSM.addState(State {
            if (enemyMulId == "-1") {
                val playerDamageComponent = player.getComponent(DamageComponent::class.java)
                val playerHealthComponent = player.getComponent(HealthComponent::class.java)

                val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
                val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)

                if (playerDamageComponent != null && enemyHealthComponent != null &&
                    enemyDamageComponent != null && playerHealthComponent != null
                ) {
                    enemyHealthComponent.applyDamage(playerDamageComponent)
                    playerHealthComponent.applyDamage(enemyDamageComponent)

                    setEnemyHealthText(enemyHealthComponent.healthPoints.toString(),
                        enemy!!.getComponent(BitmapComponent::class.java)!!._id, player,  this)
                    setPlayerHealthText(playerHealthComponent.healthPoints.toString(), player, this)
                }
                setNewPlayerHealthToDatabase(this, player)
            } else {
                val playerDamageComponent = player.getComponent(DamageComponent::class.java)
                val playerHealthComponent = player.getComponent(HealthComponent::class.java)
                val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
                val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)


                if (playerDamageComponent != null && playerHealthComponent != null
                ) {
                    if (enemyHealthComponent != null && enemyDamageComponent != null
                    ) {
                        enemyHealthComponent.applyDamage(playerDamageComponent)
                        setEnemyHealthText(enemyHealthComponent.healthPoints.toString(),
                            enemy!!.getComponent(BitmapComponent::class.java)!!._id,player,
                            this)
                    }

                    val enemiesIterator = enemies.iterator()
                    enemiesIterator.forEach {
                        val enemyDamageComponentTmp = it.getComponent(DamageComponent::class.java)
                        val enemyHealthComponentTmp = it.getComponent(HealthComponent::class.java)
                        if (enemyHealthComponentTmp != null && enemyDamageComponentTmp != null
                        ) {
                            playerHealthComponent.applyDamage(enemyDamageComponentTmp)
                            setPlayerHealthText(playerHealthComponent.healthPoints.toString(), player, this)
                        }
                    }
                    setNewPlayerHealthToDatabase(this, player)
                }
            }
        })

        attackState.addTransition(Transition { return@Transition chooseState })

        chooseState.addTransition(Transition { button ->
            if (button == ButtonType.Attack.ordinal) {
                return@Transition chooseAttackState

            }
            return@Transition null
        })

        chooseAttackState.addTransition(Transition { button ->
            if (button == ButtonType.PhysicalAttack.ordinal || button == ButtonType.NatureAttack.ordinal) {
                return@Transition attackState
            }
            return@Transition null
        })

        fightFSM.setCurrentState(chooseState)

    }

    private fun hideImageButtonById(id: Int) {
        val buttonTest = findViewById<ImageButton>(id)
        buttonTest.visibility = View.GONE
        val prgTest = findViewById<ProgressBar>(id*100)
        prgTest.visibility = View.GONE
    }

    private fun showImageButtonById(id: Int) {
        val buttonTest = findViewById<ImageButton>(id)
        buttonTest.visibility = View.VISIBLE
        val prgTest = findViewById<ProgressBar>(id*100)
        prgTest.visibility = View.VISIBLE
    }

    private fun scaleAllButtons(enemies: ArrayList<Enemy>){
        val iter = enemies.iterator()
        iter.forEach { it3 ->
            val id = it3.getComponent(BitmapComponent::class.java)!!._id
            val buttonTest = findViewById<ImageButton>(id)
            buttonTest.scaleX = 0.8f
            buttonTest.scaleY = 0.8f
        }

    }

    private fun setNewPlayerHealthToDatabase(context:Context, player: Player){
        DBHelperFunctions().setPlayerHealth(context, player)
    }

    private fun deleteEnemyFromDatabase(context:Context, enemy: Enemy){
        DBHelperFunctions().deleteEnemy(context, enemy)
    }

    private fun updateViewData(enemy: Enemy?, player: Player, context:Context) {
        /*
        Update visual info
         */
        logEnemyIdText("Fighting with ${enemy!!.getComponent(BitmapComponent::class.java)!!._name}")
        currentId.text = enemy.getComponent(BitmapComponent::class.java)!!._id.toString()
        setEnemyHealthText(enemy.getComponent(HealthComponent::class.java)!!.healthPoints.toString(),
            enemy.getComponent(BitmapComponent::class.java)!!._id, player, context)
        setPlayerHealthText(player.getComponent(HealthComponent::class.java)!!.healthPoints.toString(), player, context)
    }

    private fun createBackToAttackButton() {
        /*
        2 level: Back to 1 level
         */
        val btnBackToAttack = Button(this)
        btnBackToAttack.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnBackToAttack.text = "Back"
        btnBackToAttack.setOnClickListener {
            createAttackButton()
        }
        attackButtonsLayout.addView(btnBackToAttack)
    }

    private fun createObjectsButton() {
        /*
        2 level: create Physical attacking buttons
         */
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
        /*
        3 level: directly attack
         */
    }

    private fun createMagicButton() {
        /*
        2 level: create Physical attacking buttons
         */
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
        /*
        3 level: directly attack
         */
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
            createAttackButton()
        }
        attackButtonsLayout.addView(btnMagic)
    }

    private fun createWeaponButton() {
        /*
        2 level: create Physical attacking buttons
         */
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
        /*
        3 level: directly attack
         */
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
            createAttackButton()
        }
        attackButtonsLayout.addView(btnMagic)

    }

    private fun createBackToMajorAttackButton() {
        /*
        3 level: Back to 2 level button
         */
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

    private fun createAttackButton() {
        /*
        1 level: Main attack button
         */
        attackButtonsLayout.removeAllViews()
        val btnBackToAttack = Button(this)
        btnBackToAttack.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnBackToAttack.text = "Attack"
        btnBackToAttack.setOnClickListener {
            fightFSM.handle(ButtonType.Attack.ordinal)
            fightFSM.execute()
            attackButtonsLayout.removeAllViews()
            createWeaponButton()
            createMagicButton()
            createObjectsButton()
            createBackToAttackButton()
        }
        attackButtonsLayout.addView(btnBackToAttack)

    }

    private fun findEnemyByID(enemyId: String): Enemy? {
        /*
        Find enemy by ID in files
         */
        val enemiesLoader = EnemiesLoader(this)
        for (enemyIterator in enemiesLoader.enemies){
            if(enemyIterator.getComponent(BitmapComponent::class.java)!!._id.toString() == enemyId) {
                return enemyIterator as Enemy
            }
        }
        return null
    }

    private fun exit(errorCode: Int = -1){
        /*
        Left fight
         */
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Set", "Game")
        intent.putExtra("Reason", errorCode)
        startActivity(intent)
        finish()
    }


    private fun logEnemyIdText(message: String){
        /*
        Set header "Fighting with..."
         */
        header.text = message
    }

    private fun setPlayerHealthText(valuePlayer: String, player:Player, context: Context){
    /*
    Check correctness of player health values
     */
        if (valuePlayer.toInt() <= 0) {
            val maxHealth = player.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            val progressAbove = findViewById<ProgressBar>(player.getComponent(BitmapComponent::class.java)!!._id*100)
            progressAbove.max = maxHealth
            progressAbove.progress = valuePlayer.toInt()
            playerHealthProgress.max = maxHealth
            playerHealthProgress.progress = 0
            updatePlayerHealthText("0/$maxHealth")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("You lose")
            builder.setMessage("You have been killed")
            builder.setCancelable(false)
            //set negative button
            builder.setPositiveButton(
                "Ok =("
            ) { _, _ ->
                setNewPlayerHealthToDatabase(context, player)
                exit(1)
            }
            builder.show()
        }
        else {
            val maxHealth = player.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            val progressAbove = findViewById<ProgressBar>(player.getComponent(BitmapComponent::class.java)!!._id*100)
            progressAbove.max = maxHealth
            progressAbove.progress = valuePlayer.toInt()
            playerHealthProgress.max = maxHealth
            playerHealthProgress.progress = valuePlayer.toInt()
            updatePlayerHealthText("$valuePlayer/$maxHealth")
        }
    }

    private fun setEnemyHealthText(valueEnemy: String, id: Int, player:Player, context: Context){
        /*
        Check correctness of enemy health values
         */
        if (valueEnemy.toInt() <= 0) {
            val maxHealth = enemy!!.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            val progressAbove = findViewById<ProgressBar>(id*100)
            progressAbove.max = maxHealth
            progressAbove.progress = valueEnemy.toInt()
            enemyHealthProgress.max = maxHealth
            enemyHealthProgress.progress = valueEnemy.toInt()
            updateEnemyHealthText("0/$maxHealth")
            deleteEnemyFromDatabase(context, enemy!!)
            hideImageButtonById(id)
            enemiesNum -= 1
            if(enemiesNum == 0){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("You win")
                builder.setMessage("You killed all enemies")
                builder.setCancelable(false)
                //set negative button
                builder.setPositiveButton(
                    "Cool =>"
                ) { _, _ ->
                    setNewPlayerHealthToDatabase(context, player)
                    exit(1)
                }
                builder.show()
            }
            else{
                //enemies.remove(enemy)
                enemiesNums.remove((enemies.indexOf(enemy)))
                enemy = enemies[enemiesNums[0]]
                updateViewData(enemy, player, this)
                val index2: Int = enemies[0].getComponent(BitmapComponent::class.java)!!._id
                val buttonTest = findViewById<ImageButton>(index2)
                buttonTest.scaleX = 1f
                buttonTest.scaleY = 1f
            }
        }
        else {
            val maxHealth = enemy!!.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            val progressAbove = findViewById<ProgressBar>(id*100)
            progressAbove.max = maxHealth
            progressAbove.progress = valueEnemy.toInt()
            enemyHealthProgress.max = maxHealth
            enemyHealthProgress.progress = valueEnemy.toInt()
            updateEnemyHealthText("$valueEnemy/$maxHealth")
        }
    }

    private fun updatePlayerHealthText(value: String){
        /*
        Set correct health values to player
         */
        playerHPValue.text = value
    }

    private fun updateEnemyHealthText(value: String){
        /*
        Set correct health values to enemy
         */
        healthValue.text = value
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