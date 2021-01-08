package com.mikhailgrigorev.game.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.mikhailgrigorev.game.MapsActivity
import com.mikhailgrigorev.game.R
import kotlinx.android.synthetic.main.fragment_main.*

class GameMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            // Transparent status bar
            window.statusBarColor = Color.TRANSPARENT
            setContentView(R.layout.activity_game_menu)

            // MAIN - > PLAY
            menuPlay.setOnClickListener {
                //findNavController().navigate(R.id.action_FirstFragment_to_PlayFragment)
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
            // MAIN - > SETTINGS
            menuSettings.setOnClickListener {
                //findNavController().navigate(R.id.action_FirstFragment_to_SettingFragment)
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
            }

            gameName.setOnClickListener {
                gameName.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake)) }

        }
    }
}