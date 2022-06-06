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


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
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
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onResume() {
        getLocationPermission()

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
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-4.0, 4.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        //show user location
        if(isLocationPermissionGranted){
            detectUserLocation()
        }else{
            getLocationPermission()
        }
    }

    private fun getLocationPermission() {
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
        //listen for user location and updates it on the map
        try {
            if (isLocationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        val lastKnownLocation: Location = task.result
                        if (lastKnownLocation != null) {
                            pointToUserCurrentLocation(lastKnownLocation)
                        } else {
                            //ask user to turn on location
                            detectUserLocation()
                        }
                    }
                    //Instantiating the Location request and setting the priority and the interval I need to update the location.
                    //Instantiating the Location request and setting the priority and the interval I need to update the location.
//                    val locationRequest = LocationRequest.create()
//                    locationRequest.interval = 7000
//                    locationRequest.fastestInterval = 5000
//                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//
//                    //instantiating the LocationCallBack
//                    val locationCallback: LocationCallback = object : LocationCallback() {
//                        override fun onLocationResult(locationResult: LocationResult) {
//                            val lastKnownLocation: Location = locationResult.lastLocation
//                            if (lastKnownLocation != null) {
//                                pointToUserCurrentLocation(lastKnownLocation)
//
//                            }
//                        }
//
//                        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
//                            if (!locationAvailability.isLocationAvailable) {
//                                Toast.makeText(
//                                    this@MapsActivity,
//                                    "Location is unavailable",
//                                    Toast.LENGTH_LONG
//                                ).show();
//                            }
//                        }
//                    }
//                    fusedLocationProviderClient.requestLocationUpdates(
//                        locationRequest,
//                        locationCallback,
//                        Looper.getMainLooper()
//                    )
                }
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
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                userLatLng, 17.toFloat()
            )
        )

        if (::userLatLng.isInitialized && userLocationMarker != null) {
            userLocationMarker!!.position = userLatLng
        } else {
            userLocationMarker = mMap.addMarker(
                MarkerOptions()
                    .position(userLatLng).title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
        }
    }
}