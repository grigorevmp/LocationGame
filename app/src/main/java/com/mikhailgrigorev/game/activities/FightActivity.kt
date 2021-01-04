package com.mikhailgrigorev.game.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.DamageComponent
import com.mikhailgrigorev.game.core.ecs.Components.HealthComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.InventoryComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.core.fsm.FSM
import com.mikhailgrigorev.game.core.fsm.State
import com.mikhailgrigorev.game.core.fsm.Transition
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.ItemsDB
import com.mikhailgrigorev.game.databases.ItemsDBHelper
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.entities.sprit.Spirit
import com.mikhailgrigorev.game.loader.EnemiesLoader
import kotlinx.android.synthetic.main.activity_fight.*


class FightActivity : AppCompatActivity() {
    enum class ButtonType {
        None,
        Back,
        Attack,
        WeaponAttack,
        SpiritAttack,
        Items;
    }

    private var fightFSM = FSM<Int>()
    private var enemiesNum = 0
    private var enemy: Enemy? = null
    private val enemies: ArrayList<Enemy> = ArrayList()
    private val enemiesNums: ArrayList<Int> = ArrayList()

    @SuppressLint("SetTextI18n")
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
        val playerBitMap = player.getComponent(BitmapComponent::class.java)!!
        val prgPlayer = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        prgPlayer.id = playerBitMap._id * 100
        choosePlayerLayout.addView(prgPlayer)

        val btnPlayer = ImageButton(this)
        btnPlayer.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        btnPlayer.id = playerBitMap._id
        btnPlayer.setImageResource(playerBitMap._bitmapId)
        btnPlayer.setBackgroundColor(Color.TRANSPARENT)
        btnPlayer.setOnClickListener {
        }
        val paramsLO: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLO.setMargins(30, 0, 30, 0)
        choosePlayerLayout.addView(btnPlayer, paramsLO)

        val textView = TextView(this)
        textView.text = playerBitMap._name
        textView.gravity = Gravity.CENTER
        choosePlayerLayout.addView(textView)
        // --------------------------------------------------------
        // --------------------------------------------------------

        // Get lone enemy if exists
        if(enemyId != "-1" ){
            enemiesNum += 1
            enemy = findEnemyByID(enemyId)

            // Create image button
            val prgPEnemy = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
            val enemyBitmapComponent = enemy!!.getComponent(BitmapComponent::class.java)!!
            prgPEnemy.id = enemyBitmapComponent._id * 100
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
            btnEnemy.setImageResource(enemyBitmapComponent._bitmapId)
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
            chooseEnemyLayout.addView(btnEnemy, paramsLO)
        }

        // Get multiple enemies if exist
        if(enemyMulId != "-1" ){
            val enemyStr = enemyMulId.split(',')
            for((i, it) in enemyStr.withIndex()){
                enemiesNum += 1
                findEnemyByID(it)?.let {
                        newEnemy -> enemies.add(newEnemy)
                }
                enemiesNums.add(i)

                val index: Int = i

                // HEALTH PROGRESS BAR FOR ENEMY
                val prgPEnemy = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
                prgPEnemy.id = it.toInt() * 100
                val maxHealth = enemies[index].getComponent(HealthComponent::class.java)!!.maxHealthPoints
                prgPEnemy.max = maxHealth
                prgPEnemy.progress = maxHealth
                chooseEnemyLayout.addView(prgPEnemy)

                // IMAGE BUTTON FOR ENEMY
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
                chooseEnemyLayout.addView(btnEnemy, paramsLO)


            }
            enemy = enemies[0]
        }

        // Exit if data doesn't not exist
        if ((enemy == null) or ((enemyId == "-1") and (enemyMulId == "-1")))
            exit(-1)

        // Set info text
        updateViewData(enemy, player, this)

