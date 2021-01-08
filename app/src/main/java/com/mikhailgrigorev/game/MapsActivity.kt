package com.mikhailgrigorev.game


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mikhailgrigorev.game.activities.FightActivity
import com.mikhailgrigorev.game.core.data.NatureForces
import com.mikhailgrigorev.game.core.ecs.Components.*
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquipmentComponent
import com.mikhailgrigorev.game.core.ecs.Components.equipment.EquippableItem
import com.mikhailgrigorev.game.core.ecs.Components.inventory.InventoryComponent
import com.mikhailgrigorev.game.core.ecs.Components.inventory.item.Item
import com.mikhailgrigorev.game.core.ecs.Entity
import com.mikhailgrigorev.game.databases.DBHelperFunctions
import com.mikhailgrigorev.game.databases.ItemsDB
import com.mikhailgrigorev.game.entities.Enemy
import com.mikhailgrigorev.game.entities.Player
import com.mikhailgrigorev.game.entities.Totem
import com.mikhailgrigorev.game.loader.BuildingsLoader
import com.mikhailgrigorev.game.loader.EnemiesLoader
import com.mikhailgrigorev.game.loader.TotemsLoader
import com.mikhailgrigorev.game.views.ItemView
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

    // character
    private var player: Player? = null
    // map objects
    private var buildingsLoader: BuildingsLoader? = null
    private var totemsLoader: TotemsLoader? = null
    private var enemiesLoader: EnemiesLoader? = null
    // for storing game objects
    private var gameEntities = ArrayList<Entity>()

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

                    loadData()

                    for((i, entity) in gameEntities.withIndex()){
                        val random1: Double = 0.0001 + Math.random() * (0.0020 - 0.0001)
                        val random2: Double = 0.0001 + Math.random() * (0.0020 - 0.0001)

                        var multiplexer = 1
                        if(Math.random() < 0.5)
                            multiplexer = -1
                        val pos = LatLng(lastLocation.latitude + multiplexer*random1, lastLocation.longitude + multiplexer*random2)
                        val entityBitmap = entity.getComponent(BitmapComponent::class.java)!!._bitmapId
                        val entityName = entity.getComponent(BitmapComponent::class.java)!!._name
                        placeObjectOnMap(
                            pos,
                            BitmapDescriptorFactory.fromResource(entityBitmap),
                            "$i",
                            "THIS IS $entityName"
                        )

                    }


                    //val pos1 = LatLng(lastLocation.latitude + 0.0010, lastLocation.longitude)
                    //val pos2 = LatLng(lastLocation.latitude, lastLocation.longitude + 0.0010)
                    //val pos3 = LatLng(
                    //    lastLocation.latitude - 0.0005,
                    //    lastLocation.longitude - 0.0005
                    //)
                    //placeObjectOnMap(
                    //    pos1,
                    //    BitmapDescriptorFactory.fromResource(R.drawable.marker),
                    //    "Marker",
                    //    "THIS IS MARKER"
                    //)
                    //placeObjectOnMap(
                    //    pos2,
                    //    BitmapDescriptorFactory.fromResource(R.drawable.tower),
                    //    "Tower",
                    //    "THIS IS TOWER"
                    //)
                    //placeObjectOnMap(
                    //    pos3,
                    //    BitmapDescriptorFactory.fromResource(R.drawable.office),
                    //    "Office",
                    //    "THIS IS OFFICE"
                    //)
                    isPlaced = true
                }
            }
        }
        createLocationRequest()

    }

    private fun loadData(){
        player = Player(this)
        buildingsLoader = BuildingsLoader(this)
        totemsLoader = TotemsLoader(this)
        enemiesLoader = EnemiesLoader(this)
        for (obj in buildingsLoader!!.mapObjects)
            gameEntities.add(obj)
        for (totem in totemsLoader!!.totems)
            gameEntities.add(totem)
        for (enemy in enemiesLoader!!.enemies)
            gameEntities.add(enemy)
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr: String = "player"  // add these two lines
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

        if(p0?.title.toString() in "0123456789") {
            val entity = gameEntities[p0?.title!!.toInt()]

            val bitmapComponent = entity.getComponent(BitmapComponent::class.java)
            var group = bitmapComponent!!._group
            if (group in "0,Skeleton,skeleton,Bones,devil,13,Zombie,zombie,Bones,devil")
                group = "enemy"
            when (group) {
                "totem" -> createTotemDialog(this, entity as Totem)
                "enemy" -> createEnemyDialog(this, entity as Enemy)
                "player" -> createInventoryDialog(this, entity)
                else -> alertDialog(this, entity)
            }
        }
        if(p0?.title.toString() == "player") {
            player?.let { createInventoryDialog(this, it) }
        }

        return true
    }

    fun placeObjectOnMap(
        coordinates: LatLng,
        image: BitmapDescriptor,
        title: String = "",
        snippet: String = ""
    ){
        map.addMarker(
            MarkerOptions()
                .position(coordinates)
                .title(title)
                .snippet(snippet)
                .icon(image)
        )
    }

    @SuppressLint("SetTextI18n")
    private fun createEnemyDialog(context: Context, obj: Enemy) {
        val objPositionComponent = obj.getComponent(PositionComponent::class.java)!!
        val objBitmapComponent = obj.getComponent(BitmapComponent::class.java)!!
        val enemyMultiple = objBitmapComponent._multiple

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enemy)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.mainLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val paramsLO: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        paramsLO.setMargins(20, 0, 20, 0)


        val imageView = dialog.findViewById(R.id.oneImageHandler) as ImageView
        imageView.setBackgroundResource(objBitmapComponent._bitmapId)
        val aloneImageHandler = dialog.findViewById(R.id.aloneImageHandler) as LinearLayout
        val imageHandler = dialog.findViewById(R.id.imageHandler) as LinearLayout
        val multipleImageHandler = dialog.findViewById(R.id.multipleImageHandler) as HorizontalScrollView

        val nameEnemy = dialog.findViewById(R.id.name) as TextView
        val enemiesIds = DBHelperFunctions.loadEnemyIDByXY(
            context,
            objPositionComponent.x.toInt(),
            objPositionComponent.y.toInt()
        )

        if (enemyMultiple == 0) {
            multipleImageHandler.visibility = View.GONE
            nameEnemy.text = objBitmapComponent._name
        }
        else{
            aloneImageHandler.visibility = View.GONE
            nameEnemy.text = context.getString(R.string.dungeon)
            for (enemy in enemiesLoader!!.enemies) {
                if (enemy.getComponent(BitmapComponent::class.java)!!._id.toString() in enemiesIds)
                {
                    val image = ImageView(context)
                    image.setBackgroundResource(enemy.getComponent(BitmapComponent::class.java)!!._bitmapId)
                    imageHandler.addView(image, paramsLO)
                }
            }
        }

        // CLOSE BUTTON
        val btnClose = dialog.findViewById(R.id.closeEnemyDialog) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // POSITIVE BUTTON
        val btnOk = dialog.findViewById(R.id.fightButton) as Button
        btnOk.setOnClickListener {
            val intent = Intent(context, FightActivity::class.java)
            if (enemyMultiple == 0) {
                intent.putExtra("enemyMulId", objBitmapComponent._id.toString())
                println("Fighting with... @id#" + objBitmapComponent._id.toString())
            } else {
                intent.putExtra("enemyMulId", enemiesIds)
                println("Fighting with... @id#$enemiesIds")
            }
            val origin = context as Activity
            origin.startActivity(intent)
            origin.finish()
        }

        // DESCRIPTION
        val descriptionEnemy = dialog.findViewById(R.id.description) as TextView
        descriptionEnemy.text = objBitmapComponent._desc

        // Enemy characteristics
        // TOTEM PROPERTIES
        val enemyProperties = dialog.findViewById(R.id.enemyProperties) as LinearLayout

        val damageComponent = obj.getComponent(DamageComponent::class.java)!!
        val defenceComponent = obj.getComponent(DefenceComponent::class.java)!!

        val textView1 = TextView(context)
        textView1.text = "HP: ${obj.getComponent(HealthComponent::class.java)!!.healthPoints}"
        val textView2 = TextView(context)
        textView2.text = "Damage: ${damageComponent.physicalDamage}"
        val textView3 = TextView(context)
        textView3.text = "Air: ${damageComponent.natureForcesDamage[NatureForces.Air.ordinal]}"
        val textView4 = TextView(context)
        textView4.text = "Water: ${damageComponent.natureForcesDamage[NatureForces.Water.ordinal]}"
        val textView5 = TextView(context)
        textView5.text = "Fire: ${damageComponent.natureForcesDamage[NatureForces.Fire.ordinal]}"
        val textView6 = TextView(context)
        textView6.text = "Earth: ${damageComponent.natureForcesDamage[NatureForces.Earth.ordinal]}"
        val textView7 = TextView(context)
        textView7.text = "Defence: ${defenceComponent.physicalDefence}"
        val textView8 = TextView(context)
        textView8.text = "Air: ${defenceComponent.natureForcesDefence[NatureForces.Air.ordinal]}"
        val textView9 = TextView(context)
        textView9.text = "Water: ${defenceComponent.natureForcesDefence[NatureForces.Water.ordinal]}"
        val textView10 = TextView(context)
        textView10.text = "Fire: ${defenceComponent.natureForcesDefence[NatureForces.Fire.ordinal]}"
        val textView11 = TextView(context)
        textView11.text = "Earth: ${defenceComponent.natureForcesDefence[NatureForces.Earth.ordinal]}"

        enemyProperties.addView(textView1)
        enemyProperties.addView(textView2)
        enemyProperties.addView(textView3)
        enemyProperties.addView(textView4)
        enemyProperties.addView(textView5)
        enemyProperties.addView(textView6)
        enemyProperties.addView(textView7)
        enemyProperties.addView(textView8)
        enemyProperties.addView(textView9)
        enemyProperties.addView(textView10)
        enemyProperties.addView(textView11)

        // ITEMS
        val sItems = dialog.findViewById(R.id.sItems) as GridLayout

        var ids = ""
        for (enemy in enemiesLoader!!.enemies) {
            if (enemy.getComponent(BitmapComponent::class.java)!!._id.toString() in enemiesIds) {
                (enemy as Enemy).items.split('.').zip(enemy.itemsNum.split('.')).forEach { pair ->
                    ItemsDB.init(context)
                    val itemView = ItemView(context)
                    val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                    val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                    //val btn = Button(context, null, android.R.attr.borderlessButtonStyle)
                    val item = ItemsDB.loadItemByID(context, pair.component1().toInt())
                    if (item != null) {
                        if (pair.component1() !in ids) {
                            val bitmapId = context.resources.getIdentifier(
                                ItemsDB.loadItemBitmapByID(
                                    context,
                                    item.id
                                ), "drawable", context.packageName
                            )
                            btn.contentDescription = item.name
                            btn.setBackgroundResource(bitmapId)
                            itemView.id = item.id
                            countText.text = pair.component2()
                            sItems.addView(itemView)
                            ids += "${pair.component1()}."
                        }
                        else{
                            val itemViewID = dialog.findViewById<ItemView>(
                                pair.component1().toInt()
                            )
                            val countTextID = itemViewID.findViewById<TextView>(R.id.itemContentCount)
                            countTextID.text = "${pair.component2().toInt() + countTextID.text.toString().toInt()}"

                        }
                    }
                }
            }
        }

        dialog.show()

    }

    @SuppressLint("SetTextI18n")
    private fun createTotemDialog(context: Context, obj: Totem){

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.80).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_totem)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.totemLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params


        val totemBitmapComponent = obj.getComponent(BitmapComponent::class.java)!!

        val btnClose = dialog.findViewById(R.id.closeDialogTotem) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // TOTEM ICON
        val imageView = dialog.findViewById(R.id.image) as ImageView
        imageView.setBackgroundResource(totemBitmapComponent._bitmapId)

        // NAME
        val nameTotem = dialog.findViewById(R.id.name) as TextView
        nameTotem.text = totemBitmapComponent._name

        val noItems = dialog.findViewById(R.id.no_items) as TextView

        // TOTEM DESCRIPTION
        val descriptionTotem = dialog.findViewById(R.id.description) as TextView
        descriptionTotem.text = totemBitmapComponent._desc


        // TOTEM PROPERTIES

        val heart = dialog.findViewById(R.id.heart) as TextView
        heart.text = "+${obj.maxHealth}"

        val damage = dialog.findViewById(R.id.damage) as TextView
        damage.text = "+${obj.damage}"

        val damageAir = dialog.findViewById(R.id.damageA) as TextView
        damageAir.text = "+${obj.damageAir}"

        val damageWater = dialog.findViewById(R.id.damageW) as TextView
        damageWater.text = "+${obj.damageWater}"

        val damageFire = dialog.findViewById(R.id.damageF) as TextView
        damageFire.text = "+${obj.damageFire}"

        val damageEarth = dialog.findViewById(R.id.damageE) as TextView
        damageEarth.text = "+${obj.damageEarth}"

        val defence = dialog.findViewById(R.id.defence) as TextView
        defence.text = "+${obj.defence}"

        val defenceAir = dialog.findViewById(R.id.defenceA) as TextView
        defenceAir.text = "+${obj.defenceAir}"

        val defenceWater = dialog.findViewById(R.id.defenceW) as TextView
        defenceWater.text = "+${obj.defenceWater}"

        val defenceFire = dialog.findViewById(R.id.defenceF) as TextView
        defenceFire.text = "+${obj.defenceFire}"

        val defenceEarth = dialog.findViewById(R.id.defenceE) as TextView
        defenceEarth.text = "+${obj.defenceEarth}"

        // ITEMS
        val sItems = dialog.findViewById(R.id.sItems) as LinearLayout

        val sacrificeItem: ArrayList<Item> = ArrayList()

        val sacrifice = dialog.findViewById(R.id.sacrifice) as Button
        var isOk = true

        obj.items.split('.').zip(obj.itemsNum.split('.')).forEach { pair ->
            ItemsDB.init(context)
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            //val btn = Button(context, null, android.R.attr.borderlessButtonStyle)
            val item = ItemsDB.loadItemByID(context, pair.component1().toInt())
            if (item != null) {
                //btn.text = "${item.name}-${pair.component2()}"
                val bitmapId = context.resources.getIdentifier(
                    ItemsDB.loadItemBitmapByID(
                        context,
                        item.id
                    ), "drawable", context.packageName
                )
                btn.contentDescription = item.name
                btn.setBackgroundResource(bitmapId)
                btn.id = item.id
                countText.text = pair.component2()

                val playerItem = player!!.getComponent(InventoryComponent::class.java)!!.takeItem(
                    pair.component1().toInt()
                )
                if(playerItem == null){
                    isOk = false
                }
                else if(playerItem.count < pair.component2().toInt()){
                    isOk = false
                }
                sacrificeItem.add(Item(pair.component1().toInt(), "", pair.component2().toInt(), 0))
                sItems.addView(itemView)
            }
        }

        if(isOk) {
            noItems.visibility = View.GONE
            sacrifice.setOnClickListener {
                for (item in sacrificeItem) {
                    val inventoryComponent = player!!.getComponent(InventoryComponent::class.java)!!
                    inventoryComponent.dropItem(item)
                    if (inventoryComponent.takeItem(item.id) != null)
                        DBHelperFunctions.replaceItem(
                            context, item.id, inventoryComponent.takeItem(
                                item.id
                            )!!.count
                        )
                    else{
                        DBHelperFunctions.dropItem(context, item.id)
                    }
                }
                val upgradeComponent = obj.getComponent(UpgradeComponent::class.java)!!
                upgradeComponent.upgrade(context, player as Entity)
                Toast.makeText(context, "SUCCESSFULLY", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        else {
            sacrifice.setOnClickListener {
                Toast.makeText(context, "NO ITEMS", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            sacrifice.alpha = 0.4f
            sacrifice.isClickable = false
        }

        dialog.show()

    }



    private fun showEquipmentItemPopup(view: View, item: EquippableItem, dialog: Dialog) {
        val popupMenu = PopupMenu(this, view)
        val inventory = player!!.getComponent(InventoryComponent::class.java)!!
        popupMenu.inflate(R.menu.menu_equipment_item)

        // TEST
        // itemsCount.title = inventory.takeItem(view.id / 1000)!!.count.toString()
        // EQUIP

        val itemId = view.id / 1000

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dropItem -> {
                    Toast.makeText(this, "Item id = $itemId", Toast.LENGTH_SHORT)
                        .show()

                    inventory.dropItem(item)
                    DBHelperFunctions.dropItem(this, itemId)
                    view.alpha = 0.4f
                    view.isClickable = false
                    return@setOnMenuItemClickListener true
                }
                R.id.unEquip -> {
                    item.takeFromEntity(player as Entity)
                    DBHelperFunctions.unEquipItem(this, itemId)

                    val equipmentLayout = dialog.findViewById(R.id.equipmentLayout) as GridLayout
                    val equipment = player!!.getComponent(EquipmentComponent::class.java)!!
                    equipmentLayout.removeAllViews()

                    // Loading weapon
                    val weapon = equipment.weapon

                    if (weapon != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                weapon.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = weapon.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, weapon, dialog)
                        }
                        btn.id = weapon.id * 1000
                        countText.text = weapon.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading amulet
                    val amulet = equipment.amulet

                    if (amulet != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                amulet.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = amulet.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, amulet, dialog)
                        }
                        btn.id = amulet.id * 1000
                        countText.text = amulet.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading belt
                    val belt = equipment.belt

                    if (belt != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                belt.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = belt.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, belt, dialog)
                        }
                        btn.id = belt.id * 1000
                        countText.text = belt.count.toString()
                        equipmentLayout.addView(itemView)
                    }
                    // Loading head
                    val head = equipment.head

                    if (head != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                head.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = head.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, head, dialog)
                        }
                        btn.id = head.id * 1000
                        countText.text = head.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading ring
                    val ring = equipment.ring

                    if (ring != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                ring.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = ring.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, ring, dialog)
                        }
                        btn.id = ring.id * 1000
                        countText.text = ring.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading armor
                    val armor = equipment.armor

                    if (armor != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                armor.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = armor.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, armor, dialog)
                        }
                        btn.id = armor.id * 1000
                        countText.text = armor.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    val inventoryLayout = dialog.findViewById(R.id.inventoryLayout) as GridLayout
                    inventoryLayout.removeAllViews()
                    val items = inventory.getAllItems()

                    for (tmpItem in items) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                tmpItem.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = tmpItem.name
                        btn.setBackgroundResource(bitmapId)
                        btn.id = tmpItem.id * 1000
                        btn.setOnClickListener {
                            showFilterPopup(it, dialog)
                        }

                        countText.text = tmpItem.count.toString()

                        inventoryLayout.addView(itemView)
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    Toast.makeText(this, "Meow", Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener false
                }
            }
        }
        popupMenu.show()
    }


    private fun showFilterPopup(view: View, dialog: Dialog) {
        /*
        Drop item function
         */
        val popupMenu = PopupMenu(this, view)
        val inventory = player!!.getComponent(InventoryComponent::class.java)!!
        popupMenu.inflate(R.menu.menu_inventory_item)
        val itemsCount = popupMenu.menu.findItem(R.id.items_count)

        // TEST
        // itemsCount.title = inventory.takeItem(view.id / 1000)!!.count.toString()
        // EQUIP
        itemsCount.title = "EQUIP"

        val item = inventory.takeItem(view.id / 1000) ?: return
        val itemId = view.id / 1000

        if(!item.isType(Item.equippable))
            popupMenu.menu.removeItem(R.id.items_count)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.dropItem -> {
                    Toast.makeText(this, "Item id = $itemId", Toast.LENGTH_SHORT)
                        .show()

                    inventory.dropItem(item)
                    DBHelperFunctions.dropItem(this, itemId)

                    val inventoryLayout = dialog.findViewById(R.id.inventoryLayout) as GridLayout
                    inventoryLayout.removeAllViews()
                    val items = inventory.getAllItems()

                    for (tmpItem in items) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                tmpItem.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = tmpItem.name
                        btn.setBackgroundResource(bitmapId)
                        btn.id = tmpItem.id * 1000
                        btn.setOnClickListener {
                            showFilterPopup(it, dialog)
                        }

                        countText.text = tmpItem.count.toString()

                        inventoryLayout.addView(itemView)
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.items_count -> {
                    val equippableItem = item as EquippableItem
                    equippableItem.equipToEntity(player as Entity)
                    DBHelperFunctions.equipItem(this, itemId)

                    val equipmentLayout = dialog.findViewById(R.id.equipmentLayout) as GridLayout
                    val equipment = player!!.getComponent(EquipmentComponent::class.java)!!
                    equipmentLayout.removeAllViews()
                    // Loading weapon
                    val weapon = equipment.weapon

                    if (weapon != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                weapon.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = weapon.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, weapon, dialog)
                        }
                        btn.id = weapon.id * 1000
                        countText.text = weapon.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading amulet
                    val amulet = equipment.amulet

                    if (amulet != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                amulet.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = amulet.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, amulet, dialog)
                        }
                        btn.id = amulet.id * 1000
                        countText.text = amulet.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading belt
                    val belt = equipment.belt

                    if (belt != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                belt.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = belt.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, belt, dialog)
                        }
                        btn.id = belt.id * 1000
                        countText.text = belt.count.toString()
                        equipmentLayout.addView(itemView)
                    }
                    // Loading head
                    val head = equipment.head

                    if (head != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                head.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = head.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, head, dialog)
                        }
                        btn.id = head.id * 1000
                        countText.text = head.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading ring
                    val ring = equipment.ring

                    if (ring != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                ring.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = ring.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, ring, dialog)
                        }
                        btn.id = ring.id * 1000
                        countText.text = ring.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    // Loading armor
                    val armor = equipment.armor

                    if (armor != null) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        ItemsDB.init(this)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                armor.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = armor.name
                        btn.setBackgroundResource(bitmapId)
                        btn.setOnClickListener {
                            showEquipmentItemPopup(it, armor, dialog)
                        }
                        btn.id = armor.id * 1000
                        countText.text = armor.count.toString()
                        equipmentLayout.addView(itemView)
                    }

                    val inventoryLayout = dialog.findViewById(R.id.inventoryLayout) as GridLayout
                    inventoryLayout.removeAllViews()
                    val items = inventory.getAllItems()

                    for (tmpItem in items) {
                        val itemView = ItemView(this)
                        val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                        val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                        val bitmapId = this.resources.getIdentifier(
                            ItemsDB.loadItemBitmapByID(
                                this,
                                tmpItem.id
                            ), "drawable", this.packageName
                        )
                        btn.contentDescription = tmpItem.name
                        btn.setBackgroundResource(bitmapId)
                        btn.id = tmpItem.id * 1000
                        btn.setOnClickListener {
                            showFilterPopup(it, dialog)
                        }

                        countText.text = tmpItem.count.toString()

                        inventoryLayout.addView(itemView)
                    }
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    Toast.makeText(this, "Meow", Toast.LENGTH_SHORT).show()
                    return@setOnMenuItemClickListener false
                }
            }
        }
        popupMenu.show()
    }

    @SuppressLint("SetTextI18n")
    private fun createInventoryDialog(context: Context, obj: Entity){

        val dialog = Dialog(context)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_player)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set size
        val mainLayout = dialog.findViewById(R.id.playerLayout) as LinearLayout
        val params: ViewGroup.LayoutParams = mainLayout.layoutParams
        params.width = width
        mainLayout.layoutParams = params

        val playerHealthComponent = player!!.getComponent(HealthComponent::class.java)!!
        val objBitmapComponent =  obj.getComponent(BitmapComponent::class.java)!!
        val objDefenceComponent =  obj.getComponent(DefenceComponent::class.java)!!
        val objDamageComponent =  obj.getComponent(DamageComponent::class.java)!!

        // PLAYER ADDITIONAL TEXT
        val healthTextPlayer = dialog.findViewById(R.id.healthText) as TextView
        healthTextPlayer.text = "Health is ${playerHealthComponent.healthPoints}/${playerHealthComponent.maxHealthPoints}"
        val playerHealthProgress = dialog.findViewById(R.id.playerHealthProgress) as ProgressBar
        playerHealthProgress.max = playerHealthComponent.maxHealthPoints
        playerHealthProgress.progress = playerHealthComponent.healthPoints

        val btnClose = dialog.findViewById(R.id.closePlayerDialog) as ImageButton
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        val btnOk = dialog.findViewById(R.id.ok) as Button
        btnOk.setOnClickListener {
            DBHelperFunctions.restorePlayerHealth(context, player!!)
            healthTextPlayer.text = "Health is ${playerHealthComponent.healthPoints}/${playerHealthComponent.maxHealthPoints}"
            playerHealthProgress.progress = playerHealthComponent.healthPoints
        }


        // -------------------------------------
        // EQUIPMENT INVENTORY
        // -------------------------------------

        val equipmentLayout = dialog.findViewById(R.id.equipmentLayout) as GridLayout
        val equipment = player!!.getComponent(EquipmentComponent::class.java)!!

        // Loading weapon
        val weapon = equipment.weapon

        if(weapon != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    weapon.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = weapon.name
            btn.setBackgroundResource(bitmapId)
            btn.setOnClickListener {
                showEquipmentItemPopup(it, weapon, dialog)
            }
            btn.id = weapon.id*1000
            countText.text = weapon.count.toString()
            equipmentLayout.addView(itemView)
        }

        // Loading amulet
        val amulet = equipment.amulet

        if(amulet != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    amulet.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = amulet.name
            btn.setBackgroundResource(bitmapId)
            btn.setOnClickListener {
                showEquipmentItemPopup(it, amulet, dialog)
            }
            btn.id = amulet.id*1000
            countText.text = amulet.count.toString()
            equipmentLayout.addView(itemView)
        }

        // Loading belt
        val belt = equipment.belt

        if(belt != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    belt.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = belt.name
            btn.setBackgroundResource(bitmapId)
            btn.setOnClickListener {
                showEquipmentItemPopup(it, belt, dialog)
            }
            btn.id = belt.id*1000
            countText.text = belt.count.toString()
            equipmentLayout.addView(itemView)
        }
        // Loading head
        val head = equipment.head

        if(head != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    head.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = head.name
            btn.setBackgroundResource(bitmapId)
            btn.setOnClickListener {
                showEquipmentItemPopup(it, head, dialog)
            }
            btn.id = head.id*1000
            countText.text = head.count.toString()
            equipmentLayout.addView(itemView)
        }

        // Loading ring
        val ring = equipment.ring

        if(ring != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    ring.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = ring.name
            btn.setBackgroundResource(bitmapId)
            btn.setOnClickListener {
                showEquipmentItemPopup(it, ring, dialog)
            }
            btn.id = ring.id*1000
            countText.text = ring.count.toString()
            equipmentLayout.addView(itemView)
        }

        // Loading armor
        val armor = equipment.armor

        if(armor != null) {
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            ItemsDB.init(context)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    armor.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = armor.name
            btn.setBackgroundResource(bitmapId)
            btn.setOnClickListener {
                showEquipmentItemPopup(it, armor, dialog)
            }
            btn.id = armor.id*1000
            countText.text = armor.count.toString()
            equipmentLayout.addView(itemView)
        }


        // -------------------------------------
        // LOADING INVENTORY
        // -------------------------------------

        val inventoryLayout = dialog.findViewById(R.id.inventoryLayout) as GridLayout
        val inventory = player!!.getComponent(InventoryComponent::class.java)!!
        val loadItems = dialog.findViewById(R.id.load_items) as Button
        loadItems.setOnClickListener {
            var itemsTemp = DBHelperFunctions.loadAllItem(context)
            // TEST LOADING

            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "1",
                    "Fresh meat",
                    "0",
                    "30",
                    "${Item.stackable}",
                    "null",
                    "",
                    "0"
                )
            )
            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "2",
                    "Bones",
                    "0",
                    "30",
                    "${Item.stackable}",
                    "null",
                    "",
                    "0"
                )
            )
            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "3",
                    "Rotten meat",
                    "30",
                    "2",
                    "${Item.stackable}",
                    "null",
                    "",
                    "0"
                )
            )
            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "4",
                    "Wood",
                    "0",
                    "30",
                    "${Item.stackable}",
                    "null",
                    "",
                    "0"
                )
            )
            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "5",
                    "Souls",
                    "0",
                    "30",
                    "${Item.stackable}",
                    "null",
                    "",
                    "0"
                )
            )
            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "9",
                    "Ring",
                    "0",
                    "1",
                    "${Item.equippable}",
                    "jewelry",
                    "0.0.5.2.1.0.0.0.0.0.0.0",
                    "0"
                )
            )

            DBHelperFunctions.createItem(
                context, arrayListOf(
                    "520",
                    "Sword",
                    "0",
                    "1",
                    "${Item.equippable}",
                    "weapon",
                    "15.0.0.0.0.0.0",
                    "1"
                )
            )
            itemsTemp = DBHelperFunctions.loadAllItem(context)


            inventory.delItems()

            for(item in itemsTemp)
                inventory.addItem(item)

            val items = inventory.getAllItems()


            inventoryLayout.removeAllViews()
            for(item in items){
                val itemView = ItemView(context)
                val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
                val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
                ItemsDB.init(context)
                val bitmapId = context.resources.getIdentifier(
                    ItemsDB.loadItemBitmapByID(
                        context,
                        item.id
                    ), "drawable", context.packageName
                )
                //val btn = Button(context, null, android.R.attr.borderlessButtonStyle)
                btn.contentDescription = item.name
                btn.setBackgroundResource(bitmapId)
                btn.id = item.id*1000
                btn.setOnClickListener {
                    showFilterPopup(it, dialog)
                }

                countText.text = item.count.toString()

                inventoryLayout.addView(itemView)
            }
        }

        val items = inventory.getAllItems()

        for(item in items){
            val itemView = ItemView(context)
            val btn = itemView.findViewById<ImageButton>(R.id.itemContent)
            val countText = itemView.findViewById<TextView>(R.id.itemContentCount)
            val bitmapId = context.resources.getIdentifier(
                ItemsDB.loadItemBitmapByID(
                    context,
                    item.id
                ), "drawable", context.packageName
            )
            btn.contentDescription = item.name
            btn.setBackgroundResource(bitmapId)
            btn.id = item.id*1000
            btn.setOnClickListener {
                showFilterPopup(it, dialog)
            }

            countText.text = item.count.toString()

            inventoryLayout.addView(itemView)
        }

        // PLAYER DAMAGE
        val damage  = dialog.findViewById(R.id.damage) as TextView
        damage.text = "${objDamageComponent.physicalDamage}"
        val damageA = dialog.findViewById(R.id.damageA) as TextView
        damageA.text = "${objDamageComponent.natureForcesDamage[NatureForces.Air.ordinal]}"
        val damageF = dialog.findViewById(R.id.damageF) as TextView
        damageF.text = "${objDamageComponent.natureForcesDamage[NatureForces.Fire.ordinal]}"
        val damageW = dialog.findViewById(R.id.damageW) as TextView
        damageW.text = "${objDamageComponent.natureForcesDamage[NatureForces.Water.ordinal]}"
        val damageE = dialog.findViewById(R.id.damageE) as TextView
        damageE.text = "${objDamageComponent.natureForcesDamage[NatureForces.Earth.ordinal]}"

        // PLAYER DEFENCE
        val defence  = dialog.findViewById(R.id.defence) as TextView
        defence.text = "${objDefenceComponent.physicalDefence}"
        val defenceA = dialog.findViewById(R.id.defenceA) as TextView
        defenceA.text = "${objDefenceComponent.natureForcesDefence[NatureForces.Air.ordinal]}"
        val defenceF = dialog.findViewById(R.id.defenceF) as TextView
        defenceF.text = "${objDefenceComponent.natureForcesDefence[NatureForces.Fire.ordinal]}"
        val defenceW = dialog.findViewById(R.id.defenceW) as TextView
        defenceW.text = "${objDefenceComponent.natureForcesDefence[NatureForces.Water.ordinal]}"
        val defenceE = dialog.findViewById(R.id.defenceE) as TextView
        defenceE.text = "${objDefenceComponent.natureForcesDefence[NatureForces.Earth.ordinal]}"

        // PLAYER ICON
        val imageView = dialog.findViewById(R.id.playerImage) as ImageView
        imageView.setBackgroundResource(objBitmapComponent._bitmapId)
        // PLAYER NAME
        val namePlayer = dialog.findViewById(R.id.name) as TextView
        namePlayer.text = objBitmapComponent._name

        dialog.show()

    }

    // When User cilcks on dialog button, call this method
    private fun alertDialog(context: Context, obj: Entity) {
        val positionComponent = obj.getComponent(PositionComponent::class.java)

        //Instantiate builder variable
        val builder = AlertDialog.Builder(context)

        //set content area
        builder.setMessage("You touched ${positionComponent!!.rect}")

        //set negative button
        builder.setPositiveButton(
            "Respawn enemies"
        ) { _, _ ->
            enemiesLoader = EnemiesLoader(context, true)
            for (enemy in enemiesLoader!!.enemies) {
                gameEntities.add(enemy)

                val i = gameEntities.size
                val random1: Double = 0.0001 + Math.random() * (0.0020 - 0.0001)
                val random2: Double = 0.0001 + Math.random() * (0.0020 - 0.0001)

                var multiplexer = 1
                if(Math.random() < 0.5)
                    multiplexer = -1
                val pos = LatLng(lastLocation.latitude + multiplexer*random1, lastLocation.longitude + multiplexer*random2)
                val entityBitmap = enemy.getComponent(BitmapComponent::class.java)!!._bitmapId
                val entityName = enemy.getComponent(BitmapComponent::class.java)!!._name
                placeObjectOnMap(
                    pos,
                    BitmapDescriptorFactory.fromResource(entityBitmap),
                    "$i",
                    "THIS IS $entityName"
                )

            }
        }
        //set positive button
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, id ->
            // User cancelled the dialog
        }
        builder.show()
    }

}