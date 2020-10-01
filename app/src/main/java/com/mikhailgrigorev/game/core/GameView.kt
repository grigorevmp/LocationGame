package com.mikhailgrigorev.game.core

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import com.mikhailgrigorev.game.character.Player


internal class GameView(context: Context?): SurfaceView(context), Runnable, SurfaceHolder.Callback {

    //TODO Optimize
    //TODO Own thread class

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

    // for thread control
    private var gameThread: Thread? = null

    // for object drawing
    private var surfaceHolder: SurfaceHolder? = null
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
                //Check if the x and y position of the touch is inside the bitmap
                if (x >= player!!.getXPos() * unitW &&
                    x <= player!!.getXPos() * unitW + player!!.getSizePos() * unitW &&
                    y >= player!!.getYPos() * unitH &&
                    y <= player!!.getYPos() * unitW + player!!.getSizePos() * unitH
                ) {
                    Toast.makeText(context, "SHIP TOUCHED", Toast.LENGTH_SHORT).show()
                    //Bitmap touched
                }
                return true
            }
        }
        return false
    }

    init{
        // init objects for drawing
        surfaceHolder = holder
        paint = Paint()
        // init thread
        gameThread = Thread(this, "Поток для примера")
        Log.i("MY GAME", "Создан второй поток $gameThread")
        gameThread!!.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
       /* var retry = true
        // завершаем работу потока
        // завершаем работу потока
        gameRunning = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // если не получилось, то будем пытаться еще и еще
            }
        }*/
    }

    override fun run() {
        while (gameRunning) {
            update()
            draw()
            //control()
        }
    }


    fun step(){
        player?.step()
    }


    private fun update() {
        if (!firstTime) {
            player?.update()
        }
    }

    private fun draw() {
        if (surfaceHolder!!.surface.isValid) {  //проверяем валидный ли surface
            if (firstTime) { // инициализация при первом запуске
                firstTime = false
                unitW =
                    surfaceHolder!!.surfaceFrame.width() / maxX.toFloat() // вычисляем число пикселей в юните
                unitH = surfaceHolder!!.surfaceFrame.height() / maxY.toFloat()
                player = Player(context) // добавляем корабль
            }
            canvas = surfaceHolder!!.lockCanvas() // закрываем canvas
            canvas!!.drawColor(Color.BLACK) // заполняем фон чёрным
            player?.draw(paint, canvas!!) // рисуем корабль
            surfaceHolder!!.unlockCanvasAndPost(canvas) // открываем canvas
        }
    }

    private fun control() {
        try {
            Thread.sleep(60)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }


}