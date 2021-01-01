package com.mikhailgrigorev.game.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.mikhailgrigorev.game.R


class ItemView : FrameLayout {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initView()
    }

    constructor(context: Context?) : super(context!!) {
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.item_view, this)
    }
}