        // Выбирает атаку или побег
        val attackOrEscapeState = fightFSM.addState(State{})
        attackOrEscapeState.setEntryAction {
            val attackButton = Button(this)
            val escapeButton = Button(this)
            attackButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            attackButton.text = "Attack"
            attackButton.setOnClickListener {
                fightFSM.handle(ButtonType.Attack.ordinal)
                fightFSM.execute()
            }

            escapeButton.text = "Escape"
            escapeButton.setOnClickListener {
                tryEscape(player, enemyMulId)
            }

            attackButtonsLayout.addView(attackButton)
            attackButtonsLayout.addView(escapeButton)
        }
        attackOrEscapeState.setExitAction { attackButtonsLayout.removeAllViews() }

        // Выбирает между физической и магической
        val chooseAttackState = fightFSM.addState(State {})
        chooseAttackState.setEntryAction {
            val weaponButton = Button(this)
            val spiritButton = Button(this)
            val itemButton   = Button(this)
            val backButton   = Button(this)

            val buttons = arrayOf(weaponButton, spiritButton, itemButton, backButton)
            val buttonsName = arrayOf("Weapon Attack", "Spirit Attack", "Items", "Back")
            val buttonsSignals = arrayOf(
                ButtonType.WeaponAttack.ordinal,
                ButtonType.SpiritAttack.ordinal,
                ButtonType.Items.ordinal,
                ButtonType.Back.ordinal
            )

            for(i in buttons.indices){
                buttons[i].layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                buttons[i].text = buttonsName[i]
                buttons[i].setOnClickListener {
                    fightFSM.handle(buttonsSignals[i])
                    fightFSM.execute()
                }
                attackButtonsLayout.addView(buttons[i])
            }
        }
        chooseAttackState.setExitAction { attackButtonsLayout.removeAllViews() }

        val weaponChooseState = fightFSM.addState(State {})
        weaponChooseState.setEntryAction {
            val weaponButton = Button(this)
            weaponButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            weaponButton.text = "Super Weapon 100k damage"
            weaponButton.setOnClickListener {
                fightFSM.handle(ButtonType.None.ordinal)
                fightFSM.execute()
                fightFSM.handle(ButtonType.None.ordinal)
            }

            val backButton = Button(this)
            backButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            backButton.text = "Back"
            backButton.setOnClickListener {
                fightFSM.handle(ButtonType.Back.ordinal)
                fightFSM.execute()
            }

            attackButtonsLayout.addView(weaponButton)
            attackButtonsLayout.addView(backButton)
        }
        weaponChooseState.setExitAction { attackButtonsLayout.removeAllViews() }

