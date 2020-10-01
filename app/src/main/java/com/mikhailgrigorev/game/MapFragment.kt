package com.mikhailgrigorev.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mikhailgrigorev.game.core.GameView
import kotlinx.android.synthetic.main.fragment_map.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
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
        super.onViewCreated(view, savedInstanceState)

        menuBack.setOnClickListener {
            findNavController().navigate(R.id.action_PlayFragment_to_FirstFragment)
        }

        val gameView = GameView(this.context) // создаём gameView
        val gameLayout = gameLayout as LinearLayout // находим gameLayout
        gameLayout.addView(gameView) // и добавляем в него gameView

        forward.setOnClickListener {
            gameView.step()
        }

    }
}