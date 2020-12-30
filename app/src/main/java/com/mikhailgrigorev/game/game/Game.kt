package com.mikhailgrigorev.game.game

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.activities.FightActivity
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.core.ecs.Components.inventory.InventoryComponent
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.loader.BuildingsLoader
import com.mikhailgrigorev.game.loader.EnemiesLoader
import com.mikhailgrigorev.game.loader.TotemsLoader


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

    private fun createEnemyDialog(context: Context, obj: Entity) {
        val objPositionComponent = obj.getComponent(PositionComponent::class.java)!!
        val objBitmapComponent = obj.getComponent(BitmapComponent::class.java)!!
        val enemyMultiple = objBitmapComponent._multiple

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.70).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_enemy)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Set size
        val mainLayout = dialog.findViewById(R.id.mainLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val imageHandler = dialog.findViewById(R.id.imageHandler) as LinearLayout

        val nameEnemy = dialog.findViewById(R.id.name) as TextView
        val enemiesIds = DBHelperFunctions.loadEnemyIDByXY(
            context,
            objPositionComponent.x.toInt(),
            objPositionComponent.y.toInt()
        )

        if (enemyMultiple == 0) {
            val image = ImageView(context)
            image.setBackgroundResource(objBitmapComponent._bitmapId)
            imageHandler.addView(image)
            nameEnemy.text = objBitmapComponent._name
        }
        else{
            nameEnemy.text = "Group of monsters"
            for (enemy in enemiesLoader!!.enemies) {
                if (enemy.getComponent(BitmapComponent::class.java)!!._id.toString() in enemiesIds)
                {
                    val image = ImageView(context)
                    image.setBackgroundResource(enemy.getComponent(BitmapComponent::class.java)!!._bitmapId)
                    imageHandler.addView(image)
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

        // DESCCRIPTION
        val descriptionEnemy = dialog.findViewById(R.id.description) as TextView
        descriptionEnemy.text = objBitmapComponent._desc

        // SUB TEXT ENEMY
        val subTextEnemy = dialog.findViewById(R.id.subText) as TextView
        subTextEnemy.text = "Fight or Run"

        dialog.show()


    }

    private fun createTotemDialog(context: Context, obj: Entity){

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.70).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_totem)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.mainLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val totemBitmapComponent = obj.getComponent(BitmapComponent::class.java)!!

        val btnClose = dialog.findViewById(R.id.closeDialogTotem) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        val btnOk = dialog.findViewById(R.id.sacrifice) as Button
        btnOk.setOnClickListener {
            val upgradeComponent = obj.getComponent(UpgradeComponent::class.java)!!
            upgradeComponent.upgrade(context, player as Entity)
            dialog.dismiss()
        }

        // TOTEM ICON
        val imageView = dialog.findViewById(R.id.image) as ImageView
        imageView.setBackgroundResource(totemBitmapComponent._bitmapId)

        // NAME
        val nameTotem = dialog.findViewById(R.id.name) as TextView
        nameTotem.text = totemBitmapComponent._name
        // TOTEM DESCRIPTION
        val descriptionTotem = dialog.findViewById(R.id.description) as TextView
        descriptionTotem.text = totemBitmapComponent._desc
        // TOTEM SUB TEXT
        val subTextTotem = dialog.findViewById(R.id.subText) as TextView
        subTextTotem.text = "Trust your luck"
        dialog.show()

    }

    private fun showFilterPopup(view: View) {
        /*
        Drop item function
         */
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.action)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dropItem -> {
                    Toast.makeText(context, "Item id = ${view.id/1000}", Toast.LENGTH_SHORT).show()
                    player!!.getComponent(InventoryComponent::class.java)!!.dropItemById(view.id/1000)
                    DBHelperFunctions.dropItem(context, view.id/1000)
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
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_player)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.playerLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val playerHealthComponent = player!!.getComponent(HealthComponent::class.java)!!
        val objBitmapComponent =  obj.getComponent(BitmapComponent::class.java)!!

        // PLAYER SUB TEXT
        val healthTextPlayer = dialog.findViewById(R.id.healthText) as TextView

        val btnClose = dialog.findViewById(R.id.closePlayerDialog) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        val btnOk = dialog.findViewById(R.id.ok) as Button
        btnOk.setOnClickListener {
            DBHelperFunctions.restorePlayerHealth(context, player!!)
            healthTextPlayer.text = "Health is ${playerHealthComponent.healthPoints}"
        }

        val inventoryLayout = dialog.findViewById(R.id.inventoryLayout) as GridLayout
        val inventory = player!!.getComponent(InventoryComponent::class.java)!!

        var itemsTemp = DBHelperFunctions.loadAllItem(context)
        // TEST LOADING
        if(itemsTemp.count() < 2) {
            DBHelperFunctions.createItem(context, arrayListOf("1", "Item",  "0", "5", "0"))
            DBHelperFunctions.createItem(context, arrayListOf("2", "Item2", "0", "1", "0"))
            DBHelperFunctions.createItem(context, arrayListOf("3", "Item3", "0", "2", "0"))
            DBHelperFunctions.createItem(context, arrayListOf("4", "Item4", "0", "6", "0"))
            DBHelperFunctions.createItem(context, arrayListOf("5", "Item5", "0", "8", "0"))
            itemsTemp = DBHelperFunctions.loadAllItem(context)
        }

        for(item in itemsTemp) {
            inventory.addItem(item)
            inventory.addItem(item)
            inventory.addItem(item)
            inventory.addItem(item)
            inventory.addItem(item)
        }

        val items = inventory.getAllItems()

        for(item in items){
            val btn = Button(context)
            btn.text = "${item.name} : ${item.count}"
            btn.id = item.id*1000
            btn.setOnClickListener {
                showFilterPopup(it)
            }

            inventoryLayout.addView(btn)
        }

        // PLAYER ICON
        val imageView = dialog.findViewById(R.id.playerImage) as ImageView
        imageView.setBackgroundResource(objBitmapComponent._bitmapId)
        // PLAYER NAME
        val namePlayer = dialog.findViewById(R.id.name) as TextView
        namePlayer.text = objBitmapComponent._name
        // PLAYER DESCRIPTION
        val descriptionPlayer = dialog.findViewById(R.id.description) as TextView
        descriptionPlayer.text = objBitmapComponent._desc
        // PLAYER SUB TEXT
        healthTextPlayer.text = "Health is ${playerHealthComponent.healthPoints}"
        dialog.show()

    }

    // When User cilcks on dialog button, call this method
    private fun alertDialog(context: Context, obj: Entity) {
        val positionComponent = obj.getComponent(PositionComponent::class.java)
        val bitmapComponent = obj.getComponent(BitmapComponent::class.java)

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
                            "totem" -> createTotemDialog(context, obj)
                            "enemy" -> createEnemyDialog(context, obj)
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