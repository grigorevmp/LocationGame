package com.mikhailgrigorev.game.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mikhailgrigorev.game.R
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Main fragment
 */

class MainFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Transparent status bar
            requireActivity().window.statusBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Black icons
                requireActivity().window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(activity?.intent?.getStringExtra("Set")  == "Game") {
            activity?.intent?.putExtra("Set", "none")
            findNavController().navigate(R.id.action_FirstFragment_to_PlayFragment)
        }

        // MAIN - > PLAY
        menuPlay.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_PlayFragment)
        }
        // MAIN - > AUTH
        menuAuth.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_AuthFragment)
        }
        // MAIN - > SETTINGS
        menuSettings.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SettingFragment)
        }
    }


}