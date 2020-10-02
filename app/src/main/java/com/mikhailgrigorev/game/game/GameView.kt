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
import java.lang.Exception


class GameView(context: Context?): SurfaceView(context), Runnable, SurfaceHolder.Callback {

    companion object{
        // sizes
        var maxX = 20
        var maxY = 28
        var unitW = 0f
        var unitH = 0f
    }

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
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //Check if the x and y position of the touch is inside the bitmap rectangle
                val objectRect = player!!.getObjectRect()
                if (objectRect.contains(x, y)) {
                    Toast.makeText(context, "SHIP TOUCHED", Toast.LENGTH_SHORT).show()
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
            draw()
            control()
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
            if (firstTime) { // инициализация при первом запуске
                firstTime = false
                unitW =
                    surfaceHolder.surfaceFrame.width() / maxX.toFloat() // вычисляем число пикселей в юните
                unitH = surfaceHolder.surfaceFrame.height() / maxY.toFloat()
                player = Player(context) // добавляем корабль
            }
            canvas = surfaceHolder.lockCanvas() // закрываем canvas
            canvas!!.drawColor(Color.BLACK) // заполняем фон чёрным
            player?.draw(paint, canvas!!) // рисуем корабль
            surfaceHolder.unlockCanvasAndPost(canvas) // открываем canvas
        }
    }

    private fun control() {
        timeMillis = (System.nanoTime() - startTime) / 1000000
        waitTime = targetTime - timeMillis
        try {
            Thread.sleep(waitTime)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Exception) {
        }

    }


}