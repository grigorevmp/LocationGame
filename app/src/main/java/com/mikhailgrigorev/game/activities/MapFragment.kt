package com.mikhailgrigorev.game.activities

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mikhailgrigorev.game.R
import com.mikhailgrigorev.game.game.Game
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * Main playing screen fragment
 */

class MapFragment : Fragment() {

    // Old status bar color #DELETE
    private var color: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Transparent status bar
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        color = requireActivity().window.statusBarColor
        requireActivity().window.statusBarColor = Color.TRANSPARENT

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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStop() {
        super.onStop()
        // Show status bar
        requireActivity().window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        requireActivity().window.statusBarColor = color
    }



}