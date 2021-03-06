package com.mikhailgrigorev.game.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.mikhailgrigorev.game.MapsActivity
import com.mikhailgrigorev.game.R
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
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.entities.sprit.Ability
import com.mikhailgrigorev.game.entities.sprit.Spirit
import com.mikhailgrigorev.game.loader.EnemiesLoader
import kotlinx.android.synthetic.main.activity_fight.*


class FightActivity : AppCompatActivity() {
    enum class ButtonType {
        Weapon,
        Spirit,
        Items;
    }

    private var fightFSM = FSM<Int>()
    private var enemiesNum = 0
    private var enemy: Enemy? = null
    private lateinit var player: Player
    private val enemies: ArrayList<Enemy> = ArrayList()
    private val enemiesNums: ArrayList<Int> = ArrayList()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_fight)

        player = Player(this)

        // Get information about multiple enemies
        val enemiesIdContent = if (intent.getStringExtra("enemyMulId") != null) {
            intent.getStringExtra("enemyMulId")
        } else {
            "-1"
        }

        if (enemiesIdContent == "-1")
            exit(-1)

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


        // Get multiple enemies if exist
        if (enemiesIdContent != "-1") {
            val enemyStr = enemiesIdContent.split(',')
            for ((i, it) in enemyStr.withIndex()) {
                enemiesNum += 1
                findEnemyByID(it)?.let { newEnemy ->
                    enemies.add(newEnemy)
                }
                enemiesNums.add(i)

                val index: Int = i

                // HEALTH PROGRESS BAR FOR ENEMY
                val prgPEnemy = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
                prgPEnemy.id = it.toInt() * 100
                val maxHealth =
                    enemies[index].getComponent(HealthComponent::class.java)!!.maxHealthPoints
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
                if (i == 0) {
                    btnEnemy.scaleX = 1f
                    btnEnemy.scaleY = 1f
                }
                btnEnemy.setOnClickListener {
                    enemy = enemies[index]
                    updateViewData(enemy, player, this)

                    scaleAllButtons(enemies)
                    val index2: Int = btnEnemy.id
                    val buttonTest = findViewById<ImageButton>(index2)
                    buttonTest.scaleX = 1f
                    buttonTest.scaleY = 1f
                }
                chooseEnemyLayout.addView(btnEnemy, paramsLO)
            }
            enemy = enemies[0]
        }

        if (enemy == null)
            exit(-1)

        // Set info text
        updateViewData(enemy, player, this)

        escapeButton.setOnClickListener {
            tryEscape(player, enemiesIdContent)
        }

        val sectionButtons = arrayOf(weaponSectionButton, spiritSectionButton, itemsSectionButton)
        val sectionButtonSignals = arrayOf(
            ButtonType.Weapon.ordinal,
            ButtonType.Spirit.ordinal,
            ButtonType.Items.ordinal
        )

        for (i in sectionButtons.indices) {
            sectionButtons[i].setOnClickListener {
                fightFSM.handle(sectionButtonSignals[i])
            }
        }

        val weaponSectionState =
            fightFSM.addState(State {}).setExitAction { abilityButtonsLayout.removeAllViews() }
        val spiritSectionState =
            fightFSM.addState(State {}).setExitAction { abilityButtonsLayout.removeAllViews() }
        val itemsSectionState =
            fightFSM.addState(State {}).setExitAction { abilityButtonsLayout.removeAllViews() }


        weaponSectionState.setEntryAction {
            val weaponButton = Button(this)
            weaponButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            weaponButton.text = "Super Weapon 100k damage"
            weaponButton.setOnClickListener {

                val playerDamageComponent = player.getComponent(DamageComponent::class.java)
                val playerHealthComponent = player.getComponent(HealthComponent::class.java)
                val enemyDamageComponent = enemy!!.getComponent(DamageComponent::class.java)
                val enemyHealthComponent = enemy!!.getComponent(HealthComponent::class.java)

                if (playerDamageComponent != null && playerHealthComponent != null
                ) {
                    if (enemyHealthComponent != null && enemyDamageComponent != null
                    ) {
                        enemyHealthComponent.applyDamage(playerDamageComponent)
                        setEnemyHealthText(
                            enemy!!,
                            enemyHealthComponent.healthPoints.toString(),
                            enemy!!.getComponent(BitmapComponent::class.java)!!._id, player,
                            this
                        )
                    }

                    val enemiesIterator = enemies.iterator()
                    enemiesIterator.forEach {
                        val enemyDamageComponentTmp =
                            it.getComponent(DamageComponent::class.java)
                        val enemyHealthComponentTmp =
                            it.getComponent(HealthComponent::class.java)
                        if (enemyHealthComponentTmp != null && enemyDamageComponentTmp != null
                        ) {
                            playerHealthComponent.applyDamage(enemyDamageComponentTmp)
                            setPlayerHealthText(
                                playerHealthComponent.healthPoints.toString(),
                                player,
                                this
                            )
                        }
                    }
                    setNewPlayerHealthToDatabase(this, player)
                }
            }
            abilityButtonsLayout.addView(weaponButton)
        }


        val playerSpirit = player.getComponent(Spirit::class.java)
        val spiritAbilities = playerSpirit?.abilityPack
        if (spiritAbilities != null) {
            spiritSectionState.setEntryAction {
                putAbilitiesInAbilityButtonsLayout(spiritAbilities)
            }
        }

        weaponSectionState.addTransition(Transition { button ->
            if (button == ButtonType.Spirit.ordinal) {
                return@Transition spiritSectionState
            }
            if (button == ButtonType.Items.ordinal) {
                return@Transition itemsSectionState
            }
            return@Transition null
        })

        spiritSectionState.addTransition(Transition { button ->
            if (button == ButtonType.Weapon.ordinal) {
                return@Transition weaponSectionState
            }
            if (button == ButtonType.Items.ordinal) {
                return@Transition itemsSectionState
            }
            return@Transition null
        })

        itemsSectionState.addTransition(Transition { button ->
            if (button == ButtonType.Weapon.ordinal) {
                return@Transition weaponSectionState
            }
            if (button == ButtonType.Spirit.ordinal) {
                return@Transition spiritSectionState
            }
            return@Transition null
        })

        fightFSM.setCurrentState(weaponSectionState)
    }

    private fun putAbilitiesInAbilityButtonsLayout(abilities: ArrayList<Ability>) {
        for (i in 0 until abilities.size) {
            val abilityButton = Button(this)
            abilityButton.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            abilityButton.text = (abilities[i].name)
            abilityButton.setOnClickListener {
                 fight(player, abilities[i], enemy, enemies)
            }
            abilityButtonsLayout.addView(abilityButton)
        }

    }

    private fun fight(
        player: Player,
        playerAbility: Ability,
        focusedEnemy: Enemy?,
        enemies: ArrayList<Enemy>
    ) {
        val playerHealthComponent = player.getComponent(HealthComponent::class.java)
        val focusedEnemyHealthComponent = focusedEnemy?.getComponent(HealthComponent::class.java)
        val enemiesIterator = enemies.iterator()


        if (focusedEnemy != null && focusedEnemyHealthComponent != null) {
            playerAbility(focusedEnemy, enemies)
            enemiesIterator.forEach {
                val enemyHealthComponent = it.getComponent(HealthComponent::class.java)
                setEnemyHealthText(it,
                    enemyHealthComponent?.healthPoints.toString(),
                    it.getComponent(BitmapComponent::class.java)!!._id, player,
                    this
                )
            }
        }

        if (playerHealthComponent != null) {
            enemiesIterator.forEach {
                val enemyDamageComponent = it.getComponent(DamageComponent::class.java)
                if (enemyDamageComponent != null) {
                    playerHealthComponent.applyDamage(enemyDamageComponent)
                    setPlayerHealthText(
                        playerHealthComponent.healthPoints.toString(),
                        player,
                        this
                    )
                }
            }
        }
        setNewPlayerHealthToDatabase(this, player)
    }


    private fun tryEscape(player: Player, enemyMulId: String){
        val randomState = (0..10).random()
        if(randomState > 5) {
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
        for(it in enemies){
            val id = it.getComponent(BitmapComponent::class.java)!!._id
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

    private fun updateViewData(_enemy: Enemy?, player: Player, context:Context) {
        /*
        Update visual info
         */
        logEnemyIdText("Fighting with ${_enemy!!.getComponent(BitmapComponent::class.java)!!._name}")
        currentId.text = _enemy.getComponent(BitmapComponent::class.java)!!._id.toString()
        setEnemyHealthText(_enemy, _enemy.getComponent(HealthComponent::class.java)!!.healthPoints.toString(),
            _enemy.getComponent(BitmapComponent::class.java)!!._id, player, context)
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
        val intent = Intent(this, MapsActivity::class.java)
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

    private fun setEnemyHealthText(_enemy: Enemy, valueEnemy: String, id: Int, player:Player, context: Context){
        /*
        Check correctness of enemy health values
         */
        if (valueEnemy.toInt() <= 0) {
            val maxHealth = _enemy.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            val progressAbove = findViewById<ProgressBar>(id*100)
            progressAbove.max = maxHealth
            progressAbove.progress = valueEnemy.toInt()
            enemyHealthProgress.max = maxHealth
            enemyHealthProgress.progress = valueEnemy.toInt()
            updateEnemyHealthText("0/$maxHealth")

            val enemyDrop = _enemy.items
            val enemyDropNum = _enemy.itemsNum
            val dropItems: ArrayList<Item> = ArrayList()

            enemyDrop.split('.').zip(enemyDropNum.split('.')).forEach { pair ->
                ItemsDB.init(context)
                val item = ItemsDB.loadItemByID(context, pair.component1().toInt())
                if (item != null) {
                    dropItems.add(Item(pair.component1().toInt(), item.name, pair.component2().toInt(), 0))
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

            deleteEnemyFromDatabase(context, _enemy)
            hideImageButtonById(id)
            enemiesNum -= 1
            if(enemiesNum == 0){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("You win")
                builder.setMessage("You killed all enemies")
                builder.setCancelable(false)
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
                enemiesNums.remove((enemies.indexOf(_enemy)))
                if(_enemy == enemy) {
                    enemy = enemies[enemiesNums[0]]
                    updateViewData(enemy, player, this)
                    val index2: Int = enemies[0].getComponent(BitmapComponent::class.java)!!._id
                    val buttonTest = findViewById<ImageButton>(index2)
                    buttonTest.scaleX = 1f
                    buttonTest.scaleY = 1f
                }
            }
        }
        else {
            val maxHealth = _enemy.getComponent(HealthComponent::class.java)!!.maxHealthPoints
            val progressAbove = findViewById<ProgressBar>(id*100)
            progressAbove.max = maxHealth
            progressAbove.progress = valueEnemy.toInt()
            if(_enemy == enemy) {
                enemyHealthProgress.max = maxHealth
                enemyHealthProgress.progress = valueEnemy.toInt()
                updateEnemyHealthText("$valueEnemy/$maxHealth")
            }
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
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return false
    }


}