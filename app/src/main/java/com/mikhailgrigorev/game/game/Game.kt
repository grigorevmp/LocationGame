package com.mikhailgrigorev.game.game

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.activities.FightActivity
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.InventoryComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.ItemsDB
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.entities.Totem
import com.mikhailgrigorev.game.loader.BuildingsLoader
import com.mikhailgrigorev.game.loader.EnemiesLoader
import com.mikhailgrigorev.game.loader.TotemsLoader
import com.mikhailgrigorev.game.views.ItemView


@SuppressLint("ViewConstructor")
class Game(context: Context?, gameThreadName: String = "GameThread"): SurfaceView(context), Runnable, SurfaceHolder.Callback {
    private var mContext: Context? = context
    companion object{
        // sizes
        var maxX = 20
        var maxY = 28
        var unitW = 0f
        var unitH = 0f
    }

    // map objects
    private var buildingsLoader: BuildingsLoader? = null
    private var totemsLoader: TotemsLoader? = null
    private var enemiesLoader: EnemiesLoader? = null

    // for storing game objects
    private var gameEntities = ArrayList<Entity>()

    // for game control
    private var gameRunning = true
    private var firstTime = true
    // FPS control
    private val targetFPS = 60 //fps
    private val targetTime = (1000 / targetFPS).toLong()

    // thread control
    private var startTime: Long = 0
    private var timeMillis: Long = 0
    private var waitTime: Long = 0

    private var gameThread: Thread

    // for object drawing
    private var surfaceHolder: SurfaceHolder = holder
    private var paint: Paint? = null
    private var canvas: Canvas? = null

    // character
    private var player: Player? = null

    @SuppressLint("SetTextI18n")
    private fun createEnemyDialog(context: Context, obj: Enemy) {
        val objPositionComponent = obj.getComponent(PositionComponent::class.java)!!
        val objBitmapComponent = obj.getComponent(BitmapComponent::class.java)!!
        val enemyMultiple = objBitmapComponent._multiple

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enemy)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.mainLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val paramsLO: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLO.setMargins(20, 0, 20, 0)


        val imageView = dialog.findViewById(R.id.oneImageHandler) as ImageView
        imageView.setBackgroundResource(objBitmapComponent._bitmapId)
        val aloneImageHandler = dialog.findViewById(R.id.aloneImageHandler) as LinearLayout
        val imageHandler = dialog.findViewById(R.id.imageHandler) as LinearLayout
        val multipleImageHandler = dialog.findViewById(R.id.multipleImageHandler) as HorizontalScrollView

        val nameEnemy = dialog.findViewById(R.id.name) as TextView
        val enemiesIds = DBHelperFunctions.loadEnemyIDByXY(
            context,
            objPositionComponent.x.toInt(),
            objPositionComponent.y.toInt()
        )

        if (enemyMultiple == 0) {
            multipleImageHandler.visibility = View.GONE
            nameEnemy.text = objBitmapComponent._name
        }
        else{
            aloneImageHandler.visibility = View.GONE
            nameEnemy.text = context.getString(R.string.dungeon)
            for (enemy in enemiesLoader!!.enemies) {
                if (enemy.getComponent(BitmapComponent::class.java)!!._id.toString() in enemiesIds)
                {
                    val image = ImageView(context)
                    image.setBackgroundResource(enemy.getComponent(BitmapComponent::class.java)!!._bitmapId)
                    imageHandler.addView(image, paramsLO)
                }
            }
        }

        // CLOSE BUTTON
        val btnClose = dialog.findViewById(R.id.closeEnemyDialog) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // POSITIVE BUTTON
        val btnOk = dialog.findViewById(R.id.fightButton) as Button
        btnOk.setOnClickListener {
            val intent = Intent(mContext, FightActivity::class.java)
            if (enemyMultiple == 0) {
                // One enemy sample
                intent.putExtra("enemyId", objBitmapComponent._id.toString())
                println("Fighting with... @id#" + objBitmapComponent._id.toString())
            } else {
                intent.putExtra("enemyMulId", enemiesIds)
                println("Fighting with... @id#$enemiesIds")
            }
            val origin = mContext as Activity
            origin.startActivity(intent)
            origin.finish()
        }

        // DESCRIPTION
        val descriptionEnemy = dialog.findViewById(R.id.description) as TextView
        descriptionEnemy.text = objBitmapComponent._desc

