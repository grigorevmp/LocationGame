package com.mikhailgrigorev.game.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.mikhailgrigorev.game.R

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */

class MainFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.menuPlay).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_PlayFragment)
        }
        view.findViewById<Button>(R.id.menuAuth).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_AuthFragment)
        }
        view.findViewById<Button>(R.id.menuSettings).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SettingFragment)
        }
    }
}