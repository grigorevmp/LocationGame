package com.mikhailgrigorev.game


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private val TAG = MapsActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mPositionMarker: Marker? = null
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }
    private var myLoc: Marker? = null
    private val DEFAULT_ZOOM_LEVEL = 18f
    private val MIN_ZOOM_LEVEL = 16.5f
    private val MAX_ZOOM_LEVEL = 20f
    private var isPlaced = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(lastLocation.latitude, lastLocation.longitude)) // Sets the center of the map to Mountain View
                    .zoom(DEFAULT_ZOOM_LEVEL)            // Sets the zoom
                   // .bearing(0f)         // Sets the orientation of the camera to east
                    .tilt(45f)            // Sets the tilt of the camera to 30 degrees
                    .build()              // Creates a CameraPosition from the builder
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                if (!isPlaced){
                    val pos1 = LatLng(lastLocation.latitude + 0.0010, lastLocation.longitude)
                    val pos2 = LatLng(lastLocation.latitude, lastLocation.longitude + 0.0010)
                    val pos3 = LatLng(lastLocation.latitude - 0.0005, lastLocation.longitude - 0.0005)
                    placeObjectOnMap(pos1, BitmapDescriptorFactory.fromResource(R.drawable.marker), "Marker", "THIS IS MARKER")
                    placeObjectOnMap(pos2, BitmapDescriptorFactory.fromResource(R.drawable.tower),"Tower", "THIS IS TOWER")
                    placeObjectOnMap(pos3, BitmapDescriptorFactory.fromResource(R.drawable.office),"Office", "THIS IS OFFICE")
                    isPlaced = true
                }
            }
        }
        createLocationRequest()

    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr: String = "It's you"  // add these two lines
        markerOptions.title(titleStr)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.player))

        myLoc?.remove()

        myLoc = map.addMarker(markerOptions)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val marat = LatLng(54.11, 54.11)
        val misha = LatLng(53.97952, 38.19016)
        /*
        map.addMarker(
            MarkerOptions()
                .position(marat)
                .title("1st marker")
                .snippet("Marker in my home")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
        )

        map.addMarker(
            MarkerOptions()
                .position(misha)
                .title("2st marker")
                .snippet("Misha's home")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
        )
         */

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM_LEVEL))

            }
        }


        map.setMinZoomPreference(MIN_ZOOM_LEVEL)
        map.setMaxZoomPreference(MAX_ZOOM_LEVEL)

        setMapStyle(map)
        map.setOnMarkerClickListener(this)
        val ui = map.uiSettings
        ui.isMapToolbarEnabled = false
        ui.isMyLocationButtonEnabled = false
        ui.isTiltGesturesEnabled = false
        ui.isCompassEnabled = false
        // ui.isScrollGesturesEnabled = false

        // Camera Tilt
        val newTilt = 45F
        val cameraPosition = CameraPosition.Builder(map.cameraPosition).tilt(newTilt).build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

    }


    private fun setMapStyle(map: GoogleMap){
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )
            if (!success){
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Resources.NotFoundException){
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation(){
        if (isPermissionGranted()){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        this@MapsActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION){
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)){
                enableMyLocation()
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val toast = Toast.makeText(applicationContext, p0?.snippet, Toast.LENGTH_SHORT)
        toast.show()
        return true
    }

    fun placeObjectOnMap(coordinates:LatLng, image:BitmapDescriptor, title:String="", snippet:String=""){
        map.addMarker(
            MarkerOptions()
                .position(coordinates)
                .title(title)
                .snippet(snippet)
                .icon(image)
        )
    }
}