        // Enemy characteristics
        // TOTEM PROPERTIES
        val enemyProperties = dialog.findViewById(R.id.enemyProperties) as LinearLayout

        val damageComponent = obj.getComponent(DamageComponent::class.java)!!
        val defenceComponent = obj.getComponent(DefenceComponent::class.java)!!

        val textView1 = TextView(context)
        textView1.text = "HP: ${obj.getComponent(HealthComponent::class.java)!!.healthPoints}"
        val textView2 = TextView(context)
        textView2.text = "Damage: ${damageComponent.physicalDamage}"
        val textView3 = TextView(context)
        textView3.text = "Air: ${damageComponent.natureForcesDamage[NatureForces.Air.ordinal]}"
        val textView4 = TextView(context)
        textView4.text = "Water: ${damageComponent.natureForcesDamage[NatureForces.Water.ordinal]}"
        val textView5 = TextView(context)
        textView5.text = "Fire: ${damageComponent.natureForcesDamage[NatureForces.Fire.ordinal]}"
        val textView6 = TextView(context)
        textView6.text = "Earth: ${damageComponent.natureForcesDamage[NatureForces.Earth.ordinal]}"
        val textView7 = TextView(context)
        textView7.text = "Defence: ${defenceComponent.physicalDefence}"
        val textView8 = TextView(context)
        textView8.text = "Air: ${defenceComponent.natureForcesDefence[NatureForces.Air.ordinal]}"
        val textView9 = TextView(context)
        textView9.text = "Water: ${defenceComponent.natureForcesDefence[NatureForces.Water.ordinal]}"
        val textView10 = TextView(context)
        textView10.text = "Fire: ${defenceComponent.natureForcesDefence[NatureForces.Fire.ordinal]}"
        val textView11 = TextView(context)
        textView11.text = "Earth: ${defenceComponent.natureForcesDefence[NatureForces.Earth.ordinal]}"

        enemyProperties.addView(textView1)
        enemyProperties.addView(textView2)
        enemyProperties.addView(textView3)
        enemyProperties.addView(textView4)
        enemyProperties.addView(textView5)
        enemyProperties.addView(textView6)
        enemyProperties.addView(textView7)
        enemyProperties.addView(textView8)
        enemyProperties.addView(textView9)
        enemyProperties.addView(textView10)
        enemyProperties.addView(textView11)

        // ITEMS
        val sItems = dialog.findViewById(R.id.sItems) as GridLayout

        var ids = ""
        for (enemy in enemiesLoader!!.enemies) {
            if (enemy.getComponent(BitmapComponent::class.java)!!._id.toString() in enemiesIds) {
                (enemy as Enemy).items.split('.').zip(enemy.itemsNum.split('.')).forEach { pair ->
                    ItemsDB.init(context)
                    val itemView = ItemView(context)
                    val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                    val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                    //val btn = Button(context, null, android.R.attr.borderlessButtonStyle)
                    val item = ItemsDB.loadItemByID(context, pair.component1().toInt())
                    if (item != null) {
                        if (pair.component1() !in ids) {
                            val bitmapId = context.resources.getIdentifier(ItemsDB.loadItemBitmapByID(context, item.id), "drawable", context.packageName)
                            btn.contentDescription = item.name
                            btn.setBackgroundResource(bitmapId)
                            itemView.id = item.id
                            countText.text = pair.component2()
                            sItems.addView(itemView)
                            ids += "${pair.component1()}."
                        }
                        else{
                            val itemViewID = dialog.findViewById<ItemView>(pair.component1().toInt())
                            val countTextID = itemViewID.findViewById<TextView>(R.id.itemContentCount)
                            countTextID.text = "${pair.component2().toInt() + countTextID.text.toString().toInt()}"

                        }
                    }
                }
            }
        }

