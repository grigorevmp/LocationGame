package com.mikhailgrigorev.game.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.game.Game
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * Main playing screen fragment
 */

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Transparent status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            requireActivity().window.statusBarColor = Color.TRANSPARENT
        }

        super.onViewCreated(view, savedInstanceState)

        //PLAY -> MAIN
        menuBack.setOnClickListener {
            findNavController().navigate(R.id.action_PlayFragment_to_FirstFragment)
        }

        val gameView = Game(this.context)
        val gameLayout = gameLayout as LinearLayout // находим gameLayout
        gameLayout.addView(gameView) // и добавляем в него gameView

        forward.setOnClickListener {
        }
    }


    override fun onStop() {
        super.onStop()
        gameLayout.removeAllViews()
        // Show status bar
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            requireActivity().window.statusBarColor = Color.TRANSPARENT
        }
        super.onResume()
        val gameView2 = Game(this.context, "GameThread2")
        gameLayout.addView(gameView2) // и добавляем в него gameView

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 1){
            val gameView = Game(this.context)
            val gameLayout = gameLayout as LinearLayout // находим gameLayout
            gameLayout.addView(gameView) // и добавляем в него gameView
        }
    }




}