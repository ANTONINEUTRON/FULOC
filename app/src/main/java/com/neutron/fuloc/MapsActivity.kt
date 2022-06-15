package com.neutron.fuloc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.neutron.fuloc.data.LocationsInFulafia
import com.neutron.fuloc.data.LocationsInFulafia.calculateDistance
import com.neutron.fuloc.databinding.ActivityMapsBinding
import com.neutron.fuloc.fragments.LocationDescriptionFragment


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var activityMapsBinding:ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationManager: LocationManager by lazy{
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    private lateinit var USER_POSITION: LatLng
    private var isLocationPermissionGranted = false
    private lateinit var userLatLng: LatLng
    private var userLocationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(activityMapsBinding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onResume() {
        verifyLocationPermission()

        super.onResume()
    }

    /** Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Aus
        * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //SHOW SCHOOL DETAILS HERE
        displayPlacesInFulafia()
        // Add a marker in Sydney and move the camera

        //show user location
        if(isLocationPermissionGranted){
            detectUserLocation()
        }else{
            verifyLocationPermission()
        }


        val mapTypeSwitch = activityMapsBinding.mapTypeSwitch
        mapTypeSwitch.setOnClickListener {
            if(mapTypeSwitch.isChecked){
                Toast.makeText(this, "satelite mode", Toast.LENGTH_LONG).show()
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                mapFragment.getMapAsync(this)
            }else{
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                Toast.makeText(this, "normal mode", Toast.LENGTH_LONG).show()
                mapFragment.getMapAsync(this)
            }
        }

    }

    private fun verifyLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionGranted = true
            detectUserLocation()
        } else {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage("We need your permission to access location")
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(this, PermissionActivity::class.java))
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(this, "Some functionality will be limited!",Toast.LENGTH_LONG).show()
                    })
                    .show()
        }
    }

    private fun detectUserLocation(){
        if(isUserLocationOn()){
            updateUiWithUserLocation()
        }else{
            //alert user to on location
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setMessage("Your location is turned off\nyou should turn it on so that your location can be known and direction provided")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    })
                    .setNegativeButton("No", null)
                    .show()
        }
    }

    private fun isUserLocationOn(): Boolean {
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun updateUiWithUserLocation() {
        if(this::mMap.isInitialized){
            showUserLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showUserLocation() {
        mMap.isMyLocationEnabled = false
        //listen for user location and updates it on the map
        try {
            if (isLocationPermissionGranted) {
                    //Instantiating the Location request and setting the priority and the interval I need to update the location.
                    //Instantiating the Location request and setting the priority and the interval I need to update the location.
                    val locationRequest = LocationRequest.create()
                    locationRequest.interval = 0
                    locationRequest.numUpdates = 2
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                    //instantiating the LocationCallBack
                    val locationCallback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val lastKnownLocation: Location? = locationResult.lastLocation
                            if (lastKnownLocation != null) {
                                pointToUserCurrentLocation(lastKnownLocation)
                            }
                        }

                        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                            if (!locationAvailability.isLocationAvailable) {
                                Toast.makeText(
                                    this@MapsActivity,
                                    "Location is unavailable",
                                    Toast.LENGTH_LONG
                                ).show();
                            }
                            detectUserLocation()
                        }
                    }
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )

            }
        } catch (e: Exception){
            Log.e("FULOC", "Exception occured ${e.message.toString()}")
        }

    }

    private fun pointToUserCurrentLocation(location: Location) {
        //Showing the latitude, longitude and accuracy on the home screen.
        userLatLng = LatLng(
            location.latitude,
            location.longitude
        )
        //determine appropriete zoom to use
        val schoolPointOfRef = LocationsInFulafia.listOfPlaces[0]//initial location for list of location serve as school point
        val zoomLevel = getApproprieteZoom(
            userLatLng,
            LatLng(schoolPointOfRef.latitude,schoolPointOfRef.longitude)
        )
//
//        Toast.makeText(this,zoomLevel.toString(), Toast.LENGTH_LONG).show()
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                userLatLng, zoomLevel
            )
        )

        if (::userLatLng.isInitialized && userLocationMarker != null) {
            userLocationMarker!!.position = userLatLng
        } else {
            userLocationMarker = mMap.addMarker(
                MarkerOptions()
                    .position(userLatLng).title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
            )
        }
    }

    private fun getApproprieteZoom(userLatLng: LatLng, schoolLatLng: LatLng): Float {
        val distanceInMeters: Double = calculateDistance(schoolLatLng, userLatLng)
        if(distanceInMeters <= 508.497220){
            return 21f
        }else if(distanceInMeters <= 808.497220){
            return 20f
        }else if(distanceInMeters <= 1128.497220){
            return 17f
        }else if(distanceInMeters <= 9027.977761){
            return 13.5f
        }else if(distanceInMeters <= 72223.822090){
            return 10f
        }else if(distanceInMeters <= 588895.288400){
            return 6f
        }else if(distanceInMeters <= 2311162.307000){
            return 5f
        }else{
            return 3f
        }
    }

    private fun displayPlacesInFulafia() {
        //Get list of locations
        //Display on map
        LocationsInFulafia.listOfPlaces.forEachIndexed { index, place ->
            val loc = LatLng(place.latitude, place.longitude)
            val marker: Marker? = mMap.addMarker(
                MarkerOptions()
                    .position(loc)
                    .title(place.name)
                    .snippet(place.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
            marker?.tag = index //will be pass to bottom sheet
            if(index == 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,13.0f))
            }
            mMap.setOnMarkerClickListener { markerr ->
                if(markerr.tag != null && this::userLatLng.isInitialized){
                    LocationDescriptionFragment(markerr.tag as Int,userLatLng).show(supportFragmentManager, "Show Details")//index of the location in list will eneble fast access of location
                }
                false
            }
            //set listeners to markers
        }
        //
    }


}