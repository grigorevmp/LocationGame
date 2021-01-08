package com.mikhailgrigorev.game.core.ecs.Components

import android.content.Context
import android.graphics.*
import com.mikhailgrigorev.game.core.ecs.Component
import com.mikhailgrigorev.game.game.Game

class BitmapComponent(
    positionComponent: PositionComponent,
    context: Context,
    id: Int = 0,
    name: String = "objectName",
    desc: String= "objectDesc",
    bitmapId: Int = 0,
    group: String = "static",
    multiple: Int = 0
) : Component() {

    // id
    var _id : Int = id
        private set
    // name
    var _name: String = name
        private set
    // description
    var _desc: String = desc
        private set
    // id картинки
    var _bitmapId: Int = bitmapId
        private set
    // id картинки
    private var _bitmapMultiple: Bitmap? = null
    // группа персонажей
    var _multiple: Int = multiple
        private set
    // картинка
    private var _bitmap : Bitmap? = null

    // классы
    var _group : String = group
        private set

    // сжимаем картинку до нужных размеров
    init {
        val size = positionComponent.size
        //val cBitmap = BitmapFactory.decodeResource(context.resources, _bitmapId)
        //_bitmap = Bitmap.createScaledBitmap(
        //    cBitmap,
        //    (size * Game.unitW).toInt(),
        //    (size * Game.unitH).toInt(),
        //    false
        //)
        //cBitmap.recycle()
        //val cBitmapMul = BitmapFactory.decodeResource(context.resources, context.resources.getIdentifier("tower", "drawable", context.packageName))
        //_bitmapMultiple = Bitmap.createScaledBitmap(
        //    cBitmapMul,
        //    (size * Game.unitW).toInt(),
        //    (size * Game.unitH).toInt(),
        //    false
        //)
        //cBitmapMul.recycle()
    }

    // тут будут вычисляться новые координаты
    override fun update() {}

    // рисуем картинку
    fun draw(paint: Paint?, canvas: Canvas) {
        val positionComponent = this.entity?.getComponent(PositionComponent::class.java)
        if(positionComponent != null)
            if(_multiple == 0) {
                _bitmap?.let {
                    canvas.drawBitmap(
                        it,
                        positionComponent.x * Game.unitW,
                        positionComponent.y * Game.unitH,
                        paint
                    )
                }
            }
        else{
                _bitmapMultiple?.let {
                    canvas.drawBitmap(
                        it,
                        positionComponent.x * Game.unitW,
                        positionComponent.y * Game.unitH,
                        paint
                    )
                }
            }
    }

}