package com.mikhailgrigorev.game.activities

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

        val gameView = Game(this.context)
        val gameLayout = gameLayout as LinearLayout // находим gameLayout
        gameLayout.addView(gameView) // и добавляем в него gameView

        forward.setOnClickListener {
            gameView.step()
        }

    }

}