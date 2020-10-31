package com.mikhailgrigorev.game

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mikhailgrigorev.game.activities.MainActivity
import kotlinx.android.synthetic.main.activity_fight.*

class FightActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fight)

        // AUTH TO MAIN
        menuBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Set", "Game")
            startActivity(intent)
            finish()
        }
    }
}