        val weaponAttackState = fightFSM.addState(State {
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


        val playerSpirit = player.getComponent(Spirit::class.java)
        val spiritAbilities = playerSpirit?.abilityPack

        // Создание состояния для каждой абилки
        val abilityStates = ArrayList<State<Int>>()
        if (spiritAbilities != null){
            for (i in 0 until spiritAbilities.size) {
                val abilityState = fightFSM.addState(State {
                    val playerSpiritAbility = spiritAbilities[i]
                    val playerHealthComponent = player.getComponent(HealthComponent::class.java)

                    if (enemyMulId == "-1") {
                        val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
                        val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)

                        if (enemyHealthComponent != null &&
                            enemyDamageComponent != null && playerHealthComponent != null
                        ) {
                            enemyHealthComponent.applyDamage(playerSpiritAbility.damageComponent)
                            playerHealthComponent.applyDamage(enemyDamageComponent)

                            setEnemyHealthText(enemyHealthComponent.healthPoints.toString(),
                                enemy!!.getComponent(BitmapComponent::class.java)!!._id, player,  this)
                            setPlayerHealthText(playerHealthComponent.healthPoints.toString(), player, this)
                        }
                        setNewPlayerHealthToDatabase(this, player)
                    } else {
                        val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
                        val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)

                        if (playerHealthComponent != null) {
                            if (enemyHealthComponent != null && enemyDamageComponent != null
                            ) {
                                enemyHealthComponent.applyDamage(playerSpiritAbility.damageComponent)
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
                abilityState.addTransition(Transition { return@Transition attackOrEscapeState })
                abilityStates.add(abilityState)
            }
        }

        // Состояние выбора абилки
        val abilityChooseState = fightFSM.addState(State {})
        abilityChooseState.setEntryAction {
            if (spiritAbilities != null){
                for (i in 0 until spiritAbilities.size) {
                    val abilityButton = Button(this)
                    abilityButton.layoutParams = ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )
                    abilityButton.text = (spiritAbilities[i].name + " (${spiritAbilities[i].damageComponent.natureForcesDamage[NatureForces.Fire.ordinal]} dmg)")
                    abilityButton.setOnClickListener {
                        fightFSM.handle((i+1)*100)
                        fightFSM.execute()
                        fightFSM.handle(ButtonType.None.ordinal)
                    }
                    attackButtonsLayout.addView(abilityButton)
                }
            }
            val backButton = Button(this)
            backButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            backButton.text = "Back"
            backButton.setOnClickListener {
                fightFSM.handle(ButtonType.Back.ordinal)
                fightFSM.execute()
            }
            attackButtonsLayout.addView(backButton)
        }
        abilityChooseState.setExitAction { attackButtonsLayout.removeAllViews() }


        attackOrEscapeState.addTransition(Transition { button ->
            if (button == ButtonType.Attack.ordinal)
                return@Transition chooseAttackState
            return@Transition null
        })

        chooseAttackState.addTransition(Transition { button ->
            if (button == ButtonType.WeaponAttack.ordinal) {
                return@Transition weaponChooseState
            }
            if (button == ButtonType.SpiritAttack.ordinal){
                return@Transition abilityChooseState
            }
            if (button == ButtonType.Items.ordinal) {
                return@Transition attackOrEscapeState
            }
            if (button == ButtonType.Back.ordinal) {
                return@Transition attackOrEscapeState
            }
            return@Transition null
        })

        weaponChooseState.addTransition(Transition { button ->
            if (button == ButtonType.Back.ordinal) {
                return@Transition chooseAttackState
            }
            return@Transition weaponAttackState
        })
        abilityChooseState.addTransition(Transition { button ->
            if (button == ButtonType.Back.ordinal ) {
                return@Transition chooseAttackState
            }
            if (spiritAbilities != null) {
                val index = (button / 100) - 1
                if (index >= 0 && index < spiritAbilities.size) {
                    return@Transition abilityStates[index]
                }
            }
            return@Transition null
        })

        weaponAttackState.addTransition(Transition { return@Transition attackOrEscapeState })

        fightFSM.setCurrentState(attackOrEscapeState)
    }

    private fun tryEscape(player: Player, enemyMulId: String){
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
        DBHelperFunctions.setPlayerHealth(context, player)
    }

    private fun deleteEnemyFromDatabase(context:Context, enemy: Enemy){
        DBHelperFunctions.deleteEnemy(context, enemy)
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

            val enemyDrop = enemy!!.items
            val enemyDropNum = enemy!!.itemsNum
            val dropItems: ArrayList<Item> = ArrayList()

            enemyDrop.split('.').zip(enemyDropNum.split('.')).forEach { pair ->
                ItemsDB.init(context)
                val item = ItemsDB.loadItemByID(context, pair.component1().toInt())
                if (item != null) {
                    dropItems.add(Item(pair.component1().toInt(), "", pair.component2().toInt(), 0))
                }
            }

            for (item in dropItems) {
                val inventoryComponent = player.getComponent(InventoryComponent::class.java)!!
                val isItemInInventory = inventoryComponent.takeItem(item.id)
                inventoryComponent.addItem(item)
                if(isItemInInventory != null)
                    DBHelperFunctions.replaceItem(context, item.id, inventoryComponent.takeItem(item.id)!!.count)
                else
                    DBHelperFunctions.createItem(context, arrayListOf("${item.id}", item.name, "0", "${item.count}", "${item.type}",
                        "${ItemsDB.loadItemEQ(context, item.id)}", "" , "0"))
                Toast.makeText(context, "You got ${item.count} ${item.name}", Toast.LENGTH_SHORT).show()
                }

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