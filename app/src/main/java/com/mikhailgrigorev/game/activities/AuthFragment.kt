package com.mikhailgrigorev.game.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mikhailgrigorev.game.R
import kotlinx.android.synthetic.main.fragment_auth.*

/**
 * Authorization Fragment
 */

class AuthFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AUTH TO MAIN
        menuBack.setOnClickListener {
            findNavController().navigate(R.id.action_AuthFragment_to_FirstFragment)
        }
    }
}