        dialog.show()

    }

    @SuppressLint("SetTextI18n")
    private fun createTotemDialog(context: Context, obj: Totem){

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_totem)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.totemLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params


        val totemBitmapComponent = obj.getComponent(BitmapComponent::class.java)!!

        val btnClose = dialog.findViewById(R.id.closeDialogTotem) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // TOTEM ICON
        val imageView = dialog.findViewById(R.id.image) as ImageView
        imageView.setBackgroundResource(totemBitmapComponent._bitmapId)

        // NAME
        val nameTotem = dialog.findViewById(R.id.name) as TextView
        nameTotem.text = totemBitmapComponent._name

        val noItems = dialog.findViewById(R.id.no_items) as TextView

        // TOTEM DESCRIPTION
        val descriptionTotem = dialog.findViewById(R.id.description) as TextView
        descriptionTotem.text = totemBitmapComponent._desc

        // TOTEM PROPERTIES
        val totemProperties = dialog.findViewById(R.id.totemProperties) as LinearLayout

        val textView1 = TextView(context)
        textView1.text = "HP +${obj.maxHealth}"
        val textView2 = TextView(context)
        textView2.text = "Damage +${obj.damage}"
        val textView3 = TextView(context)
        textView3.text = "Air +${obj.damageAir}"
        val textView4 = TextView(context)
        textView4.text = "Water +${obj.damageWater}"
        val textView5 = TextView(context)
        textView5.text = "Fire +${obj.damageFire}"
        val textView6 = TextView(context)
        textView6.text = "Earth +${obj.damageEarth}"
        val textView7 = TextView(context)
        textView7.text = "Defence +${obj.defence}"
        val textView8 = TextView(context)
        textView8.text = "Air +${obj.defenceAir}"
        val textView9 = TextView(context)
        textView9.text = "Water +${obj.defenceWater}"
        val textView10 = TextView(context)
        textView10.text = "Fire +${obj.defenceFire}"
        val textView11 = TextView(context)
        textView11.text = "Earth +${obj.defenceEarth}"

        totemProperties.addView(textView1)
        totemProperties.addView(textView2)
        totemProperties.addView(textView3)
        totemProperties.addView(textView4)
        totemProperties.addView(textView5)
        totemProperties.addView(textView6)
        totemProperties.addView(textView7)
        totemProperties.addView(textView8)
        totemProperties.addView(textView9)
        totemProperties.addView(textView10)
        totemProperties.addView(textView11)

        // ITEMS
        val sItems = dialog.findViewById(R.id.sItems) as LinearLayout

        val sacrificeItem: ArrayList<Item> = ArrayList()

        val sacrifice = dialog.findViewById(R.id.sacrifice) as Button
        var isOk = true

        obj.items.split('.').zip(obj.itemsNum.split('.')).forEach { pair ->
            ItemsDB.init(context)
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            //val btn = Button(context, null, android.R.attr.borderlessButtonStyle)
            val item = ItemsDB.loadItemByID(context, pair.component1().toInt())
            if (item != null) {
                //btn.text = "${item.name}-${pair.component2()}"
                val bitmapId = context.resources.getIdentifier(ItemsDB.loadItemBitmapByID(context, item.id), "drawable", context.packageName)
                btn.contentDescription = item.name
                btn.setBackgroundResource(bitmapId)
                btn.id = item.id
                countText.text = pair.component2()

                val playerItem = player!!.getComponent(InventoryComponent::class.java)!!.takeItem(pair.component1().toInt())
                if(playerItem == null){
                    isOk = false
                }
                else if(playerItem.count < pair.component2().toInt()){
                    isOk = false
                }
                sacrificeItem.add(Item(pair.component1().toInt(), "", pair.component2().toInt(), 0))
                sItems.addView(itemView)
            }
        }

        if(isOk) {
            noItems.visibility = View.GONE
            sacrifice.setOnClickListener {
                for (item in sacrificeItem) {
                    val inventoryComponent = player!!.getComponent(InventoryComponent::class.java)!!
                    inventoryComponent.dropItem(item)
                    if (inventoryComponent.takeItem(item.id) != null)
                        DBHelperFunctions.replaceItem(context, item.id, inventoryComponent.takeItem(item.id)!!.count)
                    else{
                        DBHelperFunctions.dropItem(context, item.id)
                    }
                }
                val upgradeComponent = obj.getComponent(UpgradeComponent::class.java)!!
                upgradeComponent.upgrade(context, player as Entity)
                Toast.makeText(context, "SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        else {
            sacrifice.setOnClickListener {
                Toast.makeText(context, "NO ITEMS", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            sacrifice.alpha = 0.4f
            sacrifice.isClickable = false
        }

        dialog.show()

    }

    private fun showFilterPopup(view: View) {
        /*
        Drop item function
         */
        val popupMenu = PopupMenu(context, view)
        val inventory = player!!.getComponent(InventoryComponent::class.java)!!
        popupMenu.inflate(R.menu.action)
        val itemsCount = popupMenu.menu.findItem(R.id.items_count)
        itemsCount.title = inventory.takeItem(view.id / 1000)!!.count.toString()
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dropItem -> {
                    Toast.makeText(context, "Item id = ${view.id / 1000}", Toast.LENGTH_SHORT)
                        .show()
                    val item = inventory.takeItem(view.id / 1000)
                    if (item != null) {
                        inventory.dropItem(item)
                    }
                    DBHelperFunctions.dropItem(context, view.id / 1000)
                    //view.visibility = View.GONE
                    view.alpha = 0.4f
                    view.isClickable = false
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    Toast.makeText(context, "Meow", Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener false
                }
            }
        }
        popupMenu.show()
    }

    @SuppressLint("SetTextI18n")
    private fun createInventoryDialog(context: Context, obj: Entity){

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_player)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.playerLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val playerHealthComponent = player!!.getComponent(HealthComponent::class.java)!!
        val objBitmapComponent =  obj.getComponent(BitmapComponent::class.java)!!
        val objDefenceComponent =  obj.getComponent(DefenceComponent::class.java)!!
        val objDamageComponent =  obj.getComponent(DamageComponent::class.java)!!

        // PLAYER ADDITIONAL TEXT
        val healthTextPlayer = dialog.findViewById(R.id.healthText) as TextView
        healthTextPlayer.text = "Health is ${playerHealthComponent.healthPoints}/${playerHealthComponent.maxHealthPoints}"
        val playerHealthProgress = dialog.findViewById(R.id.playerHealthProgress) as ProgressBar
        playerHealthProgress.max = playerHealthComponent.maxHealthPoints
        playerHealthProgress.progress = playerHealthComponent.healthPoints

        val btnClose = dialog.findViewById(R.id.closePlayerDialog) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        val btnOk = dialog.findViewById(R.id.ok) as Button
        btnOk.setOnClickListener {
            DBHelperFunctions.restorePlayerHealth(context, player!!)
            healthTextPlayer.text = "Health is ${playerHealthComponent.healthPoints}/${playerHealthComponent.maxHealthPoints}"
            playerHealthProgress.progress = playerHealthComponent.healthPoints
        }


        // -------------------------------------
        // EQUIPMENT INVENTORY
        // -------------------------------------

        val equipmentLayout = dialog.findViewById(R.id.equipmentLayout) as GridLayout
        val equipment = player!!.getComponent(EquipmentComponent::class.java)!!

        // Loading weapon
        val weapon = equipment.weapon

        if(weapon != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(ItemsDB.loadItemBitmapByID(context, weapon.id), "drawable", context.packageName)
            btn.contentDescription = weapon.name
            btn.setBackgroundResource(bitmapId)
            btn.id = weapon.id*1000
            countText.text = weapon.count.toString()
            equipmentLayout.addView(itemView)
        }

        // Loading armor
        val armor = equipment.armor

        if(armor != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(ItemsDB.loadItemBitmapByID(context, armor.id), "drawable", context.packageName)
            btn.contentDescription = armor.name
            btn.setBackgroundResource(bitmapId)
            btn.id = armor.id*1000
            countText.text = armor.count.toString()
            equipmentLayout.addView(itemView)
        }


        // -------------------------------------
        // LOADING INVENTORY
        // -------------------------------------

        val inventoryLayout = dialog.findViewById(R.id.inventoryLayout) as GridLayout
        val inventory = player!!.getComponent(InventoryComponent::class.java)!!
        val loadItems = dialog.findViewById(R.id.load_items) as Button
        loadItems.setOnClickListener {
            var itemsTemp = DBHelperFunctions.loadAllItem(context)
            // TEST LOADING
            if(itemsTemp.count() < 2) {
                DBHelperFunctions.createItem(
                    context, arrayListOf(
                        "1",
                        "Fresh meat",
                        "0",
                        "30",
                        "${Item.stackable}"
                    )
                )
                DBHelperFunctions.createItem(
                    context, arrayListOf(
                        "2",
                        "Bones",
                        "0",
                        "30",
                        "${Item.stackable}"
                    )
                )
                DBHelperFunctions.createItem(
                    context, arrayListOf(
                        "3",
                        "Rotten meat",
                        "30",
                        "2",
                        "${Item.stackable}"
                    )
                )
                DBHelperFunctions.createItem(
                    context, arrayListOf(
                        "4",
                        "Wood",
                        "0",
                        "30",
                        "${Item.stackable}"
                    )
                )
                DBHelperFunctions.createItem(
                    context, arrayListOf(
                        "5",
                        "Souls",
                        "0",
                        "30",
                        "${Item.stackable}"
                    )
                )
                itemsTemp = DBHelperFunctions.loadAllItem(context)
            }


            for(item in itemsTemp)
                inventory.addItem(item)

            val items = inventory.getAllItems()


            inventoryLayout.removeAllViews()
            for(item in items){
                val itemView = ItemView(context)
                val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                ItemsDB.init(context)
                val bitmapId = context.resources.getIdentifier(ItemsDB.loadItemBitmapByID(context, item.id), "drawable", context.packageName)
                //val btn = Button(context, null, android.R.attr.borderlessButtonStyle)
                btn.contentDescription = item.name
                btn.setBackgroundResource(bitmapId)
                btn.id = item.id*1000
                btn.setOnClickListener {
                    showFilterPopup(it)
                }

                countText.text = item.count.toString()

                inventoryLayout.addView(itemView)
            }
        }


        val items = inventory.getAllItems()

        for(item in items){
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            val bitmapId = context.resources.getIdentifier(ItemsDB.loadItemBitmapByID(context, item.id), "drawable", context.packageName)
            btn.contentDescription = item.name
            btn.setBackgroundResource(bitmapId)
            btn.id = item.id*1000
            btn.setOnClickListener {
                showFilterPopup(it)
            }

            countText.text = item.count.toString()

            inventoryLayout.addView(itemView)
        }

        // PLAYER DAMAGE
        val damage  = dialog.findViewById(R.id.damage) as TextView
        damage.text = "Damage: ${objDamageComponent.physicalDamage}"
        val damageA = dialog.findViewById(R.id.damageA) as TextView
        damageA.text = "A: ${objDamageComponent.natureForcesDamage[NatureForces.Air.ordinal]}"
        val damageF = dialog.findViewById(R.id.damageF) as TextView
        damageF.text = "F: ${objDamageComponent.natureForcesDamage[NatureForces.Fire.ordinal]}"
        val damageW = dialog.findViewById(R.id.damageW) as TextView
        damageW.text = "W: ${objDamageComponent.natureForcesDamage[NatureForces.Water.ordinal]}"
        val damageE = dialog.findViewById(R.id.damageE) as TextView
        damageE.text = "E: ${objDamageComponent.natureForcesDamage[NatureForces.Earth.ordinal]}"

        // PLAYER DEFENCE
        val defence  = dialog.findViewById(R.id.defence) as TextView
        defence.text = "Defence: ${objDefenceComponent.physicalDefence}"
        val defenceA = dialog.findViewById(R.id.defenceA) as TextView
        defenceA.text = "A: ${objDefenceComponent.natureForcesDefence[NatureForces.Air.ordinal]}"
        val defenceF = dialog.findViewById(R.id.defenceF) as TextView
        defenceF.text = "F: ${objDefenceComponent.natureForcesDefence[NatureForces.Fire.ordinal]}"
        val defenceW = dialog.findViewById(R.id.defenceW) as TextView
        defenceW.text = "W: ${objDefenceComponent.natureForcesDefence[NatureForces.Water.ordinal]}"
        val defenceE = dialog.findViewById(R.id.defenceE) as TextView
        defenceE.text = "E: ${objDefenceComponent.natureForcesDefence[NatureForces.Earth.ordinal]}"

        // PLAYER ICON
        val imageView = dialog.findViewById(R.id.playerImage) as ImageView
        imageView.setBackgroundResource(objBitmapComponent._bitmapId)
        // PLAYER NAME
        val namePlayer = dialog.findViewById(R.id.name) as TextView
        namePlayer.text = objBitmapComponent._name

        dialog.show()

    }

    // When User cilcks on dialog button, call this method
    private fun alertDialog(context: Context, obj: Entity) {
        val positionComponent = obj.getComponent(PositionComponent::class.java)

        //Instantiate builder variable
        val builder = AlertDialog.Builder(context)

        //set content area
        builder.setMessage("You touched ${positionComponent!!.rect}")

        //set negative button
        builder.setPositiveButton(
            "Respawn enemies"
        ) { _, _ ->
            enemiesLoader = EnemiesLoader(context, true)
            for (enemy in enemiesLoader!!.enemies)
                gameEntities.add(enemy)
        }
        //set positive button
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, id ->
            // User cancelled the dialog
        }
        builder.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        /**
         * listener for touch events
         * @x - user x coord
         * @y - user y coord
         *
         */
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (obj in gameEntities) {
                    val positionComponent = obj.getComponent(PositionComponent::class.java)
                    if (positionComponent != null && positionComponent.rect.contains(x, y)) {
                        val bitmapComponent = obj.getComponent(BitmapComponent::class.java)
                        var group = bitmapComponent!!._group
                        if (group in "0,Skeleton,skeleton,Bones,devil,13,Zombie,zombie,Bones,devil")
                            group = "enemy"
                        when (group) {
                            "totem" -> createTotemDialog(context, obj as Totem)
                            "enemy" -> createEnemyDialog(context, obj as Enemy)
                            "player" -> createInventoryDialog(context, obj)
                            else -> alertDialog(context, obj)
                        }
                        break
                    }
                }
                return true
            }
        }
        return false
    }

    private fun adjustWindowSize(){
        /**
         * State correct screen ratio to draw
         */
        val rectangle = Rect()
        val window: Window = (context as Activity).window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)

        val wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val size = Point()

        val display = wm.defaultDisplay!!
        display.getSize(size)

        val outPoint = Point()
        if (Build.VERSION.SDK_INT >= 19) {
            // include navigation bar
            display.getRealSize(outPoint)
        } else {
            // exclude navigation bar
            display.getSize(outPoint)
        }
        if (outPoint.y > outPoint.x) {
            size.y = outPoint.y
            //mRealSizeWidth = outPoint.x
        } else {
            size.y = outPoint.x
            //mRealSizeWidth = outPoint.y
        }

        println("mRealSizeHeight is... #${size.y}")
        //println("mRealSizeWidth is... #$mRealSizeWidth")

        maxY = maxX * size.y/size.x
        println("maxYOld is... #$maxY")
    }

    init{

        // Set drawing view
        adjustWindowSize()

        // init objects for drawing
        surfaceHolder.addCallback(this)
        paint = Paint()

        // Thread
        gameThread = Thread(this, gameThreadName)
        gameThread.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {}

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        gameRunning = false
        var retry = true
        // завершаем работу потока
        while (retry) {
            try {
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                // если не получилось, то будем пытаться еще и еще
            }
        }

    }

    override fun run() {
        /**
         * Infinity cycle of game process
         */
        while (gameRunning) {
            startTime = System.nanoTime()
            update()
            try {
                draw()
            }
            catch (e: Exception){
               // Toast.makeText(context, "Написать Мише, если это увидите", Toast.LENGTH_SHORT).show()
                Log.d("TAG", e.toString())
            }
            threadControl()
        }
    }

    private fun update() {
        /**
         * Update Map
         */
        if (!firstTime) {
            player?.update()
        }
    }

    private fun draw() {
        /**
         * Draws main objects
         * @ firstTime - initialization
         * @ canvas drawing square
         */
        if (surfaceHolder.surface.isValid) {  //проверяем валидный ли surface
            if (firstTime) {
                firstTime = false
                unitW = surfaceHolder.surfaceFrame.width() / maxX.toFloat()
                unitH = surfaceHolder.surfaceFrame.height() / maxY.toFloat()
                // init objects
                player = Player(context)
                buildingsLoader = BuildingsLoader(context)
                totemsLoader = TotemsLoader(context)
                enemiesLoader = EnemiesLoader(context)
                for (obj in buildingsLoader!!.mapObjects)
                    gameEntities.add(obj)
                for (totem in totemsLoader!!.totems)
                    gameEntities.add(totem)
                for (enemy in enemiesLoader!!.enemies)
                    gameEntities.add(enemy)
                gameEntities.add(player!!)
            }
            // close canvas
            canvas = surfaceHolder.lockCanvas()
            // background
            //canvas!!.drawColor(Color.BLACK)
            canvas!!.drawBitmap(
                BitmapFactory.decodeResource(resources, R.drawable.map2),
                0f,
                0f,
                null
            )
            // draw objects
            for(obj in gameEntities) {
                obj.getComponent(BitmapComponent::class.java)?.draw(paint, canvas!!)
            }
            // open canvas
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun threadControl() {
        /**
         * just... thread control
         */
        timeMillis = (System.nanoTime() - startTime) / 1000000
        waitTime = targetTime - timeMillis
        try {
            Thread.sleep(waitTime)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Exception) {
            //Toast.makeText(context, "Unexpected exception has arisen", Toast.LENGTH_SHORT).show()
        }

    }


}