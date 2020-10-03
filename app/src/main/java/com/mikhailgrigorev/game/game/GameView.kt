package com.mikhailgrigorev.game.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.mikhailgrigorev.game.character.Player
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.core.ecs.Components.BitmapComponent
import com.mikhailgrigorev.game.core.ecs.Components.PositionComponent
import com.mikhailgrigorev.game.loader.MapLoader
import java.lang.Exception


class GameView(context: Context?): SurfaceView(context), Runnable, SurfaceHolder.Callback {

    companion object{
        // sizes
        var maxX = 20
        var maxY = 28
        var unitW = 0f
        var unitH = 0f
    }

    // map
    var mapLoader: MapLoader? = null

    // for storing game objects
    private var gameEntities = ArrayList<Entity>()

    // for game control
    private var gameRunning = true
    private var firstTime = true
    private val targetFPS = 60 //fps
    private val targetTime = (1000 / targetFPS).toLong()
    private var startTime: Long = 0
    private var timeMillis: Long = 0
    private var waitTime: Long = 0

    // for thread control
    private var gameThread: Thread

    // for object drawing
    private var surfaceHolder: SurfaceHolder = holder
    private var paint: Paint? = null
    private var canvas: Canvas? = null

    // character
    private var player: Player? = null

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
                for(obj in gameEntities){
                    val positionComponent = obj.getComponent(PositionComponent::class.java)
                    val bitmapComponent = obj.getComponent(BitmapComponent::class.java)
                    if (positionComponent!= null && positionComponent._rect.contains(x, y))
                        Toast.makeText(context,
                            "You touch " +
                                    bitmapComponent!!._name +
                                " at " +
                                positionComponent._rect,
                            Toast.LENGTH_SHORT)
                            .show()
                }
                return true
            }
        }
        return false
    }

    init{
        // init objects for drawing
        surfaceHolder.addCallback(this)
        paint = Paint()

        // Thread
        gameThread = Thread(this, "Поток для примера")
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
        while (gameRunning) {
            startTime = System.nanoTime()
            update()
            try {
                draw()
            }
            catch (e: Exception){
                Toast.makeText(context, "Написать Мише, если это увидите", Toast.LENGTH_SHORT).show()
            }
            threadControl()
        }
    }

    fun step(){
        player?.stepUp()
    }

    private fun update() {
        if (!firstTime) {
            player?.update()
        }
    }

    private fun draw() {
        if (surfaceHolder.surface.isValid) {  //проверяем валидный ли surface
            if (firstTime) {
                firstTime = false
                unitW = surfaceHolder.surfaceFrame.width() / maxX.toFloat()
                unitH = surfaceHolder.surfaceFrame.height() / maxY.toFloat()
                // init objects
                player = Player(context)
                mapLoader = MapLoader(context)
                for (obj in mapLoader!!.mapObjects){
                    gameEntities.add(obj)
                }
                gameEntities.add(player!!)
            }
            // close canvas
            canvas = surfaceHolder.lockCanvas()
            // background
            canvas!!.drawColor(Color.BLACK)
            // draw objects
            for(obj in gameEntities) {
                obj.getComponent(BitmapComponent::class.java)?.draw(paint, canvas!!)
            }
            // open canvas
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun threadControl() {
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