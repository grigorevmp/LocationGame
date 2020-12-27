package com.mikhailgrigorev.game.game

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.mikhailgrigorev.game.activities.FightActivity
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.loader.BuildingsLoader
import com.mikhailgrigorev.game.loader.EnemiesLoader
import com.mikhailgrigorev.game.loader.TotemsLoader


class Game(context: Context?, gameThreadName: String= "GameThread"): SurfaceView(context), Runnable, SurfaceHolder.Callback {
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

    // When User cilcks on dialog button, call this method
    private fun alertDialog(context: Context, obj: Entity) {
        val positionComponent = obj.getComponent(PositionComponent::class.java)
        val bitmapComponent = obj.getComponent(BitmapComponent::class.java)

        var group = bitmapComponent!!._group
        if (group in "0,Skeleton,skeleton,Bones,devil,13,Zombie,zombie,Bones,devil")
            group = "enemy"

        var positive = "Close"
        when(group){
            "building" -> positive = "Respawn enemies"
            "player" -> positive = "Heal yourself"
            "totem" -> positive = "Buy"
            "enemy" -> positive = "Fight"
        }

        //Instantiate builder variable
        val builder = AlertDialog.Builder(context)

        val enemyMultiple = bitmapComponent._multiple
        // set title
        if(enemyMultiple == 0)
            builder.setTitle(bitmapComponent._name)
        else
            builder.setTitle("BIG MONSTER TOWER")

        //set content area
        builder.setMessage("You touched ${positionComponent!!.rect}")

        //set negative button
        builder.setPositiveButton(
            positive
        ) { _, _ ->
            when (group) {
                "enemy" -> {
                    val intent = Intent(mContext, FightActivity::class.java)
                    if(enemyMultiple == 0) {
                        // One enemy sample
                        intent.putExtra("enemyId", bitmapComponent._id.toString())
                        println("Fighting with... @id#" + bitmapComponent._id.toString())
                    } else {
                        var enemiesIds = ""
                        enemiesLoader = EnemiesLoader(context)
                        for (enemy in enemiesLoader!!.enemies){
                            if((obj.getComponent(PositionComponent::class.java)!!.x == enemy.getComponent(PositionComponent::class.java)!!.x)
                                and (obj.getComponent(PositionComponent::class.java)!!.y == enemy.getComponent(PositionComponent::class.java)!!.y)
                                and (enemy.getComponent(BitmapComponent::class.java)!!._multiple == 1)){
                                enemiesIds += enemy.getComponent(BitmapComponent::class.java)!!._id
                                enemiesIds += ","
                            }
                        }
                        enemiesIds = enemiesIds.substring(0, enemiesIds.length - 1)
                        intent.putExtra("enemyMulId", enemiesIds)
                        println("Fighting with... @id#$enemiesIds")
                    }
                    val origin = mContext as Activity
                    origin.startActivity(intent)
                    origin.finish()
                }
                "player" -> {
                    DBHelperFunctions().restorePlayerHealth(context, player!!)
                }
                "building" -> {
                    enemiesLoader = EnemiesLoader(context, true)
                    for (enemy in enemiesLoader!!.enemies)
                        gameEntities.add(enemy)
                }
            }
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
                        alertDialog(context, obj)
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