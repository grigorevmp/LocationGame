@file:Suppress("DEPRECATION")

package com.mikhailgrigorev.game.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.service.autofill.Validators.not
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mikhailgrigorev.game.R
import kotlinx.android.synthetic.main.fragment_marat.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MaratFragment : Fragment() {


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

        val permissionCheck = checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        
        val l = this.fineLocationPermissionApproved()

        if (!l) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )}


        val a : FragmentActivity? = getActivity()
        if (a != null) {
            Log.d("NULL", "Notnull")
            val ctx: Context? = a?.getApplicationContext()
            Configuration.getInstance()
                .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
            Configuration.getInstance().setUserAgentValue(context?.getPackageName());
        }
        val map:MapView? = mapView
        Log.d("MAP", "YA TUT")

        map?.setTileSource(TileSourceFactory.MAPNIK)


        return inflater.inflate(R.layout.fragment_marat, container, false)
    }


    private fun fineLocationPermissionApproved(): Boolean {

        val context = context ?: return false

        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

}
